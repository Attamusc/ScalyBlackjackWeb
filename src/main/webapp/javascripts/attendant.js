var CASINO = CASINO || {};


// Attendant singleton
// 
CASINO.attendant = (function( $ ) {

  // public api literal
  var api = {},
      about_message = $('#about_messages');

  // 
  api.process_message = function( message ) {

    about_message.append('<li>' + message + '</li>');
    console.log("incoming message: " + message);

    if (message && message.type) {

      console.log("processing: " + message.type);

      if (message.type == 'player') {

      } else if (message.type == 'game') {

      } else if (message.type == 'something') {

      }
    }
  };

  return api;


}( jQuery ));
