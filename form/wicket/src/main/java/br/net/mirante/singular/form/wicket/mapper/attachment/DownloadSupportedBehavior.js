(function () {
    if (window.DownloadSupportedBehavior == undefined) {
        window.DownloadSupportedBehavior = function () {
        };
        window.DownloadSupportedBehavior.ajaxDownload = function (url, hashSHA1, filename) {
            $.ajax({
                type: "POST",
                dataType: 'json',
                url: url + '&hashSHA1=' + hashSHA1 + '&fileName=' + filename,
                success: function (response, status, request) {
                    var form = $('<form method="GET" action="' + response.url + '">');
                    $('body').append(form);
                    form.submit();
                    form.remove();
                }
            });
        }
    }
})();


