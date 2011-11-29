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
package bj.card

/**
 * This trait implements a hand
 */
trait Hand {
    /**
     * Cards in this hand
     */
    var cards = List[Card]()
   
    /**
     * Returns true if the hand broke.
     */
    def broke : Boolean = value > 21

    /**
     * Returns true if this hand has blackjack.
     */
    def blackjack: Boolean = {
        if (cards.size != 2)
            return false

        if(cards(0).ace && cards(1).ten)
            return true
        
        if(cards(1).ace && cards(0).ten)
            return true
        
        false
    }
    
    /**
     * Gets the value of the hand
     */
    def value : Int = {
      // Get the count for aces as soft
      var count : Int = softValue
      
      // Count aces
      val aceCount = cards.count(card => card.ace)
      
      // Use ace == 11 if it favors the player
      if(aceCount == 1 && count >= 7 && (count+10) <= 21)
          count += 10

      else if(aceCount > 1 && count+10 <= 21)
          count += 10
      
      count
    }
      
    /**
     * Gets the soft value of the hand.
     */
    def softValue : Int = cards.foldLeft(0)((xs, x) => xs + x.value)
    
    /**
     * Hits this hand.
     */
    def hit(card : Card) : Unit =  cards = cards ::: List(card)
}