package code
package lib
package bj.util

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import collection.mutable.HashMap

object MessageFactory {
    def info(message:String) = new Info("info", message)
    def message(sender:String, id:String, message:String) = new Message("message", sender, id, message)
    def dealer_update(tid: Int, cardNum: Int, suit: String, value: String) = new DealerMessage(tid, new CardUpdate("update", "dealer", "-1", cardNum, suit, value))
    def player_update(tid: Int, pid:String, cardNum: Int, suit:String, value: String) = new PlayerMessage(tid, new CardUpdate("update", "player", pid, cardNum, suit, value))
    def player_result(tid: Int, pid:String, status:String, payout:String) = new PlayerMessage(tid, new ResultUpdate("result", "player", pid, status, payout))
}

case class BaseStatus {
    implicit val formats = DefaultFormats
    
    def toJson = write(this)
}

case class Info(`type`:String, message:String) extends BaseStatus

case class Message(`type`:String, sender:String, id:String, message:String) extends BaseStatus

case class Update {
    implicit val formats = DefaultFormats
    
    def toJson = write(this)
}

// {type: update, sender: player|dealer, id: player_id|-1, suit: String, value: Int}
case class CardUpdate(`type` : String, sender:String, id:String, cardNum: Int, suit: String, value: String) extends Update

// {type: result, sender: player|dealer, id: player_id|-1, status:String, payout:String}
case class ResultUpdate(`type`:String, sender:String, id:String, status:String, payout:String) extends Update

case class BaseMessage
case class PlayerMessage(tid: Int, message: Update) extends BaseMessage
case class DealerMessage(tid: Int, message: Update) extends BaseMessage