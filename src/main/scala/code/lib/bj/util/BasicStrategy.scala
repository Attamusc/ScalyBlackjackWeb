package code
package lib
package bj.util

import bj.actor.Request
import bj.actor.Hit
import bj.actor.Stay
import bj.actor.DoubleDown
import bj.actor.Split
import bj.actor.Surrender

import collection.immutable.HashMap

import comet.Conductor

object BasicStrategy {
    val strategyHash : HashMap[String, HashMap[String, String]] = HashMap(
        "A-2"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "A-3"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit"),
        "A-4"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "Hit",           "2" -> "Hit" ),
        "A-5"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "Hit",           "2" -> "Hit" ),
        "A-6"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "DoubleDown",    "2" -> "Hit" ),
        "A-7"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Stay",          "7" -> "Stay",          "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "DoubleDown",    "2" -> "DoubleDown" ),
        "A-8"   ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Stay",          "8" -> "Stay",          "7" -> "Stay",          "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "DoubleDown",    "2" -> "DoubleDown" ),
        "A-9"   ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Stay",          "8" -> "Stay",          "7" -> "Stay",          "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "DoubleDown",    "2" -> "DoubleDown" ),
        "2"     ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Hit",           "5" -> "Hit",           "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "3"     ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Hit",           "5" -> "Hit",           "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "4"     ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Hit",           "5" -> "Hit",           "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "5"     ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Hit",           "5" -> "Hit",           "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "6"     ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Hit",           "5" -> "Hit",           "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "7"     ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Hit",           "5" -> "Hit",           "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "8"     ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Hit",           "5" -> "Hit",           "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "9"     ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "DoubleDown",    "2" -> "Hit" ),
        "10"    ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "DoubleDown",    "8" -> "DoubleDown",    "7" -> "DoubleDown",    "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "DoubleDown",    "2" -> "DoubleDown" ),
        "11"    ->  HashMap( "A" -> "Hit",       "10" -> "DoubleDown",  "9" -> "DoubleDown",    "8" -> "DoubleDown",    "7" -> "DoubleDown",    "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "DoubleDown",    "2" -> "DoubleDown" ),
        "12"    ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Hit",           "2" -> "Hit" ),
        "13"    ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "14"    ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "15"    ->  HashMap( "A" -> "Hit",       "10" -> "Surrender",   "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "16"    ->  HashMap( "A" -> "Surrender", "10" -> "Surrender",   "9" -> "Surrender",     "8" -> "Hit",           "7" -> "Hit",           "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "17"    ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Stay",          "8" -> "Stay",          "7" -> "Stay",          "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "18"    ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Stay",          "8" -> "Stay",          "7" -> "Stay",          "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "19"    ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Stay",          "8" -> "Stay",          "7" -> "Stay",          "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "20"    ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Stay",          "8" -> "Stay",          "7" -> "Stay",          "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "21"    ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Stay",          "8" -> "Stay",          "7" -> "Stay",          "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "2-2"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Split",         "6" -> "Split",         "5" -> "Split",         "4" -> "Split",         "3" -> "Split",         "2" -> "Split" ),
        "3-3"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Split",         "6" -> "Split",         "5" -> "Split",         "4" -> "Split",         "3" -> "Split",         "2" -> "Split" ),
        "4-4"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Split",         "5" -> "Split",         "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit"),
        "5-5"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "DoubleDown",    "8" -> "DoubleDown",    "7" -> "DoubleDown",    "6" -> "DoubleDown",    "5" -> "DoubleDown",    "4" -> "DoubleDown",    "3" -> "DoubleDown",    "2" -> "DoubleDown" ),
        "6-6"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Hit",           "6" -> "Hit",           "5" -> "Hit",           "4" -> "Hit",           "3" -> "Hit",           "2" -> "Hit" ),
        "7-7"   ->  HashMap( "A" -> "Hit",       "10" -> "Hit",         "9" -> "Hit",           "8" -> "Hit",           "7" -> "Split",         "6" -> "Split",         "5" -> "Split",         "4" -> "Split",         "3" -> "Split",         "2" -> "Split" ),
        "8-8"   ->  HashMap( "A" -> "Split",     "10" -> "Split",       "9" -> "Split",         "8" -> "Split",         "7" -> "Split",         "6" -> "Split",         "5" -> "Split",         "4" -> "Split",         "3" -> "Split",         "2" -> "Split" ),
        "9-9"   ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Split",         "8" -> "Split",         "7" -> "Stay",          "6" -> "Split",         "5" -> "Split",         "4" -> "Split",         "3" -> "Split",         "2" -> "Split" ),
        "10-10" ->  HashMap( "A" -> "Stay",      "10" -> "Stay",        "9" -> "Stay",          "8" -> "Stay",          "7" -> "Stay",          "6" -> "Stay",          "5" -> "Stay",          "4" -> "Stay",          "3" -> "Stay",          "2" -> "Stay" ),
        "A-A"   ->  HashMap( "A" -> "Split",     "10" -> "Split",       "9" -> "Split",         "8" -> "Split",         "7" -> "Split",         "6" -> "Split",         "5" -> "Split",         "4" -> "Split",         "3" -> "Split",         "2" -> "Split" )
    )
    
    def action(pid: Int, handKey: String, dealerUpCard: String) : Request = {
        strategyHash(handKey)(dealerUpCard) match {
            case "Hit" =>
                Conductor ! "Player(%d) says: The Basic Strategy says to HIT with a key of '%s' and an upcard of '%s'".format(pid, handKey, dealerUpCard)
                Hit(pid)
            case "Stay" => 
                Conductor ! "Player(%d) says: The Basic Strategy says to STAY with a key of '%s' and an upcard of '%s'".format(pid, handKey, dealerUpCard)
                Stay(pid)
            case "DoubleDown" => 
                Conductor ! "Player(%d) says: The Basic Strategy says to DOUBLEDOWN with a key of '%s' and an upcard of '%s'".format(pid, handKey, dealerUpCard)
                DoubleDown(pid)
            case "Split" => 
                Conductor ! "Player(%d) says: The Basic Strategy says to SPLIT with a key of '%s' and an upcard of '%s'".format(pid, handKey, dealerUpCard)
                //Split(pid)
                Hit(pid)
            case "Surrender" => 
                Conductor ! "Player(%d) says: The Basic Strategy says to SURRENDER with a key of '%s' and an upcard of '%s'".format(pid, handKey, dealerUpCard)
                Surrender(pid)
            case _ =>
                // In the event something weird happens, tell the player to stay so that the game can continue
                Stay(pid)
        }
    } 
}