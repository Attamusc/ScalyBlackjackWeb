
// Attendant singleton
// 
CASINO.attendant = (function( $ ) {

   var api = {}, // public api literal

   about_message = $('#about_messages'); // temporary for the code review demo


   // attempts to find the table by the given id
   function table (table_id) {
      var table;
      if (CASINO.mode === CASINO.modes.IN_GAME || CASINO.mode === CASINO.modes.VIEW_GAME) {
         table = CASINO.main_table;
      } else {
         // Something else to find the table on the floor. Will probably Loop through CASINO.tables
      }

      return table;
   }


   // receives a message in a psuedo JSONP call from lift
   // messages are expected in the minimum form:
   //     { type: string, data: {stuff...} }
   api.process_message = function( message ) {

      about_message.append('<li>' + message + '</li>');
      console.log("incoming message: " + message);

      if (message && message.type && message.data) {

         switch (message.type) { //  Note to self. "fine , you can use a switch statement. Just keep checking for fallthroughs"

            case  'info': // purely for logging
               console.log(message.message);
               break;


            case 'message': // messages that are broadcast publicly
               console.log(message.message);
               // TODO: broadcast publicly?
               // {sender: dealer, id: player_id, message: string}
               break;


            // Seats a player at the given table
            case 'join_table':
               /* INFO: 
                  client_user should always be false because it will be added on page load
                  dealer should always be false because it will be added on page load
                  if we don't bother letting users edit their profile and pick their avatar, just use a random http://placekitten.com/:integer/:integer url
                  chips is the number of chips they apparently 
                  
                  {table_id: integer, player: {id: integer, dealer: false, a_real_boy: true|false, client_user: true|false, name: string, avatar: string, chips: integer}}
                */
               // find the table
               table(message.data.table_id).players.add( new CASINO.models.User( message.data.player ) );
               break;

            // Removes a player from the given table
            case 'leave_table':
               // {player_id: integer, table_id: integer}
               table(message.data.table_id).playerLeave( message.data.player_id );
               break;


            case 'deal_card': // give a card to a player
               /* INFO: 
                  if card is an array of cards, batch dealing will take place!
                  card suit should fall in the set [red, black]
                  card value should fall in the set [2-10, J, Q, K, A]
               
                  {player_id: integer, card: {suit: red|black, value: string}}
                */

               // find the table
               var i, len, // looping stuffs 
                   table = table(message.data.table_id);

               // we are just going to go ahead and assume cards are well-formed for now
               if ($.isArray(message.data.card)) {
                  for (i = 0, len = message.data.card.length; i < len; i += 1) {
                     table.dealCard(message.data.card[i], message.data.player_id);
                  }
               } else {
                  table.dealCard(message.data.card, message.data.player_id);
               }
               break;


            case 'waiting_on_you': // prompt user for a move
               // TODO: debating if we need this. Do players really need to act in turn?

               break;


            default:
               console.log('Unhandled message type: ' + message.type);
         }
      }
   };

   return api;

}( jQuery ));
