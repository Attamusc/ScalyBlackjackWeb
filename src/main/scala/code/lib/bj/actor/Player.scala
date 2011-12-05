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
package bj.actor
import scala.actors.Actor
import scala.actors.OutputChannel
import bj.card.Hand
import bj.hkeeping.Ok
import bj.card.Card
import bj.util.Log
import bj.hkeeping.Broke
import bj.util.MessageFactory
import bj.util.BasicStrategy

import collection.mutable.HashMap
import comet.Conductor

/** This object represents the player's class variables */
object Player {
  /** Unique id counter for players */
  var id: Int = -1

  /** Creates, starts, and send the players on their way.  */
  def start(players : List[Player]) {   
    players.foreach { p =>
      p.start
      
      p ! Go
    }
  }
}

/**
 * This class implements the functionality of a player.
 * @param name Name of the player
 * @param bankroll Bankroll of the player to start
 * @param betAmt Minimum amount player will bet
 * @param tableId Table id I've been assigned
 * @param realBoy Whether the player is a real person or not
 */
class Player(name: String, var bankroll: Double, var betAmt: Double, var tableId: Int, realBoy: Boolean) extends Actor with Hand {
  /**
   * Get the player's unique id
   * Note: this assumes players are constructed serially!
   */
  Player.id += 1  
  val pid = Player.id

  /** Dealer's up-card */
  var upcard : Card = _
  
  /** Special bet type I've made, if any */
  var betType : Int = 0
  
  /** If I've asked for an insurance bet */
  var insured : Boolean = false

  /** Pretty-prints the player reference */
  override def toString: String = "(" + name + ", " + pid + ")"

  /** This method receives messages */
  def act {
    loop {
      react {
        // Receives message to tell player to place its bet
        case Go =>
          Log.debug(this+" received Go placing bet = "+betAmt+" from bankroll = "+bankroll)
          Conductor ! MessageFactory.message(name, pid.toString, "received Go placing bet = %d from bankroll = %d".format(betAmt.toInt, bankroll.toInt))
          if(!this.realBoy)
            bet
          
        // Receives the dealer's up-card which is player's cue to play
        case Up(card) =>
          Log.debug(this + " received dealer's up card = " + card)
          Conductor ! MessageFactory.message(name, pid.toString, "received up card = %s".format(card.toString))
          // Only perform the auto-logic if this is a Bot player and not a real player
          if(!this.realBoy)
            play(card)

        // Receives a card from the dealer
        case card: Card =>         
          hitMe(card)
          
        case bet_type: BetType =>
            Log.debug(this + " received dealer's confirmation of bet type of " + bet_type)
            if(bet_type.bid == 3) {
                this.insured = true
            }
            else {
                this.betType = bet_type.bid
            }

        // Receives broke message
        case Broke =>
          Log.debug(this+ " received BROKE")
          Conductor ! MessageFactory.message(name, pid.toString, "received broke")
          
        // Receives message about dealt card
        case Observe(card,player,shoeSize) =>
          Log.debug(this+" observed: "+card)
          Conductor ! MessageFactory.message(name, pid.toString, "observed player with pid %d receive a %s".format(player, card.toString))
          observe(card,player,shoeSize)
          
        // Receives the table number I've been assigned to
        case TableNumber(tid : Int) =>
          Log.debug(this+" received table assignment tid = "+tid)
          Conductor ! MessageFactory.message(name, pid.toString, "received table assignment tid = %d".format(tid))
          assign(tid)
        
        case Win(gain) =>
          val won = betAmt * gain
          
          bankroll += won
          
          Log.debug(this+" received WIN " + won + " new bankroll = "+bankroll)
          Conductor ! MessageFactory.player_result(this.tableId, pid.toString, "win", "%d".format(won.toInt))
          
          resetCards
          
        case Loose(gain) =>
          val lost = betAmt * gain
          
          bankroll += lost
          
          Log.debug(this+" received LOOSE " + lost + " new bankroll = "+bankroll)          
          Conductor ! MessageFactory.player_result(this.tableId, pid.toString, "loss", "%d".format(lost.toInt))
          
          resetCards
          
        case Push(gain) =>
          Log.debug(this+" received PUSH bankroll = "+bankroll)
          Conductor ! MessageFactory.player_result(this.tableId, pid.toString, "push", "0")
          
          resetCards
          
        // Receives an ACK
        case Ok =>
          Log.debug(this + " received Ok")
          Conductor ! MessageFactory.message(name, pid.toString, "received OK")

        // Receives something completely from left field
        case dontKnow =>
          // Got something we REALLY didn't expect
          Log.debug(this+" received unexpected: "+dontKnow)
          Conductor ! MessageFactory.message(name, pid.toString, "received an unexpected value")
      }
    }

  }

