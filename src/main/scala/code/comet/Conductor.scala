package code
package comet

import net.liftweb._
import http._
import util._
import Helpers._
import actor._
import net.liftweb.json._
import net.liftweb.json.Serialization.write

import lib.bj.Game
import lib.bj.util.Log
import lib.bj.util.BaseMessage

object Conductor extends LiftActor with ListenerManager {
    private var table_patter : Vector[String] = Vector()
    private var game_started = false
    
    def createUpdate = table_patter
    
    override def lowPriority = {
        case "Start" =>
            game_started = true
            Game.init
        case s: String => 
            table_patter :+= s 
            //Log.debug("Table Server says: " + s)
            updateListeners()
        case message: BaseMessage =>
            table_patter :+= message.toJson
            updateListeners()
    } 
}

class Dispatcher extends CometActor with CometListener{
    private var patter : Vector[String] = Vector()
    
    def registerWith = Conductor

    override def lowPriority = {
        case v: Vector[String] => 
            patter = v
            //Log.debug("Comet Table says: " + v)
            reRender()
            //partialUpdate(JsRaw("CASINO.attendant.process_message('" + patter + "')"))
    }

    def render = "li *" #> patter & ClearClearable
}