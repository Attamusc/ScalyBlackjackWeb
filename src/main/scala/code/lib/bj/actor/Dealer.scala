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

import java.util.Date
import scala.actors.Actor
import bj.hkeeping.Ok
import bj.hkeeping.NotOk
import bj.hkeeping.NotOk
import bj.hkeeping.Ok
import bj.hkeeping.Broke
import bj.card.Shoe
import bj.card.Card
import bj.card.Hand
import scala.collection.mutable.HashMap
import bj.hkeeping.NotOk
import scala.actors.OutputChannel
import bj.table.Table
import bj.util.Log
import bj.util.MessageFactory
import bj.Game

import comet.Conductor

case class Request
case class Hit(pid:Int) extends Request
case class Stay(pid:Int) extends Request
case class DoubleDown(pid:Int) extends Request
case class Split(pid:Int) extends Request
case class Surrender(pid:Int) extends Request
case class Insurance(pid:Int) extends Request

case class Up(card : Card)
case class Observe(card : Card, player : Int, shoeSize : Int)

case class BetType(bid: Int) { 
    override def toString: String = {
        this.bid match {
            case 1 => "Double Down"
            case 2 => "Surrender"
            case 3 => "Insurance"
        }
    }
}

case class Outcome
case class Win(gain : Double) extends Outcome
case class Loose(gain : Double) extends Outcome
case class Push(gain : Double) extends Outcome

/** This object implements the dealer static members */
object Dealer {
  /** Unique id counter */
  var id : Int = -1
}

/**
 * This class implements the dealer.
 * @param tableId The table I'm associated with.
 */
class Dealer(tableId: Int) extends Actor with Hand {  
  /** Unique dealer id */
  Dealer.id += 1
  val did = Dealer.id
  
  /** Mailboxes of players   */
  var players = List[OutputChannel[Any]]()
  
  /** Mailbox for the table */
  var table : OutputChannel[Any] = null
  
  /** Copies of player hands */
  var hands = List[Object with Hand]()
  
  /** Hand-to-pid map */
  var handMap = HashMap[Object with Hand,Int]()
  
  /** pid-to-bet type map for anything other than a regular bet */
  // 1 = Double Down
  // 2 = Surrender
  var betMap = HashMap[Int, Int]()
  
  /** List of pids of players who have insurance */
  var insuredPlayers = List[Int]()
  
  /** Shoe from which cards are dealt */
  var shoe: Shoe = null
  
  /** Current player index */
  var playerIndex: Int = 0
  
  /** Current player index */
  var curPlayer : OutputChannel[Any] = null
  
  /** Current hand in play */
  var curHand : Object with Hand = _
  
  /** My hole (faced down) card */
  var hole : Card = null
  
  // Starts the dealer actor
  start

  /** Converts this instance to a string */
  override def toString : String = "dealer(" + did + ")"  
  
