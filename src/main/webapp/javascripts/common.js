if (!window.CASINO) { window.CASINO = {}; }


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

  })();

};


CASINO.init = function() {

  CASINO.reset();

  //model close events
  $('body').delegate('.modal .cancel', 'click', function() {
    $(this).parents('.modal').modal('hide');
  });

};



$(document).ready(CASINO.init);