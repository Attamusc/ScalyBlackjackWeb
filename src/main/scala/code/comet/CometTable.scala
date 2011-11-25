package code
package comet

import net.liftweb._
import http._
import util._
import Helpers._
import actor._

import lib.bj.util.Log

object TableServer extends LiftActor with ListenerManager {
    private var table_patter : Vector[String] = Vector("Test")
    
    def createUpdate = table_patter
    
    override def lowPriority = {
        case s: String => 
            table_patter :+= s 
            Log.debug("Table Server says: " + s)
            updateListeners()
    } 
}

class CometTable extends CometActor with CometListener{
    private var patter : Vector[String] = Vector()
    
    def registerWith = TableServer

    override def lowPriority = {
        case v: Vector[String] => 
            patter = v
            Log.debug("Comet Table says: " + v)
            reRender()
    }

    def render = "li *" #> patter & ClearClearable
}