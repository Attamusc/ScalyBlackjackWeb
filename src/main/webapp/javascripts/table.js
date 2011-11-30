( function( $ ) {

   function Table(id, options) {

      var i, len;

      this.defaults = {
         chips_to_cash_ratio: 1/100,
         num_seats: 5
      };

      this.info = $.extend({}, this.defaults, options);
      this.info.id = id;
      this.players = {}; // Not an array because order doesn't matter, we always show current user in center. Key is player id
      this.seats = [];

      for (i = 0, len = this.info.num_seats.length; i < len; i += 1) {
         this.info.seats[i] = {taken: false, player: 0};
      }
   }

   // private
   // returns an integer for the index in seats that is available,
   // or false otherwise
   function unoccupiedSeat() {
      var i = this.seats.length, seat = false;
      while(i-- && seat === false) {
         if (this.seats[i].taken === false) {
           seat = i;
         }
      }
      return seat;
   };

   // private
   // sets the seat at the given index to taken, and links the layer
   function markSeatTaken(i, player) {
      if (this.seats[i] !== undefined) {
         this.seats[i].taken = true;
         this.seats[i].player = player;
      }
   };

   // private
   // sets the seat at the given index to not taken and removes the player
   function markSeatFree(i) {
      if (this.seats[i] !== undefined) {
         this.seats[i].taken = false;
         this.seats[i].player = null;
      }
   };


   // add a player to the table
   Table.prototype.addPlayer = function (player) {
      // find seat for the player
      var new_seat = unoccupiedSeat.call(this);

      if (new_seat !== false) {
         CASINO.log("New player joined table " + this.id);
         player.info.seat = new_seat;
         markSeatTaken.call(this, new_seat, player);
         this.info.players[player.info.id] = player;
         // TODO: update the view
      } else {
         CASINO.log('Seat not found for player');
      }
   };

   // remove a player from the table
   Table.prototype.removePlayer = function (player) {
      CASINO.log("Player left table " + this.id);
      markSeatFree.call(this, player.info.id);
      delete this.info.players[player.info.id];
      // TODO: update the view
   };


   // Table will manage distributing cards to the player's hands
   Table.prototype.addCard = function (player_id, card) {
      
      var player = this.players[player_id];
      player.addCardToHand(card);
   };


   var CASINO = CASINO || { generators : {} }; // Loose module pattern
   CASINO.generators.Table = Table;
   CASINO.tables = {}; // will hold each active table. Indexed by table id

}( jQuery ));
