package code 
package snippet 

import scala.xml.{NodeSeq, Text}
import net.liftweb.common.{Box,Full,Empty,Failure,ParamFailure}
import code.lib._
import lib._
import net.liftweb._
import http._
import common._
import util.Helpers._

import model.User

object CurrentPlayer {
	//var playerName = User.currentUser().firstName
}

class PlayerUtils {
	def getPlayerName = {
	    // This will only ever get called when an actual user is logged in, sooooo....
		"*" #> User.currentUser.openOr(new User()).firstName
	}
}