var CASINO = CASINO || {}; // loose module pattern


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
