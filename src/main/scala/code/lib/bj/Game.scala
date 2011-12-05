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
package bj

import collection.mutable.HashMap

import bj.actor.Player
import bj.actor.House
import bj.actor.Dealer
import scala.actors.Actor
import bj.actor.Go
import bj.util.Log
import bj.util.MessageFactory

import comet.Conductor

case class Done
case class Launch

object Game {
    
  private val botNames : Vector[String] = Vector("RonBot", "John", "Russell", "George", "April", "Steve", "Phillip", "Agnes", "Beatrice", "Eugene", "Joanne", "Blanche", "Maryann", "Jodi", "Timothy", "Bruce", "Justin", "Keith", "William", "Richard")
  
  def init = {
    Log.debug("Starting the House")
    Conductor ! MessageFactory.info("Starting the House")
    House.start
    
    Thread.sleep(1000)
    
    Log.debug("Starting Players")
    Conductor ! MessageFactory.info("Starting Players")
    val players = List[Player](new Player("Sean", 10000, 100, 0, true), new Player("Gaby", 10000, 100, 0, false), new Player("RonBot", 10000, 100, 0, false))
    
    Player.start(players)
    
    Thread.sleep(1000)
    
    Log.debug("Telling the House Go")
    Conductor ! MessageFactory.info("Telling the House Go")
    House ! Go
  }
  
  def addPlayerToTable(name: String, tid: Int) = {
      
  }
  
  private def assignBotsToTables = {
      
  }
}