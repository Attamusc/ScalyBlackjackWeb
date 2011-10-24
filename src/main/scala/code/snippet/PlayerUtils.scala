package code 
package snippet 

import scala.xml.{NodeSeq, Text}
import code.lib._
import lib._
import net.liftweb._
import http._
import common._
import util.Helpers._

object CurrentPlayer {
	var playerName = ""
}

class PlayerUtils {
	def getPlayerName = {
		"*" #> (if(CurrentPlayer.playerName == "") "Player" else CurrentPlayer.playerName)
	}
}