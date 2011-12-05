package code
package comet

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
import lib.bj.util.BaseMessage

case class PlayerAction(val action: String, val pid: Int, val tid: Int)
case class PlayerBet(val pid: Int, val tid: Int, val amount: Int)

object Conductor extends LiftActor with ListenerManager {
    private var table_patter : Vector[String] = Vector()
    private var patter_update : String = ""
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
        case action : PlayerAction =>
            Log.debug("(action: %s, pid: %s, tid: %s)".format(action.action, action.pid, action.tid))
            val dealer = House.getDealerForTable(action.tid)
            action.action match {
                case "hit" => dealer ! Hit(action.pid)
                case "stay" => dealer ! Stay(action.pid)
                case "double_down" => dealer ! DoubleDown(action.pid)
                case "split" => dealer ! Split(action.pid)
                case "surrender" => dealer ! Surrender(action.pid)
                case "insurance" => dealer ! Insurance(action.pid)
            }
        case message: BaseMessage =>
            //table_patter :+= message.toJson
            patter_update = message.toJson
            updateListeners()
    } 
}

class Dispatcher extends CometActor with CometListener{
    private var patter : Vector[String] = Vector()
    private var update : String = "This is a test"
    
    def registerWith = Conductor

    override def lowPriority = {
        case s : String =>
            patter :+= s
            Log.debug("Comet Table says: '" + s + "'")
            partialUpdate(JsRaw("CASINO.attendant.process_message('" + s + "')"))
            //partialUpdate(JsRaw("CASINO.attendant.process_message('" + s + "')"))
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