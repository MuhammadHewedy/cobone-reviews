//var serverUrl = "https://cobone-reviews.herokuapp.com"
var serverUrl = "http://localhost:8080"

var buttonList = $(".add-to-cart")

if (buttonList.length <= 0) {
    console.log('this page DOEST NOT have add To cart!')
} else {
    console.log('this page has add To cart!')

    // inject comments html page to html body
    $.get(chrome.extension.getURL('/comments.html'), function(data) {
        var e = $(xpath('//*[@id="page-content-wrapper"]/section[2]/div/div/div[1]'));
        $($.parseHTML(data)).appendTo(e);
    });

    var offer = $(document).find("title").text()
    var company = $('#page-content-wrapper > section:nth-child(8) > div > div > div.singledealcolright > div.singledealpanel.companyinfo > h3').text()
    var path = window.location.pathname.split("/").join("~").substring(4);
    var getUrl = serverUrl + "/comments/" + path;
    var dealId = null;

    loadComments();
}

/// ------- functions ------------------------------------------------------

// -- load comments function
function loadComments() {
    $.get(getUrl, function(data) {
        console.log(data);

        $('#fieldset').prop("disabled", false);

        if (data.length > 0) {
            dealId = data[0].deal.id;
            // draw comments
            $('#comments').empty()
            for (var i = 0; i < data.length; i++) {
                var comment = data[i];
                $('#comments').append("<h4>" + comment.name + "</h4>");
                $('#comments').append("<p>" + comment.content + "</p>");
                $('#comments').append("<p><small>" + new Date(comment.created).toISOString().slice(0, 10) + "</small></p> <hr />");
            }
        } else {
            $('#comments').append("<h5>No comments found - لا توجد تعليقات</h5>");
        }

        $('#review-submit').unbind('click').bind('click', submitHandler);
    });
}

// -- button submit callback
function submitHandler() {
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
                    id: dealId,
                    path: path,
                    offerTitle: offer,
                    company: company
                },
                name: name,
                email: email,
                content: content
            }),
            success: successMessage,
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

// -- success callback & validations
function successMessage() {
    $('#review-name').val('')
    $('#review-email').val('')
    $('#review-content').val('')
    // $('#fieldset').prop("disabled", false);  // already called in loadComments
    $('#success-message-alert').css('display', 'block');
    showRequiredFields(true);
    loadComments();
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
