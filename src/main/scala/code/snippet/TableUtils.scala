package code 
package snippet 

import java.util.Date
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
import bj.actor.House
import bj.Game
import bj.util.Log

import comet.Conductor

object Logger extends Logger
object TableSession {
    object tid extends SessionVar[String]("")
}

object TableInput {
    def render = SHtml.onSubmit(s => {
        Conductor ! s
        SetValById("chat_in", "")
    })
}

class TableUtils {
    def setTableSessionId = {
        TableSession.tid(S.param("tableId").openOr("0"))
        "*" #> ""
    }
    
    def getTableSessionId = "*" #> TableSession.tid
    
    def genTableDispatcher = {
        //"*" #> <lift:comet type="Dispatcher" name={"%s".format(S.param("tableId").openOr("0"))} />
        "*" #> <lift:comet type="Dispatcher" name={"%s-%d".format(S.param("tableId").openOr("0"), new Date().getTime)}>
	    <div style="width:75%;margin-left:auto;margin-right:auto;margin-bottom:120px;"> 
            <h2>Blackjack Table Message Dump</h2>
            <ul id="about_messages">
              <li>Message...</li>
              <li class="clearable">Another message</li>
              <li class="clearable">A third message</li>
            </ul>
        </div>
        </lift:comet>
    }
    
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
	
	def renderBootstrap = {
	    val tableId = S.param("tableId").openOr("-1").toInt
	    val concernedTable = House.findTableByTid(tableId)
	    val players = Game.findPlayersByTid(tableId)
	    
	    Log.debug("" + concernedTable)
	    Log.debug("" + players)
           "*" #> Script(JsRaw("""$(document).ready(function() {
              var i,  // loop counter
              len, // number of iterations
              gameplay, players, deck, card;


              // set our mode
              CASINO.mode = CASINO.modes.IN_GAME;

              // model for the current game
              // TODO: here"s where we can bootstrap the table
              CASINO.main_table = new CASINO.models.Table({
                 id: """ + concernedTable.tid + """,
                 chips_to_cash_ratio: 1 / 200,
                 min_bet: """ + concernedTable.minBet.toInt + """,
                 in_play: true,
                 counter: 5
              });


              // view for the current game
              gameplay = new CASINO.views.GamePlayView({
                 el: $("#game_play_view"),
                 model: CASINO.main_table
              });

              // add some players
              // TODO: here"s where we can bootstrap the players currently at the table
              players = [
                 new CASINO.models.User({ dealer: true, id: 0, a_real_boy: false, client_user: false, name: "Greg", chips: 20000000, avatar: "/images/avatars/cute_1.jpg" }),
                 new CASINO.models.User({ id: 1, a_real_boy: true, client_user: true, name: "Joey", chips: 20000, avatar: "/images/avatars/cute_2.jpg" }),
                 new CASINO.models.User({ id: 2, a_real_boy: true, client_user: false, name: "Sean", chips: 40000, avatar: "/images/avatars/cute_3.jpg" }),
                 new CASINO.models.User({ id: 3, a_real_boy: true, client_user: false, name: "Neal", chips: 15000, avatar: "/images/avatars/cute_4.jpg" }),
                 new CASINO.models.User({ id: 4, a_real_boy: false, client_user: false, name: "Jon", chips: 1000, avatar: "/images/avatars/cute_5.jpg" }),
                 new CASINO.models.User({ id: 5, a_real_boy: false, client_user: false, name: "Ron", chips: 10000000, avatar: "/images/avatars/cute_6.jpg" })
              ];

              CASINO.main_table.players.add(players);

              // give cards to the players
              // TODO: here"s where we will bootstrap cards already on the table
              for (i = 0, len = players.length * 2; i < len; i += 1) {
                 // card = deck.draw(); // draw a card
                  CASINO.main_table.dealCard( new CASINO.models.Card({suit: "black", value: "A"}), players[i % players.length].get("id") );
              }


              CASINO.main_table.clearCards();

              $("#basic_strategy_handle, #table_info_handle").click(function(e) {
                  $(this).parent().toggleClass("show");
              });
           });"""))

	}
}
