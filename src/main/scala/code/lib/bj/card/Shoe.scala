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

/** This class implements the shoe from which we deal cards. */
class Shoe(seed : Long, id: Int) {
    /** Number of decks in the shoe */
    val DECK_COUNT = 6

    /** Our random number generator for shuffling, etc. */
    val rnd = new java.util.Random(seed + (id * 100))
    
    /** Burn card at this index */
    var burnIndex : Int = _

    /** The shoe */
    var shoe = List[Card]()

    create
    
    /** Gets the shoe size */
    def size : Int = shoe.size
    
    /**
     * Deals a card from the deck
     */
    def deal: Card = {
        if(size == burnIndex) {
            create
        }
        
        val card = shoe(0)

        shoe = shoe.drop(1)

        card
    }
    
    /** Creates the shoe */
    def create {
      build
      
      shuffle
      
      burnIndex = shoe.size - rnd.nextInt(52)
    }
    
    /** Builds s shuffled deck */
    def build: Unit = {
        var deck = List[Card]()
        
        for (d <- 1.to(DECK_COUNT))
            for (code <- Card.ACE.to(Card.KING))
                for (suite <- Card.SPADE.to(Card.HEART))
                    shoe = shoe ::: List(Card(code, suite))
    }
    
    /** Shuffles the deck */
    def shuffle: Unit = {
        // Copy the deck into an array since Fisher-Yates
        // only works with an array
        val array = new Array[Card](shoe.size)

        var i: Int = 0

        for (card <- shoe) {
            array(i) = card

            i = i + 1
        }

        // Fisher-Yates shuffle the array
        shuffle(array)

        // Copy the shuffled array back to the desk
        shoe = List[Card]()

        for (card <- array)
            shoe = card :: shoe

    }

  /**
   * Shuffles using Fisher-Yates
   * http://stackoverflow.com/questions/1259223/how-to-use-java-collections-shuffle-on-a-scala-array
   * Fisher-Yates shuffle, see: http://en.wikipedia.org/wiki/Fisher–Yates_shuffle
   */
  private def shuffle[T](array: Array[T]): Array[T] = {
    for (n <- Iterator.range(array.length - 1, 0, -1)) {
      val k = rnd.nextInt(n + 1)
      val t = array(k); array(k) = array(n); array(n) = t
    }

    array
  }
}