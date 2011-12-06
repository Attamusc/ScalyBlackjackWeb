// From: https://github.com/documentcloud/backbone/pull/614
// 
// Utility function for easily nesting collections into a model.
// Nesting collections is painful because the model's toJSON function
// has to be overriden since the model's data and the collection's data are not the same
// This utility fixes that by pointing the underlying model data object to the collection data
// so that the model's data is always the same as the nested collection's.
// No more need to override the model's toJSON function.
Backbone.Collection.nest = function(model, attributeName, nestedCollection) {
   // Setup nested references
   for (var i = 0; i < nestedCollection.length; i++) {
      if(model.attributes[attributeName]){
         model.attributes[attributeName][i] = nestedCollection.at(i).attributes;
      }
   }
   nestedCollection.bind('add', function(initiative) {
      model.get(attributeName).push(initiative.attributes);
   });
   nestedCollection.bind('remove', function(initiative) {
      var updateObj = {};
      updateObj[attributeName] = _.without(model.get(attributeName), initiative.attributes);
      model.set(updateObj);
   });

   return nestedCollection;
};


// CONSTANTS!
CASINO.modes = {
   IN_GAME: 1,
   VIEW_GAME: 2,
   FLOOR: 3
};


// re-binds common events to elements that may be added later
// TODO: use delegate on parent nodes instead
CASINO.reset = function() {

   (function() {

      var bind_to = $('[data-show-convert]'); // trigger by change

      // display converted result joining and leaving a table
      function display_conversion(e) {
         var $el = $(this), 
             target = $( '#' + $el.data('show-convert')), // new value goes here
             new_val = $el.val() * (+$el.data('rate')), // note the typecast
             text = $el.data('describe').replace('{data}', new_val);
 
         target.html(text);
      }


      // start listening!
      bind_to.bind('keyup keydown change', display_conversion);

   }());


   // images moving back and forth on edit user view!
   (function() {

      // set buttons to rotate avatars on edit page
      var avatar_list = $('#player_avatar_options'), 
         num_avatars = avatar_list.children('img').size(),
         avatar_select = $('#player_avatar_select');

      $('#cycle_avatar_left').click(function(e) {
         e.preventDefault();
         var item = avatar_list.find('.chosen').removeClass('chosen'), prev = item.prev('img');
         if (prev.size()) {
            prev.addClass('chosen');
         } else {
            prev = avatar_list.find('img').last().addClass('chosen');
         }
         avatar_select.val(prev.data('value'));
      });
      $('#cycle_avatar_right').click(function(e) {
         e.preventDefault();
         var item = avatar_list.find('.chosen').removeClass('chosen'), next = item.next('img');
         if (next.size()) {
            next.addClass('chosen');
         } else {
            next = avatar_list.find('img').first().addClass('chosen');
         }
         avatar_select.val(next.data('value'));
      });
   })();

};


// currently logs a message to the browse debugger console
CASINO.log = function (message) {
   console.log(message);
};


// The first thing to fire on page load
CASINO.init = function () {

   CASINO.reset();

   //model close events
   $('body').delegate('.modal .cancel', 'click', function() {
      $(this).parents('.modal').modal('hide');
   });

};



$(document).ready(CASINO.init);
