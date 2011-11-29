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

import bj.actor.Player
import bj.actor.House
import bj.actor.Dealer
import scala.actors.Actor
import bj.actor.Go
import bj.util.Log

import comet.TableServer

case class Done
case class Launch

object Game {
  
  def init = {
    Log.debug("starting the house")
    TableServer ! "starting the house"
    House.start
    
    Thread.sleep(1000)
    
    Log.debug("starting players")
    TableServer ! "starting players"
    val players = List[Player](new Player("Ron", 100, 30))
    
    Player.start(players)
    
    Thread.sleep(1000)
    
    Log.debug("telling house go")
    TableServer ! "telling house go"
    House ! Go
  }
}