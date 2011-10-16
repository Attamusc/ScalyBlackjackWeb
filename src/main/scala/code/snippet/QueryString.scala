package code 
package snippet 

import lib._

import net.liftweb._
import http._
import util.Helpers._
import common._
import java.util.Date

class QueryString {
  // replace the contents of the element with id "time" with the date
  def display = "*" #> S.param("tableId").openOr("fail over table id")

  /*
   lazy val date: Date = DependencyFactory.time.vend // create the date via factory

   def howdy = "#time *" #> date.toString
   */
}

