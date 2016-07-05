(function () {
    "use strict";
    if (window.DownloadSupportedBehavior == undefined) {
        window.DownloadSupportedBehavior = function () {
        };
        window.DownloadSupportedBehavior.ajaxDownload = function (url, fileId, filename) {
            $.ajax({
                type: "POST",
                dataType: 'json',
                url: url + '&fileId=' + fileId + '&fileName=' + filename,
                success: function (response, status, request) {
                    var form = $('<form method="GET" action="' + response.url + '">');
                    $('body').append(form);
                    form.submit();
                    form.remove();
                }
            });
            return false;
        }
        window.DownloadSupportedBehavior.resolveUrl = function (url, fileId, filename, callback) {
        	$.ajax({
        		type: "POST",
        		dataType: 'json',
        		url: url + '&fileId=' + fileId + '&fileName=' + filename,
        		success: function (response, status, request) {
        			callback(response.url);
        		}
        	});
        	return false;
        }
    }
})();


