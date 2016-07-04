
var serverApiUrl = serverUrl + '/api/';

(function() {
    if ($(".singledealbuy").length + $(".dealpanelover").length <= 0) {
        console.log('this page DOEST NOT have dealpanel section');
    } else {
        console.log('this page has dealpanel section');

        $.get(serverUrl + '/app/comments.html', function(data) {
            var e = $(xpath('//*[@id="page-content-wrapper"]/section[2]/div/div/div[1]'));
            $($.parseHTML(data)).appendTo(e);
            injectRecaptcha();
            translate();
        });
        loadComments({
            'path': window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1)
        }, 0);
    }
})();

/// ------- functions ----------------------------------------------------------

// -- load comments function
function loadComments(params, page) {
    $.get(serverApiUrl + "/comments/" + params.path + "?page=" + page, function(data) {

        console.log(data);
        $('#fieldset').prop("disabled", false);

        if (data.content.length > 0) {

            page == 0 && $('#comments').empty();

            for (var i = 0; i < data.content.length; i++) {
                var comment = data.content[i];
                $('#comments').append("<span style='font-weight: bold; font-size: larger;'>" + comment.name + " </span>");
                if (comment.canDelete) {
                    $('#comments').append("<button type='button' id='delete-" + comment.id + "' class='btn btn-link'>" +
                        "(" + getMessage("delete") + ")</button>");

                    $("#delete-" + comment.id).unbind('click').bind('click', {
                        id: comment.id
                    }, function(evt) {
                        deleteComment(evt.data.id, params)
                    });
                }
                $('#comments').append("<p>" + comment.content.split('\n').join('<br />') + "</p>");
                var diffObj = getDateDiff(new Date(comment.created));
                $('#comments').append("<p><small>" + getMessage('since') + diffObj.duration + getMessage(diffObj.unit) +
                    "</small></p> <hr />");
            }
            // load more
            if (data.last) {
                $('#load-more').css('display', 'none');
            } else {
                $('#load-more').css('display', 'block');
                $('#load-more').unbind('click').bind('click', function() {
                    loadComments(params, page + 1);
                });
            }
        } else {
            $('#comments').empty();
            $('#load-more').css('display', 'none');
            $('#comments').append(getMessage("no_comments_found"));
        }

        $('#review-submit').unbind('click').bind('click', function() {
            submitHandler(params);
        });
    });
}

// -- button submit callback
function submitHandler(params) {

    var name = $.trim($('#review-name').val());
    var email = $.trim($('#review-email').val());
    var content = $.trim($('#review-content').val());
    var gRecaptchaResp = $("#g-recaptcha-response").val();

    if (validate(name, email, content, gRecaptchaResp)) {

        $('#fieldset').prop("disabled", true);
        $.ajax({
            type: 'POST',
            url: serverApiUrl + "/comments",
            data: JSON.stringify({
                deal: {
                    path: params.path
                },
                name: name,
                email: email,
                content: content,
                url: window.location.pathname,
                captcha: gRecaptchaResp
            }),
            success: function() {
                showSuccess('add_success_message');
                $('input[type=text], textarea').val('');
                $("#g-recaptcha").empty();
                injectRecaptcha();
                loadComments(params, 0);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $('#fieldset').prop("disabled", false);
                alert(jqXHR.responseText);
            },
            contentType: "application/json"
        });
    }
}

function deleteComment(id, params) {
    $.ajax({
        type: 'DELETE',
        url: serverApiUrl + "/comments/" + id,
        success: function() {
            showSuccess('delete_success_message');
            loadComments(params, 0);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $('#fieldset').prop("disabled", false);
            alert(jqXHR.responseText);
        }
    });
}

function validate(name, email, content, gRecaptchaResp) {
    restRequiredFields();
    var ret = true;
    if (!name) {
        addError('#review-name', 'required_fields_name');
        ret = false;
    }
    if (email && email.length > 0 && !/\S+@\S+\.\S+/.test(email)) {
        addError('#review-email', 'required_fields_invalid_email');
        ret = false;
    }
    if (!content) {
        addError('#review-content', 'required_fields_content');
        ret = false;
    }
    if (content && content.length < 20) {
        addError('#review-content', 'required_fields_content_length');
        ret = false;
    }
    if (!gRecaptchaResp) {
        addError('#g-recaptcha', 'required_fields_gRecaptchaResp');
        ret = false;
    }
    return ret;
}

function addError(input, message) {
    $("#alert-message").append(getMessage(message) + '<br />');
    $(input).css('border-color', 'red');
    $(input).css('border-style', 'solid');
    $('#alert').attr("class", "alert alert-danger");
    $('#alert').css('display', 'block');
    $('html, body').animate({
        scrollTop: $("#alert").offset().top
    }, 100);
}

function showSuccess(message) {
    $("#alert-message").text(getMessage(message));
    $('#alert').attr("class", "alert alert-success");
    $('#alert').css('display', 'block');
    $('html, body').animate({
        scrollTop: $("#alert").offset().top
    }, 100);
}

function restRequiredFields() {
    $('input[type=text], textarea, #g-recaptcha').css('border-color', '#ccc');
    $('#g-recaptcha').css('border-style', 'hidden');
    $("#alert-message").empty();
    $('#alert').css('display', 'none');
}

// -- xpath utility
function xpath(STR_XPATH) {
    var xresult = document.evaluate(STR_XPATH, document, null, XPathResult.ANY_TYPE, null);
    var xnodes = [];
    var xres;
    while (xres = xresult.iterateNext()) {
        xnodes.push(xres);
    }
    return xnodes;
}

function getDateDiff(date) {
    var msDiff = new Date() - new Date(date);
    var minDiff = msDiff / 60 / 1000;
    var hDiff = msDiff / 3600 / 1000;
    var dDiff = hDiff / 24;

    var ret = {};

    if (hDiff < 1) {
        ret.duration = Math.ceil(minDiff);
        ret.unit = 'MIN';
    } else if (hDiff < 24) {
        ret.duration = Math.ceil(hDiff);
        ret.unit = 'HOUR';
    } else {
        ret.duration = Math.ceil(dDiff);
        ret.unit = 'DAY';
    }
    return ret;
}

function injectRecaptcha() {
    var s = document.createElement('script');
    s.src = 'https://www.google.com/recaptcha/api.js?hl=' + lang;
    s.onload = function() {
        this.parentNode.removeChild(this);
    };
    (document.head || document.documentElement).appendChild(s);
}
