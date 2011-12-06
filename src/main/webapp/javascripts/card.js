
$(function () {

   // Represents a single card a player holds in their hand
   CASINO.models.Card = Backbone.Model.extend({
   
      defaults: function () {
         return {
            suit: 'red',
            value: 'A',
            visible: true
         };
      },


      // only applicable to the dealer's down card
      flipCard: function () {
         this.visible = true;
      }
   
   });



   CASINO.models.Cards = Backbone.Collection.extend({
      model: CASINO.models.Card
   });
   
   

   CASINO.views.CardView = Backbone.View.extend({

      tagName: 'div',

      className: 'card',

      template: _.template($('#card-template').html()),
   
      initialize: function () {
         this.model.bind('change:visible', this.render, this);
      },
   
      render: function () {
         var self = this,
             card = self.model;

         $(this.el).html(this.template(this.model.toJSON()))
            .removeClass('show_back')
            .addClass(card.get('suit') + '_card ' + (card.get('visible') ? '' : 'show_back'));

         return this;
      },
   
      remove: function () {
         $(this.el).remove();
      }
   
   });



   // Each player will have a hand
   CASINO.models.Hand = Backbone.Model.extend({

      defaults: function () {
         return {
            seat: null,
            cards: []
         };
      },

      initialize: function () {
         var self = this;
         self.cards = Backbone.Collection.nest(this, 'cards', new CASINO.models.Cards(this.get('cards')));
      }
      
   });


   CASINO.models.Hands = Backbone.Collection.extend({

      initialize: function () {
         var self = this;
      },

      model: CASINO.models.Hand,

      dealer: function () {
         var list = this.filter( function (hand) { return hand.get('seat').get('player') && hand.get('seat').get('player').get('dealer') });
         return list.length > 0 ? list[0] : false;
      }
   });



   CASINO.views.HandView = Backbone.View.extend({

      tagName: 'div',

      className: 'card_wrapper',

      template: _.template($('#hand-template').html()),

      initialize: function () {
         var self = this;
         self.cards = [];

         self.model.cards.each( function (card) {
            self.cards.push( new CASINO.views.CardView({ model: card }) );
         });

         self.model.cards.bind('add', self.addOne, this);
         self.model.cards.bind('reset', self.render, this);
      },

      addOne: function (card) {
         var self = this,
             view = new CASINO.views.CardView({ model: card });
         $(this.el).append(view.render().el); // TODO: animate!
      },

      render: function () {
         var self = this, 
             seat = self.model.get('seat');

         $(self.el).html(self.template(self.model.toJSON()))
            .attr('id', seat.get('position') == 3 ? 'player_cards' : '')
            .addClass((seat.get('dealer') ? 'dealer' : 'seat_' + seat.get('position')) + '_cards');

         _(self.cards).each( function (card) {
            $(self.el).append(card.render().el);
         });
         return this;
      }
   });

});
