
$( function () {


   CASINO.views.GamePlayView = Backbone.View.extend({

      template: _.template($('#gameplay-template').html()),

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
