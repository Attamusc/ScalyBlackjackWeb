package code 
package snippet

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST._
import net.liftweb.json._
import net.liftweb.common.{Box,Full,Empty,Failure,ParamFailure}
import net.liftweb.mapper._

object Session {
	var isLoggedIn = false
}

object SessionHelper extends RestHelper {
	serve {
		case "login" :: _ Post _ => 
			Session.isLoggedIn = true
			//CurrentPlayer.playerName = S.param("email").openOr("")
			RedirectResponse("/") 
		case "logout" :: _ Get _ => 
			Session.isLoggedIn = false
			//CurrentPlayer.playerName = ""
			RedirectResponse("/")
	}
}