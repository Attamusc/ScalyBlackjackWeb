( function( $ ) {

   function Player(id, options) {

      var that = this, secret = {};

      this.defaults = {
         name: 'Anon',
         avatar: 'http://placekitten.com/50/50',
         chips: 0
      };

      this.info = $.extend({}, this.defaults, options);
      secret.id = id;
   }

   Player.prototype.id = function () {
      return secret.id;
   };


   var CASINO = CASINO || { generators : {}}; // Loose module pattern
   CASINO.generators.Player = Player;
   // CASINO.players = {}; // will hold each active player. Index by player id

}( jQuery ));
