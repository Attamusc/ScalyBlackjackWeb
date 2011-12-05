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
import lib.bj.util.Log
import lib.bj.util.BaseMessage

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