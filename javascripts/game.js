
$( function () {


   CASINO.views.GamePlayView = Backbone.View.extend({

      template: _.template($('#gameplay-template').html()),

      events: {
         'click .up_bet': 'upBet',
         'click .down_bet': 'downBet',
         'click .your_bet': 'submitBet'
      },

      initialize: function () {

         var self = this, // reference to this for closures
                    i;    // iterator counter

         // subview for the main table
         self.main_table_view = new CASINO.views.MainTableView({
            el: $('#table_section'), 
            model: self.model
         });
         self.main_table_view.render(); 
         self.render();

         self.model.bind('change:in_play', this.swap_actions, this);
      },


      upBet: function (e) {
         var self = this, 
             incrementor = 5,
             new_bet = self.model.get('your_bet') + incrementor;

         e.preventDefault();

         self.setBet(new_bet);
      },


      downBet: function (e) {
         var self = this, 
             incrementor = 5,
             new_bet = self.model.get('your_bet') - incrementor;

         e.preventDefault();
         if (new_bet >= self.model.get('min_bet')) {
            self.setBet(new_bet);
         }
      },


      setBet: function (new_bet) {
         var self = this;
         self.$('.your_bet').val('Bet ' + new_bet).siblings('.your_bet_field').val(new_bet);
         self.model.set({'your_bet': new_bet});
      },


      submitBet: function (e) {
         e.preventDefault();
         // TODO: submit the comet form for betting with the value in the hidden field, $('.your_bet_field')
      },
 

      swap_actions: function (model, in_play) {

         var self = this;
         self.$('.' + (in_play ? 'game_actions' : 'betting_actions')).addClass('hide');
         self.$('.' + (in_play ? 'betting_actions' : 'game_actions')).removeClass('hide');
      },


      render: function () {

         var self = this;
         self.el.html(self.template(self.model.toJSON()));
      }
 
   });

});
