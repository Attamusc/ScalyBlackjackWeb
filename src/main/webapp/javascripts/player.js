
$( function () {

   // A user is an instance of an object capable of playing blackjack
   CASINO.models.User = Backbone.Model.extend({

      defaults: function () {
         return {
            id: 0,
            dealer: false,
            a_real_boy: false, // whether or not this user is a bot
            client_user: false, // if this is the user who is using the webapp
            name: 'Anon',
            avatar: 'http://placekitten.com/50/50',
            chips: 0
         };
      },

      initialize: function () {
      },

      updateChips: function (difference) {
         if (!isNaN(difference)) {
            this.chips += difference;
         }
      },
   
      clearHand: function () {
         this.hand.reset();
      },
   
      addCardToHand: function (card) {
         this.hand.add(card); // fires add event
      }
   });


   // A table will have a collection of players
   CASINO.models.Players = Backbone.Collection.extend({
      model: CASINO.models.User
   });

});
