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

object BjHelper extends RestHelper {
    serve {
        case "api" :: "bj" :: "bet" :: _ Post _ =>
            val message = "(action: %s, pid: %s, tid: %s, amount: %s)".format("bet", S.param("pid").openOr(""), S.param("tid").openOr(""), S.param("amount").openOr(""))
            Conductor ! message
            JString("Ok")
        case "api" :: "bj" :: player_action :: _ Post _ =>
            val message = "(action: %s, pid: %s, tid: %s)".format(player_action, S.param("pid").openOr(""), S.param("tid").openOr(""))
            Conductor ! message
            JString("Ok")
        case "api" :: "bj" :: "start" :: _ Get _ =>
            Conductor ! "Start"
            JString("Ok")
        case "api" :: "bj" :: "clear" :: _ Get _ =>
            Conductor ! "Clear"
            JString("Ok")
    }
}

