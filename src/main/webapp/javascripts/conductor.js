var CASINO = CASINO || {};


// Attendant singleton
// 
CASINO.attendant = (function( $ ) {

  // public api literal
  var api = {};

  // 
  api.process_message = function( message ) {

    if (message && message.type) {

      console.log("processing: " + message.type);

      if (message.type == '') {

      } else if (message.type == '') {

      } else if (message.type == '') {

      }
  };

  return api;


}( jQuery ));
