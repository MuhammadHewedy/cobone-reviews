$(document).ready(function() {
	var action = 'PAGE_LOAD';
	logToServer(action);

	var start = Date.now();
	$(window).bind('beforeunload', function() {
		logToServer('TIME_SPENT', Math.round((Date.now() - start)/1000));
	});
});

$(document).scroll(function() {
	if ($(window).scrollTop() + $(window).height() > $(document).height() - 1200) {
		// remove the scroll event, I just want to detect the scroll once
		$(document).off("scroll");
		var action = 'PAGE_SCROLL';
		logToServer(action);
	}
});

$(".watchButton").bind("click", function() {
	var action = 'WATCH_CLICK';
	logToServer(action);
});
$(".contactButton").bind("click", function() {
	var action = 'CONTACT_CLICK';
	logToServer(action);
});
$(".storeButton").bind("click", function() {
	var action = 'STORE_BUTTON_CLICK';
	logToServer(action);
});

function logToServer(action, value) {
	console.log(action);
	$.ajax({
		url : '/api/logger',
		method : 'POST',
		data : JSON.stringify({
			path : window.location.pathname,
			action : action,
			referrer : document.referrer.split('/')[2],
			value: value
		}),
		contentType : 'application/json',
		success : function() {
			console.log('success')
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log('fail ' + jqXHR.responseText)
		}
	});
}