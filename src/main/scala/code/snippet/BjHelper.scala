package code 
package snippet

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST._
import net.liftweb.json._
import net.liftweb.common.{Box,Full,Empty,Failure,ParamFailure}
import net.liftweb.mapper._

import lib.bj.Game

object BjHelper extends RestHelper {
    serve {
        case "api" :: "bj" :: _ Get _ =>
            Game.init
            RedirectResponse("/")
    }
}

