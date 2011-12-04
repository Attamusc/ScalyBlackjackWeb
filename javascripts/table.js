
$( function() {

   CASINO.models.Table = Backbone.Model.extend({

      defaults : function () {
         return {
            id: 0,                           // identifier
            chips_to_cash_ratio: (1 / 100),  // how much a player can join / leave with
            in_play: false,                  // whether in betting or game mode
            min_bet: 50,
            players: [],
            seats: [],
            hands: []
         };
      },

      initialize: function () {
         var self = this,
             i,              // loop counter
             k,              // key for iterator
             v,              // value for iterator
             seat;           // an individual seat object

         this.players = Backbone.Collection.nest(this, 'players', new CASINO.models.Players(this.get('players')));
         this.seats = Backbone.Collection.nest(this, 'seats', new CASINO.models.Seats(this.get('seats')));
         this.hands = Backbone.Collection.nest(this, 'hands', new CASINO.models.Hands(this.get('hands')));

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
         var self = this, player;
         self.hands.each( function (hand) {
            if (!hand.get('seat').get('empty')) {
               player = hand.get('seat').get('player');
               if (player.id == player_id) {
                  hand.cards.add( card );
               }
            }
         });
      },


      payout: function (chip_difference, player_id) {
         var self = this;
         self.players.each( function (player) {
            if (player.get('id') == player_id) {
               player.set({ chips: player.get('chips') + chip_difference });
            }
         });
      },


      clearCards: function() {
         var self = this;
         self.hands.reset();
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
            seat = this.seats.unoccupied()[0]; // TODO: index might explode
         }

         if (seat) {
            CASINO.log('seating ' + player.get('name') + ' (player #' + player.get('id')  + ') at seat #' + seat.get('position'));
            seat.set({ 'player': player, empty: false }); // update the model 
         }
      },

      playerLeave: function (player_id) {
         var self = this,
             seat = self.seats.player(player_id);  // try finding this player's seat

         if (seat) {
            CASINO.log(seat.get('player').get('name') + ' at seat #' + seat.get('position') + ' left the table');
            self.players.remove(seat.get('player'));
         }
      }

   });



   CASINO.views.MainTableView = Backbone.View.extend({

      template: _.template($('#main-table-template').html()),

      initialize: function () {
         var self = this;    // reference to this for closures

         this.model.players.bind('add', this.renderSeats, this); // TODO: just render the specifc seat
         this.model.players.bind('remove', this.playerLeaveSeat, this);
      },

      // draws the table 
      render: function () {
         var self = this;

         self.el.html(self.template()); // render the base table
         self.renderSeats();
         self.renderHands();
         return self;
      },

      playerJoinSeat: function (seat) {

      },

      playerLeaveSeat: function (player) {
         var self = this,
             seats = self.model.seats.occupied(),
             $seats = $(self.el).find('.table_seats_wrapper');

         _.each(seats, function (seat) {
            if (seat.get('player').get('id') == player.get('id')) {
               seat.set({'player': null, empty: true});
               $seats.find('.seat_' + seat.get('position')).replaceWith( new CASINO.views.SeatView({ model: seat }).render().el );
            }
         });
      },

      renderSeats: function () {
         var self = this;
         self.$seats = $(self.el).find('.table_seats_wrapper').empty();

         self.model.seats.each( function (seat) { // render the seats at the table
            var seat_item = new CASINO.views.SeatView({ model: seat });
            self.$seats.append(seat_item.render().el);
         });
      }, 

      renderHands: function () {
         var self = this;
         self.$hands = $(self.el).find('.player_cards_wrapper').empty();

         self.model.hands.each( function (hand) {
            var hand_item = new CASINO.views.HandView({ model: hand }); // hands is a collection of collections
            self.$hands.append(hand_item.render().el);
         });
      }

   });

});
