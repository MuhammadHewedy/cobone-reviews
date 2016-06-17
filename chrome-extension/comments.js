// var serverUrl = 'https://cobone-reviews.herokuapp.com';
var serverUrl = 'http://localhost:8080';

(function() {
    var buttonList = $(".add-to-cart")
    if (buttonList.length <= 0) {
        console.log('this page DOEST NOT have add To cart!')
    } else {
        console.log('this page has add To cart!')

        $.get(chrome.extension.getURL('/comments.html'), function(data) {
            var e = $(xpath('//*[@id="page-content-wrapper"]/section[2]/div/div/div[1]'));
            $($.parseHTML(data)).appendTo(e);

            translate();
        });
        loadComments({
            'path': window.location.pathname.split("/").join("~").substring(4)
        }, 0);
    }
})();

/// ------- functions ----------------------------------------------------------

// -- load comments function
function loadComments(params, page) {
    $.get(serverUrl + "/comments/" + params.path + "?page=" + page, function(data) {

        console.log(data);
        $('#fieldset').prop("disabled", false);

        if (data.content.length > 0) {

            params.dealId = data.content[0].deal.id;
            page == 0 && $('#comments').empty();

            for (var i = 0; i < data.content.length; i++) {
                var comment = data.content[i];
                $('#comments').append("<h4>" + comment.name + "</h4>");
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

    var dealTitle = $(document).find("title").text()
    var dealCompany = ''; // TODO

    var name = $.trim($('#review-name').val())
    var email = $.trim($('#review-email').val())
    var content = $.trim($('#review-content').val())

    if (validate(name, email, content)) {

        $('#fieldset').prop("disabled", true);
        $.ajax({
            type: 'POST',
            url: serverUrl + "/comments",
            data: JSON.stringify({
                deal: {
                    id: params.dealId,
                    path: params.path,
                    offerTitle: dealTitle,
                    company: dealCompany
                },
                name: name,
                email: email,
                content: content
            }),
            success: function() {
                showSuccess();
                $('input[type=text], textarea').val('');
                $('#fieldset').prop("disabled", false);
                loadComments(params, 0);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $('#fieldset').prop("disabled", false);
                alert('textStatus : ' + textStatus)
            },
            contentType: "application/json"
        });
    }
}

function validate(name, email, content) {
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
    return ret;
}

function addError(input, message) {
    $("#alert-message").append(getMessage(message) + '<br />');
    $(input).css('border-color', 'red');
    $('#alert').attr("class", "alert alert-danger");
    $('#alert').css('display', 'block');
}

function showSuccess() {
    $("#alert-message").text(getMessage('success_message'));
    $('#alert').attr("class", "alert alert-success");
    $('#alert').css('display', 'block');
}

function restRequiredFields() {
    $('input[type=text], textarea').css('border-color', '#ccc');
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

    var ret = {}

    if (hDiff < 1) {
        ret.duration = Math.ceil(minDiff)
        ret.unit = 'MIN'
    } else if (hDiff < 24) {
        ret.duration = Math.ceil(hDiff)
        ret.unit = 'HOUR'
    } else {
        ret.duration = Math.ceil(dDiff)
        ret.unit = 'DAY'
    }
    return ret;
}
