package code 
package snippet

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST._
import net.liftweb.common.{Box,Full,Empty,Failure,ParamFailure}
import net.liftweb.mapper._
import js._
import JsCmds._
import JE._

import lib.bj.Game
import comet.Conductor
import comet.PlayerAction
import comet.PlayerBet

object BjHelper extends RestHelper {
    serve {
        case "api" :: "bj" :: "bet" :: _ Post _ =>
            Conductor ! new PlayerBet(S.param("pid").openOr("").toInt, S.param("tid").openOr("").toInt, S.param("amount").openOr("").toInt)
            JString("Ok")
        case "api" :: "bj" :: player_action :: _ Post _ =>
            Conductor ! new PlayerAction(player_action, S.param("pid").openOr("").toInt, S.param("tid").openOr("").toInt)
            JString("Ok")
        case "api" :: "bj" :: "start" :: _ Get _ =>
            Conductor ! "Start"
            JString("Ok")
        case "api" :: "bj" :: "clear" :: _ Get _ =>
            Conductor ! "Clear"
            JString("Ok")
    }
}

