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
    var submitBound = false;
    var dealId = null;

    console.log(offer, company, getUrl);

    loadComments();

    /// ------- functions ------------------------------------------------------

    // -- load comments function
    function loadComments() {
        $.get(getUrl, function(data) {
            console.log(data);

            // enable post comment controls
            $('#review-name').prop('disabled', false);
            $('#review-email').prop('disabled', false);
            $('#review-content').prop('disabled', false);
            $('#review-submit').prop('disabled', false);

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

            if (!submitBound) {
                $('#review-submit').bind('click', submit);
                submitBound = true;
            }
        });
    }

    // -- button submit callback
    function submit() {
        if ($('#form')[0].checkValidity()) {
            var comment = {
                deal: {
                    id: dealId,
                    path: path,
                    offerTitle: offer,
                    company: company
                },
                name: $('#review-name').val(),
                email: $('#review-email').val(),
                content: $('#review-content').val()
            };

            $.ajax({
                type: 'POST',
                url: serverUrl + "/comments",
                data: JSON.stringify(comment),
                success: successMessage,
                error: function(jqXHR, textStatus, errorThrown) {
                    alert('textStatus : ' + textStatus)
                },
                contentType: "application/json"
            });
        }
    }

    // -- success callback
    function successMessage() {
        alert('تم الإرسال بنجاح - success')
        name: $('#review-name').val('')
        email: $('#review-email').val('')
        content: $('#review-content').val('')
        loadComments();
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
}
