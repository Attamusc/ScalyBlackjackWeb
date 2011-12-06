package code
package comet

import scala.util.matching.Regex

import net.liftweb._
import http._
import util._
import Helpers._
import actor._
import js._
import JsCmds._
import JE._
import net.liftweb.json._
import net.liftweb.json.Serialization.write

import lib.bj.Game
import lib.bj.actor.House
import lib.bj.actor.Hit
import lib.bj.actor.Stay
import lib.bj.actor.Split
import lib.bj.actor.DoubleDown
import lib.bj.actor.Insurance
import lib.bj.actor.Surrender
import lib.bj.util.Log
import lib.bj.util.BaseStatus
import lib.bj.util.BaseMessage
import lib.bj.util.Update

import model.User

import snippet.TableSession

case class PlayerAction(val action: String, val pid: Int, val tid: Int)
case class PlayerBet(val pid: Int, val tid: Int, val amount: Int)

object Conductor extends LiftActor with ListenerManager {
    private var table_patter : Vector[String] = Vector()
    private var patter_update : BaseMessage = new BaseMessage(-1, new Update())
    private var game_started = false
    
    def createUpdate = {
        patter_update
        //table_patter
    }
    
    override def lowPriority = {
        case "Start" =>
            game_started = true
            Game.init
        case "Clear" =>
            game_started = false
        case s: String => 
            //table_patter :+= s
            //Log.debug("Table Server says: " + s)
            updateListeners()
        case bet: PlayerBet =>
            Log.debug("(action: bet, pid: %s, tid: %s, amount: %s)".format(bet.pid, bet.tid, bet.amount))
            Game.placeBet(bet.pid.toInt, bet.tid.toInt, bet.amount.toInt)
        case action : PlayerAction =>
            Log.debug("(action: %s, pid: %s, tid: %s)".format(action.action, action.pid, action.tid))
            val dealer = House.getDealerForTable(action.tid)
            action.action match {
                case "hit" => Game.informPlayerOfAction(dealer, action.pid, Hit(action.pid))
                case "stay" => Game.informPlayerOfAction(dealer, action.pid, Stay(action.pid))
                case "double_down" => Game.informPlayerOfAction(dealer, action.pid, DoubleDown(action.pid))
                case "split" => Game.informPlayerOfAction(dealer, action.pid, Split(action.pid))
                case "surrender" => Game.informPlayerOfAction(dealer, action.pid, Surrender(action.pid))
                case "insurance" => Game.informPlayerOfAction(dealer, action.pid, Insurance(action.pid))
            }
        case message: BaseStatus =>
            //patter_update = message
            //updateListeners()
        case message: BaseMessage =>
            //table_patter :+= message.toJson
            patter_update = message
            updateListeners()
    } 
}

class Dispatcher extends CometActor with CometListener{
    private var patter : Vector[String] = Vector()
    private var update : String = "This is a test"
    private val tableIdRegex = new Regex("""(\d+)-\d+""")
    
    def registerWith = Conductor

    override def lowPriority = {
        case message : BaseMessage =>
            val tableIdRegex(tid) = this.name.openOr("")
            if(message.tid.toString == tid) {
                patter :+= message.message.toJson
                Log.debug("Dispatcher(" + this.name.openOr("") + ") says: '" + message.message.toJson + "'")
                partialUpdate(JsRaw("CASINO.attendant.process_message('" + message.message.toJson + "')"))
            }
        case v: Vector[String] => 
            patter = v
            //Log.debug("Comet Table says: " + v)
            //reRender()
            //partialUpdate(JsRaw("CASINO.attendant.process_message('" + patter + "')"))
    }

    def render = {
        "li *" #> patter & ClearClearable
    }
}