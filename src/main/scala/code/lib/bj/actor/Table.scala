//Copyright (C) 2011 Ron Coleman. Contact: ronncoleman@gmail.com
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either
//version 3 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this library; if not, write to the Free Software
//Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
package code
package lib
package bj.table
import bj.actor.Dealer
import bj.actor.Player
import scala.collection.mutable.HashMap
import scala.actors.Actor
import scala.actors.OutputChannel
import bj.hkeeping.NotOk
import bj.hkeeping.Ok
import bj.hkeeping.Reply
import bj.actor.Go
import bj.actor.Arrive
import bj.util.Log
import bj.actor.Outcome
import bj.actor.Win
import bj.actor.Loose
import bj.actor.Push
import bj.actor.GameStart
import bj.actor.Bet
import bj.actor.GameOver
import bj.util.MessageFactory

import comet.Conductor

/** This class implements the table static members */
object Table  {
  val MIN_PLAYERS: Int = 1
  val MAX_PLAYERS: Int = 6
  
  var id : Int = -1  
}

/**
 * This class implement table instances.
 * @param minBet Minimum bet for the table.
 */
class Table(val minBet: Double) extends Actor {
  /** Table's id */
  Table.id += 1  
  val tid = Table.id
  
  /** Dealer for this table */
  var dealer = new Dealer

  /** True if this table is involved in a game */
  var trucking: Boolean = false

  /** Bet amounts by player id */
  var bets = HashMap[Int, Double]()

  /** Mailboxes by player id */
  var players = HashMap[Int, OutputChannel[Any]]()
  
  /** House mail box */
  var house : OutputChannel[Any] = null
  
  /** Starts the table */
  start

  /** Gives a string version of the table */
  override def toString : String = "table(" + tid + ", " + minBet + ")"
  
  /** This method receives messages */
  def act {
    loop {
      react {
        // Receives arrival of a player: mailbox is the player's
        case Arrive(mailbox : OutputChannel[Any], pid : Int, betAmt : Double) =>
          Log.debug(this+" received ARRIVE from "+mailbox+" amt = "+betAmt)
          Conductor ! MessageFactory.message("table", tid.toString, "received ARRIVE from %s amt = %d".format(mailbox.toString, betAmt.toInt))
          
          arrive(mailbox,pid,betAmt)

        // Receive game over signal from the dealer
        case GameOver(pays : HashMap[Int,Outcome]) =>
          Log.debug(this + " received game over for "+pays.size+" players")
          Conductor ! MessageFactory.message("table", tid.toString, "received game over for %d players".format(pays.size))
          gameOver(pays)
         
        // Receives game start signal from the house
        case Go =>
          Log.debug(this+" received Go for " + players.size + " players")
          Conductor ! MessageFactory.message("table", tid.toString, "received Go for %d players".format(players.size))
          go

      }
    }
  }
  
  /**
   * Processes a player arrival
   * @param source Player's mailbox
   * @param pid Player's id
   * @param betAmt Player's bet amount
   */
  def arrive(source : OutputChannel[Any], pid : Int, betAmt : Double) {
    val reply = placed(source, Bet(pid, betAmt))

    Log.debug(this + " bet = " + reply)
    Conductor ! MessageFactory.message("table", tid.toString, " bet = %s".format(reply.toString))

    source ! reply    
  }
  
  /** Handles game start */
  def go {
    val bettors = players.foldLeft(List[OutputChannel[Any]]())((xs, x) => xs ::: List(x._2))

    if (bettors.size != 0) {

      Log.debug(this + " dealing " + bettors.size + " bettors")
      Conductor ! MessageFactory.message("table", tid.toString, "dealing %d bettors".format(bettors.size))

      dealer ! GameStart(bettors)
    }    
  }
  
  /**
   * Places the bet after validation.
   * @param mailbox Player's mailbox
   * @param bet Bet parameters
   */
  def placed(mailbox : OutputChannel[Any], bet : Bet) : Reply = {
    Log.debug("table: placing bet amt = "+bet.amt+" num bets = "+bets.size)
    Conductor ! MessageFactory.message("table", tid.toString, "placing bet amt = %d num bets = %d".format(bet.amt.toInt, bets.size))
    if(bet.amt <= 0 || bets.size >= Table.MAX_PLAYERS)
      return NotOk
            
    players.get(bet.player) match {
      case None =>
        Log.debug("table: adding new player id = "+bet.player)
        Conductor ! MessageFactory.message("table", tid.toString, "adding new player id = %d".format(bet.player))
        players += bet.player -> mailbox

        bets += bet.player -> bet.amt

      case Some(player) =>
        bets.get(bet.player) match {
          case Some(oldAmt) =>
            Log.debug("table: updating bet for player id = "+bet.player)
            Conductor ! MessageFactory.message("table", tid.toString, "updating bet for player id = %d".format(bet.player))
            bets(bet.player) = (oldAmt + bet.amt)

          case None =>
            Log.debug("table: got bad bet")
            Conductor ! MessageFactory.message("table", tid.toString, "got a bed bet")
                
            return NotOk
        }
    }
    
    Ok
  }
  
  /** Handles game over */
  def gameOver(pays : HashMap[Int,Outcome]) = pays.foreach(p => pay(p))
  
  /**
   * Sends payment to player.
   * @param pid Player id
   * @parm outcome Game outcome for player pid
   */
  def pay(figure : (Int,Outcome)) : Unit = {
    val (pid, outcome) = figure
    
    outcome match {
      case Win(gain) =>
        Log.debug("player(" + pid + ") won " + gain)
        Conductor ! MessageFactory.message("table", tid.toString, "player(%d) won %d".format(pid, gain.toInt))
        players(pid) ! outcome

      case Loose(gain) =>
        Log.debug("player(" + pid + ") lost " + gain)
        Conductor ! MessageFactory.message("table", tid.toString, "player(%d) lost %d".format(pid, gain.toInt))
        players(pid) ! outcome

      case Push(gain) =>
        Log.debug("player(" + pid + ") push " + gain)
        Conductor ! MessageFactory.message("table", tid.toString, "player(%d) push %d".format(pid, gain.toInt))
        players(pid) ! outcome
    }
  } 
  
  /** Clears all the bets -- NOT USED */
  def clear: Unit = bets.clear
}