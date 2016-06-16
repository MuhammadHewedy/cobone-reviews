// var serverUrl = "https://cobone-reviews.herokuapp.com"
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
        });
    }
})();

/// ------- functions ----------------------------------------------------------

// -- load comments function
function loadComments(params) {
    $.get(serverUrl + "/comments/" + params.path, function(data) {
        console.log(data);

        $('#fieldset').prop("disabled", false);

        if (data.length > 0) {
            params.dealId = data[0].deal.id;
            // draw comments
            $('#comments').empty()
            for (var i = 0; i < data.length; i++) {
                var comment = data[i];
                $('#comments').append("<h4>" + comment.name + "</h4>");
                $('#comments').append("<p>" + comment.content + "</p>");
                var diffObj = getDateDiff(new Date(comment.created));
                $('#comments').append("<p><small>" + getMessage('since') + diffObj.duration + getMessage(diffObj.unit) +
                    "</small></p> <hr />");
            }
        } else {
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

    if (name && content) {
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
                $('#review-name').val('')
                $('#review-email').val('')
                $('#review-content').val('')
                    // $('#fieldset').prop("disabled", false);  // already called in loadComments
                $('#success-message-alert').css('display', 'block');
                showRequiredFields(true);
                loadComments(params);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $('#fieldset').prop("disabled", false);
                alert('textStatus : ' + textStatus)
            },
            contentType: "application/json"
        });
    } else {
        showRequiredFields();
    }
}

function showRequiredFields(reset) {
    $('#review-name').css('border-color', reset ? '#ccc' : 'red');
    $('#review-content').css('border-color', reset ? '#ccc' : 'red');
    $('#required-fields-alert').css('display', reset ? 'none' : 'block');
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
