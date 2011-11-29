package code 
package snippet 

import scala.xml.{NodeSeq, Text}
import code.lib._
import lib._
import net.liftweb._
import http._
import common._
import util.Helpers._
import js._
import JsCmds._
import JE._

import comet.TableServer

object Logger extends Logger

object TableInput {
    def render = SHtml.onSubmit(s => {
        TableServer ! s
        SetValById("chat_in", "")
    })
}

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
	def getTableMinBet = {
		var tableId = S.param("tableId").openOr("0")
		Logger.debug("The requested table has the id " + tableId)
		tableId match {
			case "1" => "*" #> "50"
			case "2" => "*" #> "100"
			case "3" => "*" #> "250"
			case "4" => "*" #> "500"
			case _ => "*" #> "50"
		}
  	}
	def joinTableLink = {
		var tableId = S.param("tableId").openOr("0")
		Logger.debug("The requested table has the id " + tableId)
		"* *" #> <a id="join_table_link" href={"/tables/" + tableId + "/game"}>Join Table</a>
	}
	def chipsSubmitForm = {
		"*" #> <form action={"/tables/" + S.param("tableId").openOr("0")} method="post" class="form-stacked">
	        		<div class="modal-body row">
			          <fieldset class="span5">
			            <div class="clearfix">
			              <label for="convert_cash_amount">Number of Chips</label>
			              <div class="input">
			                <input type="text" id="convert_cash_amount" name="convert_cash_amount" data-show-convert="target_cash_amount" data-rate="0.1" data-describe="${data} cash" />
			                <span class="help-block">Current Rate $<b id="table_chip-rate">100 = 1000</b> chips</span>
			              </div>
			            </div>
			          </fieldset>
			          <div class="span3">
			            <div class="clearfix">
			              You get: <strong><span id="target_cash_amount">$0 cash</span></strong>
			            </div>
			          </div>
			        </div>
			        <div class="modal-footer row">
			          <input type="button" class="btn secondary cancel" value="Close" />
			          <input type="submit" value="Cash Out" class="btn primary" />
			        </div>
		      </form>
	}
	def cashSubmitForm = {
		"*" #>  <form action={"/tables/" + S.param("tableId").openOr("0") + "/game"} method="post" class="form-stacked">
			        <div class="modal-body row">
			          <fieldset class="span5">
			            <div class="clearfix">
			              <label for="convert_chip_amount">Amount in Wallet</label>
			              <div class="input">
			                <input type="text" id="convert_chip_amount" name="convert_chip_amount" data-show-convert="target_chip_amount" data-rate="10" data-describe="{data} chips" />
			                <span class="help-block">Current Rate $<b id="table_chip-rate">100 = 1000</b> chips</span>
			              </div>
			            </div>
			          </fieldset>
			          <div class="span3">
			            You get: <strong><span id="target_chip_amount">0 chips</span></strong>
			          </div>
			        </div>
			        <div class="modal-footer">
			          <input type="button" class="btn secondary cancel" value="Close" />
			          <input type="submit" value="Sit" class="btn primary" />
			        </div>
		        </form>
	}
}
