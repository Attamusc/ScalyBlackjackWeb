var CASINO = CASINO || {};


// Attendant singleton
// 
CASINO.attendant = (function( $ ) {

  var api = {}, // public api literal

  about_message = $('#about_messages'); // temporary for the code review demo


  // receives a message in a psuedo JSONP call from lift
  api.process_message = function( message ) {

     about_message.append('<li>' + message + '</li>');
     console.log("incoming message: " + message);

     if (message && message.type) {
        switch (message.type) {
           case  'joiner': // message for when a player joins a game
              // {sender: house, id: player_id}
              console.log(message.message);
              $('.player-button, #bet-button').data("pid", message.id);
              break;
           case  'info': // purely for logging
              console.log(message.message);
              break;
           case 'message': // messages that are broadcast publicly
              console.log(message.message);
              // {sender: dealer, id: player_id, message: string}
              break;
           case 'update': // handles new cards
              // {sender: dealer, id: integer, card: {suit: red|black, value: 2-10JQKA}}
              break;
           case 'result':
              // {sender: dealer, id: integer, status: string, payout: integer}
              break;
           default:
              console.log('Unhandled message type: ' + message.type);
        }
     }
  };

  return api;


}( jQuery ));
