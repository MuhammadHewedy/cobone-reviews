$(document).ready(function() {
	var action = 'PAGE_LOAD';
	logToServer(action);
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

function logToServer(action) {
	console.log(action);
	$.ajax({
		url : '/api/logger',
		method : 'POST',
		data : JSON.stringify({
			path : window.location.pathname,
			action : action,
			referrer : document.referrer.split('/')[2]
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