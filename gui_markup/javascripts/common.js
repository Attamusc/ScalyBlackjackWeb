$(document).ready(function() {
	//model close events
	$('body').delegate('.modal .cancel', 'click', function() {
		$(this).parents('.modal').modal('hide');
	});
});
