
// fire on document.ready
$(function () {

   CASINO.models.Seat = Backbone.Model.extend({

      defaults: function () {
         return {
            dealer: false,
            position: 0,
            empty: true,
            player: null,
            client_user: false
         };
      }

   });


   // A table has a collection of seats
   CASINO.models.Seats = Backbone.Collection.extend({

      model: CASINO.models.Seat,

      dealer: function () {
         var list = this.filter(function(seat) { return seat.get('dealer'); });
         return list.length > 0 ? list[0] : false;
      },

      client: function () {
         var list = this.filter(function(seat) { return seat.get('client_user'); });
         return list.length > 0 ? list[0] : false;
      },

      unoccupied: function () {
         return this.filter(function(seat) { return seat.get('empty') && !seat.get('client_user'); });
      },

      occupied: function () {
         return this.without.apply(this, this.unoccupied());
      }

   });


   CASINO.views.SeatView = Backbone.View.extend({

      template: _.template($('#main-player-seat-template').html()),

      intialize: function () {
         var self = this;
      },

      render: function () {
         $(this.el).html(this.template(this.model.toJSON()));
         return this;
      }
   
   });
});
