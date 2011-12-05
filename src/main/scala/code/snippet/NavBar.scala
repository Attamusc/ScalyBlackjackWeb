package code 
package snippet 

import scala.xml.{NodeSeq, Text}
import code.lib._
import lib._
import net.liftweb._
import http._
import common._
import util.Helpers._

import model.User

class NavBar {
  	def navGen = {
		User.loggedIn_? match {
			case false => "*" #> <lift:embed what="base_nav" />
			case true => "*" #> <lift:embed what="logged_in_nav" />
			case _ => "*" #> <div>Whoops!!!</div>
		}
	}
}