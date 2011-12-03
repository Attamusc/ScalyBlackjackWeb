
$( function() {

   CASINO.models.Table = Backbone.Model.extend({

      defaults : function () {
         return {
            id: 0,                           // identifier
            chips_to_cash_ratio: (1 / 100),  // how much a player can join / leave with
            in_play: false                   // whether in betting or game mode
         };
      },

      initialize: function () {
         var self = this,
             i,              // loop counter
             k,              // key for iterator
             v,              // value for iterator
             seat;           // an individual seat object

         this.players = new CASINO.models.Players;
         this.seats = new CASINO.models.Seats;
         this.hands = new CASINO.models.Hands;

         // Tables have 6 seats with 6 hands at each seat
         for (i = 0; i < 6; i += 1) {
            seat = new CASINO.models.Seat({
               position: i
            });

            // The first seat is the dealer
            if (i === 0) {
               seat.set({
                  dealer: true
               });
            } else if(i == 3) {
               seat.set({
                  client_user: true
               });
            }

            self.seats.add(seat);
            self.hands.add( new CASINO.models.Hand({ seat: seat }) ); // associates the seat with the hand
         }

         // we need to link the player to a seat when a player joins the table
         this.players.bind('add', this.setPlayerSeat, this);
      },


      // Finds the hand for the given player, and adds the card to it
      dealCard: function (card, player_id) {
         var self = this;
         self.hands.each( function (hand) {
            if (!hand.get('seat').get('empty') && hand.get('seat').get('player').get('id') == player_id) {
               hand.cards.add( card );
            }
         });
      },


      setPlayerSeat: function (player) {

         var seat; // the seat the player will sit at

         // if the user is a dealer, use dealer seat
         if (player.get('dealer')) { 
            seat = this.seats.dealer();
         }
         // if the user is the clientUser, use seat 3
         else if (player.get('client_user')) {
            seat = this.seats.client();
         }
         // otherwise, find an open seat
         else {
            seat = this.seats.unoccupied()[0];
         }

         if (seat) {
            CASINO.log('seating ' + player.get('name') + ' (player #' + player.get('id')  + ') at seat #' + seat.get('position'));
            seat.set({ 'player': player, empty: false }); // update the model 
         }
      }

   });



   CASINO.views.MainTableView = Backbone.View.extend({

      template: _.template($('#main-table-template').html()),

      initialize: function () {
         var self = this;    // reference to this for closures

         this.model.players.bind('add', this.addPlayer, this);
         this.model.players.bind('reset', this.addPlayers, this);
         this.model.players.bind('all', this.render, this);
      },


     // attempts to seat a player at an open seat
     addPlayer: function (player) {
        // There's no player view, so rather we will rerender the seat
     },

     // rerenders the seats for each of the players given
     addPlayers: function (players) {
        players.each(this.addPlayer);
     },


     // rerender the seat where the player was sitting
     removePlayer: function (player) {

     },


      // draws the table 
      render: function () {
         var self = this;

         self.el.html(self.template()); // render the base table

         self.$seats = $(self.el).find('.table_seats_wrapper');
         self.$hands = $(self.el).find('.player_cards_wrapper');

         self.model.seats.each( function (seat) { // render the seats at the table
            var seat_item = new CASINO.views.SeatView({ model: seat });
            self.$seats.append(seat_item.render().el);
         });

         self.model.hands.each( function (hand) {
            var hand_item = new CASINO.views.HandView({ model: hand }); // hands is a collection of collections
            self.$hands.append(hand_item.render().el);
         });

         return self;
      }

   });

});
