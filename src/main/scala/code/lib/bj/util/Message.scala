package code
package lib
package bj.util

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import collection.mutable.HashMap

case class Message(`type`:String, content:HashMap[String, String]) {
    implicit val formats = DefaultFormats
    
    def toJson = write(this)
}