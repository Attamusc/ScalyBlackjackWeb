( function( $ ) {

   function Player(id, options) {

      var that = this, secret = {};

      this.defaults = {
         name: 'Anon',
         avatar: 'http://placekitten.com/50/50',
         chips: 0
      };

      this.info = $.extend({}, this.defaults, options);
      this.hand = [];
      secret.id = id;
   }

   Player.prototype.id = function () {
      return secret.id;
   };


   Player.prototype.updateChips = function (difference) {
      if (!isNaN(difference)) {
         this.chips += difference;
         // update the view
      }
   };

   Player.prototype.clearHand = function () {
      this.hand.length = 0;
      // TODO: update the view
   };

   Player.prototype.addCardToHand = function (card) {
      this.hand.push(card);
      // TODO: update the view
   };


   var CASINO = CASINO || { generators : {}}; // Loose module pattern
   CASINO.generators.Player = Player;

}( jQuery ));