  /** This method receives messages */
  def act {
    loop {
      react {
        // Receives players at the table
        case GameStart(players : List[OutputChannel[Any]]) => 
          Log.debug(this + " received game start w. "+players.size+" player mailboxes")
          Conductor ! MessageFactory.message("dealer", did.toString, "received game start w. %d players".format(players.size))
          gameStart(players,sender)
        
        // Receives hit request from a player
        case Hit(pid) =>
          Log.debug(this+" received HIT from player("+pid+")")
          Conductor ! MessageFactory.message("dealer", did.toString, "received HIT from player(%d)".format(pid))
          hit(pid, Game.findPlayerByPid(pid))          

        // Receives a stay request from a player
        case Stay(pid) =>
          Log.debug(this+" received STAY from player("+pid+")")
          Conductor ! MessageFactory.message("dealer", did.toString, "received STAY from player(%d)".format(pid))
          stay(pid, Game.findPlayerByPid(pid))
          
        // Receives a double down request from the player
        case DoubleDown(pid) =>
            Log.debug(this+" received DOUBLEDOWN from player("+pid+")")
            Conductor ! MessageFactory.message("dealer", did.toString, "received DOUBLEDOWN from player(%d)".format(pid))
            sender ! BetType(1)
            betMap += pid -> 1
            hit(pid, Game.findPlayerByPid(pid))
        
        // Receives an insurance request from the player
        case Insurance(pid) =>
            Log.debug(this+" received INSURANCE from player("+pid+")")
            Conductor ! MessageFactory.message("dealer", did.toString, "received INSURANCE from player(%d)".format(pid))
            sender ! BetType(3)
            insuredPlayers :+= pid 
                
        // Receives a surrender request from the player
        case Surrender(pid) =>
            Log.debug(this+" received SURRENDER from player("+pid+")")
            Conductor ! MessageFactory.message("dealer", did.toString, "received SURRENDER from player(%d)".format(pid))
            sender ! BetType(2)
            betMap += pid -> 2
            stay(pid, Game.findPlayerByPid(pid))
            
        // Receives something completely from left field
        case dontKnow =>
          // Got something we REALLY didn't expect
          Log.debug(this+" got "+dontKnow) 
          Conductor ! MessageFactory.message("dealer", did.toString, "got an unknown value")    
      }
    }
  }
  
  /** Initializes the game */
  def init {
    if(shoe == null) {
        val randSeed = new Date().getTime
    	shoe = new Shoe(randSeed)
	}

    // Deal the up card
    hit(shoe.deal)
    Conductor ! MessageFactory.dealer_update(this.tableId, 0, cards(0).shortSuite, cards(0).shortValue)
    
    // Deal the hole card but don't put it yet into dealer's hand
    hole = shoe.deal

    // Deal all  players their initial cards
    players.foreach(deal)

    // It's the first player's turn
    playerIndex = 0

    curPlayer = players(0)
    
    curHand = hands(0)

    curPlayer ! Up(cards(0))
  }

  /**
   * Starts the game.
   * @param players Player mailboxes of this game
   * @param source Source actor of the message
   */
  def gameStart(players : List[OutputChannel[Any]],source : OutputChannel[Any]) {
    table = source

    this.players = players

    init     
  }
  
  /**
   * Requests hit for the source pid.
   * @param pid Player id
   * @param source Source actor of the message.
   */
  def hit(pid : Int, player : Player) {
    /*
    if (!valid(player)) {
        Log.debug(this + " received a bad player of " + player)
        player ! NotOk
    }

    else {
    */
      val card = shoe.deal

      deal(player,card)

      curHand.hit(card)

      if (curHand.broke) {
        player ! Broke

        moveNext(pid)
      //}
    }    
  }

  /**
   * Requests stay for the source pid.
   * @param pid Player id
   * @param source Source actor of the message.
   */  
  def stay(pid : Int, source : Player) {
    /*if (!valid(source)) {
      Log.debug(this + " received a bad player of " + source)
      source ! NotOk
    }*/
      
    source ! Ok

    moveNext(pid)
  }
  
  /**
   * Moves to the next player at the table.
   * @param pid Player id
   */
  def moveNext(pid : Int) {
    // Save the hand to pid map -- we need later for pay outs
    handMap(curHand) = pid
    
    // Advance the player index
    playerIndex += 1

    // If there are more players, send my up-card which
    // signals the player that its turn begins
    if (playerIndex < players.size) {
      curPlayer = players(playerIndex)
      
      curHand = hands(playerIndex)

      curPlayer ! Up(cards(0))
    }
    else {
      // Close the game with dealer play
      close
      
      // Do the payouts
      payout
    }
    
  }  
  
