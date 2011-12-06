
// fire on document.ready
$(function () {

   CASINO.models.Seat = Backbone.Model.extend({

      defaults: function () {
         return {
            dealer: false,
            position: 0,
            empty: true,
            player: null,
            client_user: false,
            notice: null
         };
      },

      initialize: function () {
         var self = this;
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

      player: function (player_id) {
         var list = this.filter(function(seat) { var p = seat.get('player'); return p && p.id === player_id; });
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

      tagName: 'div',

      className: 'seat',

      events: {
         'mouseover':   'twipsy_awesomeness'
      },


      initialize: function () {
         var self = this;
         this.model.bind('change:player', this.render, this);
         this.model.bind('change:notice', this.noticeChipChange, this);
      },


      noticeChipChange: function (model, val) {
         var self = this;
         if (model.get('notice') === -1) {
            self.flash('red');
         } else if (model.get('notice') === 1) {
            self.flash('green');
         }
      },


      flash: function (color) {
         var self = this,
             $el = $(self.el),
             prev_color = $el.css('border-color'),
             i;

         $el.css('borderColor', color);

         for (i = 1; i < 12; i += 1) {
            ( function (i) {
               setTimeout(function() {
                  $el.css('borderColor', i % 2 == 1 ? prev_color : color);
               }, i * 400);
            }(i));
         }
      },


      // player popover
      twipsy_awesomeness: function (e) {
         $(this.el).twipsy({}).twipsy('show');
      },


      render: function () { // this is all because backbone doesn't support associations natively...
         var self = this,
             attrs = self.model.attributes,
             el = $(self.el).empty()
                  .removeClass('player_seat').removeClass('dealer_seat').removeClass('empty_seat')
                  .addClass((attrs.dealer ? 'dealer_seat' : 'seat_' + attrs.position) + ' '  + (attrs.empty ? 'empty_seat' : 'player_seat'))
                  .attr('title', !attrs.empty ? attrs.player.get('name') + ' (' + (attrs.player.get('a_real_boy') ? 'Human' : 'Bot') + ') ' + attrs.player.get('chips') + ' chips' : 'Empty')
                  .data('placement', 'below') // for twipsy
                  .html(attrs.empty ? 'Empty' : '<img src="' + attrs.player.get('avatar') + '" />');

         return this;
      }
   
   });
});
