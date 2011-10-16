package code 
package snippet 

import scala.xml.{NodeSeq, Text}
import code.lib._
import lib._
import net.liftweb._
import http._
import common._
import util.Helpers._

object Logger extends Logger

class TableUtils {
  	def getTableName = {
		var tableId = S.param("tableId").openOr("0")
		Logger.debug("The requested table has the id " + tableId)
		tableId match {
			case "1" => "*" #> "Table A"
			case "2" => "*" #> "Table B"
			case "3" => "*" #> "Table C"
			case "4" => "*" #> "Table D"
			case _ => "*" #> "Table"
		}
  	}
}