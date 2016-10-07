/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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


