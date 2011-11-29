package code
package lib
package bj.util

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import collection.mutable.HashMap

object MessageFactory {
    def info(message:String) = new Info("info", message)
    def message(sender:String, id:String, message:String) = new Message("message", sender, id, message)
    def update(sender:String, id:String, card:String) = new Update("update", sender, id, card)
    def result(sender:String, id:String, status:String, payout:String) = new Result("result", sender, id, status, payout)
}

case class BaseMessage {
    implicit val formats = DefaultFormats
    
    def toJson = write(this)
}

case class Info(`type`:String, message:String) extends BaseMessage

case class Message(`type`:String, sender:String, id:String, message:String) extends BaseMessage

case class Update(`type`:String, sender:String, id:String, card:String) extends BaseMessage

case class Result(`type`:String, sender:String, id:String, status:String, payout:String) extends BaseMessage