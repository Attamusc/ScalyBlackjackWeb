package code
package lib
package bj.util

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import collection.mutable.HashMap

import bj.actor.PlayerInfo
import bj.card.CardInfo

object MessageFactory {
    def info(message:String) = new Info("info", message)
    def message(sender:String, id:String, message:String) = new Message("message", sender, id, message)
    
    def join_table(tid: Int, player: PlayerInfo) = new BaseMessage(tid, new JoinTable("join_table", tid, player))
    def leave_table(tid: Int, pid: Int) = new BaseMessage(tid, new LeaveTable("leave_table", tid, pid))
    def deal_card(tid: Int, pid:Int, card: CardInfo) = new BaseMessage(tid, new DealCard("deal_card", tid, pid, card))
    def game_result(tid: Int, pid:Int, status:String, payout:Int) = new BaseMessage(tid, new GameResult("game_result", tid, pid, status, payout))
    def end_game(tid: Int) = new BaseMessage(tid, new EndGame("end_game", tid))
    def new_game(tid: Int) = new BaseMessage(tid, new NewGame("new_game", tid))
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

// Join Table message: join_table
// {table_id: integer, player: {id: integer, dealer: false, a_real_boy: true|false, client_user: true|false, name: string, avatar: string, chips: integer}}
case class JoinTable(`type` : String, table_id: Int, player: PlayerInfo) extends Update

// Leave Table message: leave_table
// {player_id: integer, table_id: integer}
case class LeaveTable(`type` : String, table_id: Int, player_id: Int) extends Update

// Deal Card message: deal_card
// {table_id: integer, player_id: integer, card: {card_number: integer, suit: string, value: string}}
case class DealCard(`type` : String, table_id: Int, player_id: Int, card: CardInfo) extends Update

// Game Result message: game_result
// {table_id: integer, player_id: integer, status: String, payout: integer}
case class GameResult(`type` : String, table_id: Int, player_id: Int, status: String, payout: Int) extends Update

// End Game message: end_game
// {table_id: integer }
case class EndGame(`type` : String, table_id: Int) extends Update

// New Game message: new_game
// {table_id: integer }
case class NewGame(`type` : String, table_id: Int) extends Update

case class BaseMessage(tid: Int, message: Update)