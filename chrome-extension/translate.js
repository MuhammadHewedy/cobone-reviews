var lang = window.location.href.split("/").indexOf('ar') > 0 ? 'ar' : 'en';
console.log('lang: ' + lang)

function getMessage(key) {
    return messages[key][lang];
}

function translate() {
    $("#review-name").attr('placeholder', getMessage('name'));
    $("#review-email").attr('placeholder', getMessage('email'));
    $("#review-content").attr('placeholder', getMessage('content'));
    $("#review-submit").text(getMessage('submit'));
    $("#title").text(getMessage('title'));
    $("#required-fields-alert-msg").text(getMessage('required_fields'));
    $("#success-message-alert-msg").text(getMessage('success_message'));
}
