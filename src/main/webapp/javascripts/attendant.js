
// Attendant singleton
// 
CASINO.attendant = (function( $ ) {

   var api = {}; // public api literal

   // attempts to find the table by the given id
   function find_table (table_id) {
      var found = false;
      if (CASINO.mode === CASINO.modes.IN_GAME || CASINO.mode === CASINO.modes.VIEW_GAME) {
         found = CASINO.main_table;
      } else {
         // Something else to find the table on the floor. Will probably Loop through CASINO.tables
      }

      return found;
   }


   /* receives a message in a psuedo JSONP call from lift
      // TODO: may have to parse the message into an object literal first
      messages are expected in the minimum form:
        { type: string, stuff... }
    */
   api.process_message = function( message ) {

      if (message && message.type) {

         console.log('processing message: ' + message.type);

         switch (message.type) { //  Note to self. "fine , you can use a switch statement. Just keep checking for fallthroughs"


            /* Seats a player at the given table

               - client_user should always be false because it will be bootstapped on pageload
               - dealer should always be false because it will be added on page load
               - if we don't bother letting users edit their profile and pick their avatar, just use a random http://placekitten.com/:integer/:integer url
               - chips is the number of chips they apparently 
               
               {table_id: integer, player: {id: integer, dealer: false, a_real_boy: true|false, client_user: true|false, name: string, avatar: string, chips: integer}}
             */
            case 'join_table':
               var i, len, // looping stuffs 
                   table = find_table(message.table_id);

               table.players.add( new CASINO.models.User( message.player ) );
               break;


            /* Removes a player from the given table

               {player_id: integer, table_id: integer}
             */
            case 'leave_table':

               var i, len, // looping stuffs 
                   table = find_table(message.table_id);

               table.playerLeave( message.player_id );
               break;


            /* Deal a card to a player
               - card suit should fall in the set [red, black]
               - card value should fall in the set [2-10, J, Q, K, A]
            
               {table_id: integer, player_id: integer, card: {card_number: integer, suit: red|black, value: string} }
             */
            case 'deal_card':

               // find the table
               var i, len, // looping stuffs 
                   table = find_table(message.table_id);

               table.dealCard(new CASINO.models.Card(message.card), message.player_id);
               break;


            /* Reveals the results to a player
                - Pays the players their payout
                - If the player is the current user, show the result message
                - Fun animation or something

                {table_id: integer, player_id: integer, payout: integer, status: string }
             */
            case 'game_result':

               // find the table
               var i, len, // looping stuffs
                   table = find_table(message.table_id);

               table.payout(message.payout, message.player_id, 'Status: ' + message.status); // pay the player their winnings
               break;


            /* Prepares the table for the next round
                - Starts the betting countdown
                - Switches the mode of the table to betting mode

                {table_id: integer}
             */
            case 'end_game':

               // find the table
               var i, len, // looping stuffs
                   table = find_table(message.table_id);

               table.set({ in_play: false }); // Starts the betting countdown automatically, TODO: do we want to set the counter to like the cutoff datetime

               break;


            /* Betting is finished, clear the table
                - Clears the cards off the table
                - Switches the mode of the table to in_game mode

                {table_id: integer}
             */
            case 'new_game':
               // find the table
               var i, len, // looping stuffs
                   table = find_table(message.table_id);

               table.clearCards(); // clears the cards
               table.set({ in_play: true });
               break;


            default:
               console.log('Unhandled message type: ' + message.type);
         }
      }
   };

   return api;

}( jQuery ));