  /** Closes out the game */
  def close {
    // If no players are left, dealer doesn't have to play
    if(playersLeft == 0)
      return
      
    // Hit dealer's hand with the hole card
    hit(hole)
    
    Log.debug(this+" closing card1 = "+cards(0)+" card2 = "+cards(1))
    Conductor ! MessageFactory.dealer_update(this.tableId, 1, cards(1).shortSuite, cards(1).shortValue)  
    // Conductor ! MessageFactory.update("dealer", did.toString, cards(0).toString)
    // Conductor ! MessageFactory.update("dealer", did.toString, cards(1).toString)
    
    while (value < 17 && !blackjack) {
      val card : Card = shoe.deal
      
      hit(card)
      
      Log.debug(this+" hitting card = "+card+" value = "+value)   
      Conductor ! MessageFactory.dealer_update(this.tableId, cards.size - 1, card.shortSuite, card.shortValue)
    }
    
    if(value > 21)
      Log.debug(this+" BROKE!!")
      // Conductor ! MessageFactory.result("dealer", did.toString, "broke", "0")
    
    // Broadcast observations on all cards dealer's holding
    // except the up-card at index 0 -- players have already
    // seen this card
    for(i <- (0 until players.size)) {
      for(j <- (1 until cards.size)) {
    	  players(i) ! Observe(cards(j),i,shoe.size)   
      }
    }
  }
  
  /** Returns count of players who haven't broken */
  def playersLeft = hands.count(h => !h.broke)
  
  /** Sends pay-out info to the table */
  def payout {
    val pays = HashMap[Int,Outcome]()
    
    for(hand <- hands) {
      val pid = handMap(hand)
      Log.debug(this + " performing a payout to player(" + pid + ")")
      
      // Player looses
      if(hand.broke)
        pays(pid) = Loose(-1.0)
        
      else if(!this.broke && hand.value < this.value)
        pays(pid) = Loose(-1.0)
      
      // Push
      else if(this.value == hand.value)
        pays(pid) = Push(0.0)
        
      // Player wins automatically with blackjack
      else if(hand.blackjack)
        pays(pid) = Win(1.5)
      
      // Player wins if hand value is greater
      else if(hand.value > this.value)
        pays(pid) = Win(1.0)
        
      // Player wins if dealer breaks
      else if(!hand.broke && this.broke)
        pays(pid) = Win(1.0)
    }
    
    table ! GameOver(pays)
    
    /** Since the game is now over, reset everything necessary to start a new game */
    this.hands = List[Object with Hand]()
    this.handMap = HashMap[Object with Hand,Int]()
    this.betMap = HashMap[Int, Int]()
    this.insuredPlayers = List[Int]()
    resetCards
  }  
  
  /**
   * Deals initial cards to a player
   * @param mailbox The player's mailbox
   */
  def deal(player : OutputChannel[Any]): Unit = {
    val card1 = shoe.deal
    val card2 = shoe.deal
    
    Log.debug(this+" dealing "+card1)
    Log.debug(this+" dealing "+card2)
    Conductor ! MessageFactory.message("dealer", did.toString, "dealing %s".format(card1.toString))
    Conductor ! MessageFactory.message("dealer", did.toString, "dealing %s".format(card2.toString))   
    
    deal(player,card1)
    deal(player,card2)
    
    val hand = new Object with Hand
    
    hand.hit(card1)
    hand.hit(card2)
    
    hands = hands ::: List(hand)
  }
  
  /**
   * Sends the destination player a card.
   * @param target Mailbox of the destination player.
   * @param card Card to send
   */
  def deal(player : OutputChannel[Any], card : Card) {
    // Send the card to the player
    player ! card
    
    // Broadcast the card to all observers
    for(i <- (0 until players.size))
      if(players(i) != player)
        players(i) ! Observe(card,i,shoe.size)    
  }  
   
  /**
   * Validates a mailbox
   * @param mailbox The player's mailbox
   */
  def valid(player : OutputChannel[Any]): Boolean = { 
    if(player == null)
      return false
      
//    Log.debug(this+" valid players sz = "+players.size)
    players.find(p => p == player) match {
      case None =>
//        Log.debug(this+" valid got none player")
        return false
      
      case Some(player) =>
//        Log.debug(this+" got some player "+player)
        if(player != curPlayer)
          return false

        return !curHand.broke
    }
  }
}