  /**
   * Processes table numbers.
   * @param tid Table id
   */
  def assign(tid : Int) {

  }
  
  /**
   * Observes a card being dealt.
   * Note: This method needs to be overridden if counting cards.
   * @param card Card the player received 
   * @param player Player id receiving this card
   * @param size Shoe size
   */
  def observe(card : Card, player : Int, size : Int) {
    
  }
  
  /**
   * Processes hit request.
   * @param dealer Dealer's mailbox
   * @param upcard Dealer's up-card
   */  
  def hitMe(card : Card) {
    // Hit my hand with this card
    this.hit(card)
    
    Log.debug(this + " received card " + card + " hand sz = " + cards.size + " value = " + value)
    Conductor ! MessageFactory.player_update(this.tableId, pid.toString, cards.size - 1, card.shortSuite, card.shortValue)
    Conductor ! MessageFactory.message(name, pid.toString, " received card %s hand sz = %d value = %d".format(card.toString, cards.size, value))

    // If I'm a bot and I've received more than two cards, the extras must be in
    // response to my requests
    if (!this.realBoy && cards.size > 2 && !this.broke)
      play(this.upcard)    
  }
  
  /** Places a bet with the house */
  def bet {
    if (bankroll < betAmt)
      return

    House ! Bet(pid, betAmt, tableId)
  }
  
  /**
   * Processes the dealer's upcard.
   * @param dealer Dealer's mailbox
   * @param upcard Dealer's up-card
   */
  def play(upcard : Card) {
    this.upcard = upcard
    
    // Wait for a few seconds, to give the appearance of being a real player
    Thread.sleep(3000)

    // Compute my play strategy
    // If I performed a Double Down, then I must stay, so don't even both diving into the analyze function
    val request = if(betType != 1) analyze(upcard) else Stay(pid)

    Log.debug(this + " request = " + request)
    Conductor ! MessageFactory.message(name, pid.toString, "request: " + request)

    // Don't send a request if we break since
    // deal will have moved on
    if(!this.broke)
    	sender ! request    
    
  }

  /** Analyzes my best play using the Basic Strategy. */
  def analyze(upcard : Card) : Request = {
      val upCardKey = if(upcard.value != 1) upcard.value.toString else "A"
      
      // Check my hand against the basic strategy
      if(cards.size == 2) {
          // Do I have a pair? NOTE: We us number instead of value to make sure the face cards are the same
          if(cards(0).number == cards(1).number) {
              val shortValue = if(cards(0).number == 1) "A" else cards(0).value
              return BasicStrategy.action(this.pid, "%d-%d".format(shortValue, shortValue), upCardKey)
          }
          // If I don't, check if I have an Ace
          else if(cards(0).value == 1 || cards(1).value == 1) {
              return BasicStrategy.action(this.pid, "A-%d".format(if(cards(0).value == 1) cards(1).value else cards(0).value), upCardKey)
          }
      } 
      
      // If we have more than two cards or we have neither a pair nor an ace, we just use the value as defined in the Basic Strategy
      return BasicStrategy.action(this.pid, value.toString, upCardKey)
      
    /*
    // If my hand >= 17, we're staying
    if (value >= 17)
      return Stay(pid)

    // If I have ten or less, no harm in hitting
    if (value <= 10)
      return Hit(pid)

    // If the dealer can bust, we're staying
    if (upcard.value >= 2 && upcard.value <= 6)
      return Stay(pid)

    // Dealer must be showing, A, 7, 8, 9, or 10 so...
    // we must hit
    return Hit(pid)
    */
  }
  
  def setAndProcessBet(newBet: Int, tableId: Int) = {
      this.betAmt = newBet.toDouble
      this.tableId = tableId
      bet
  }
  
  def sendRequest(dealer: Dealer, request: Request) = {
      Log.debug(this + " has been informed that it's remote player wants to send " + request + " to " + dealer)
      dealer ! request
  }

}