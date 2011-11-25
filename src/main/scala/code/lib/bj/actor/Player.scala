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
 */
class Player(name: String, var bankroll: Double, betAmt: Double) extends Actor with Hand {
  /**
   * Get the player's unique id
   * Note: this assumes players are constructed serially!
   */
  Player.id += 1  
  val pid = Player.id

  /** Dealer's up-card */
  var upcard : Card = _

  /** Table id I've been assigned */
  var tableId : Int = -1

  /** Pretty-prints the player reference */
  override def toString: String = "(" + name + ", " + pid + ")"

  /** This method receives messages */
  def act {
    loop {
      react {
        // Receives message to tell player to place its bet
        case Go =>
          Log.debug(this+" received Go placing bet = "+betAmt+" from bankroll = "+bankroll)
          bet
          
        // Receives the dealer's up-card which is player's cue to play
        case Up(card) =>
          Log.debug(this + " received dealer's up card = " + card)

          play(card)

        // Receives a card from the dealer
        case card: Card =>         
          hitMe(card)

        // Receives broke message
        case Broke =>
          Log.debug(this+ " received BROKE")
          
        // Receives message about dealt card
        case Observe(card,player,shoeSize) =>
          Log.debug(this+" observed: "+card)
          observe(card,player,shoeSize)
          
        // Receives the table number I've been assigned to
        case TableNumber(tid : Int) =>
          Log.debug(this+" received table assignment tid = "+tid)
          assign(tid)
        
        case Win(gain) =>
          val won = betAmt * gain
          
          bankroll += won
          
          Log.debug(this+" received WIN " + won + " new bankroll = "+bankroll)
          
        case Loose(gain) =>
          val lost = betAmt * gain
          
          bankroll += lost
          
          Log.debug(this+" received LOOSE " + lost + " new bankroll = "+bankroll)          
          
        case Push(gain) =>
          Log.debug(this+" received PUSH bankroll = "+bankroll)
          
        // Receives an ACK
        case Ok =>
          Log.debug(this + " received Ok")

        // Receives something completely from left field
        case dontKnow =>
          // Got something we REALLY didn't expect
          Log.debug(this+" received unexpected: "+dontKnow)
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

    // If I've received more than two cards, the extras must be in
    // response to my requests
    if (cards.size > 2)
      play(this.upcard)    
  }
  
  /** Places a bet with the house */
  def bet {
    if (bankroll < betAmt)
      return

    House ! Bet(pid, betAmt)
  }
  
  /**
   * Processes the dealer's upcard.
   * @param dealer Dealer's mailbox
   * @param upcard Dealer's up-card
   */
  def play(upcard : Card) {
    this.upcard = upcard

    // Compute my play strategy
    val request = analyze(upcard)

    Log.debug(this + " request = " + request)

    // Don't send a request if we break since
    // deal will have moved on
    if(!this.broke)
    	sender ! request    
    
  }

  /** Analyzes my best play using a condensed form of Basic Strategy. */
  def analyze(upcard : Card) : Request = {
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
  }

}