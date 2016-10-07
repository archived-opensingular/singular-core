(function () {
    window.SingularResourceHandler = (function () {
        if (typeof $ == "undefined") {
            window.alert("jQuery não foi importado corretamente.");
            return;
        }
        var name = "singular",
            prefix = "/singular-static/resources/",
            styles = [],
            scripts = [],
            favicon = "",
            cookies = decodeURIComponent(document.cookie).split(';'),
            onloaded;

        (function _resolveNameFromCookie() {
            for (i = 0; i < cookies.length; i += 1) {
                if (cookies[i].trim().startsWith('skin')) {
                    name = JSON.parse(cookies[i].replace('skin=', '')).name;
                }
            }
        })();

        function _apply() {

            var __body = $('body'),
                __head = $("head");
            __body.hide();

            window.addEventListener("load", function () {
                __body.show();
                if (onloaded) {
                    onloaded();
                }
            });

            if (favicon) {
                __head.append($("<link rel='shortcut icon' href='" + favicon + "'/>"))
            }

            for (i = 0; i < cookies.length; i += 1) {
                if (cookies[i].trim().startsWith('skin')) {
                    name = JSON.parse(cookies[i].replace('skin=', '')).name;
                }
            }

            for (i = 0; i < styles.length; i += 1) {
                __head.append("<link rel='stylesheet' href='" + prefix + name + styles[i] + "' type='text/css' />");
            }

            for (i = 0; i < scripts.length; i += 1) {
                __body.append("<script src='" + prefix + name + scripts[i] + "' type='text/javascript'></script>");
            }

        }

        return {
            addStyle: function (uri) {
                styles.push(uri);
                return window.SingularResourceHandler;
            },
            addScript: function (uri) {
                scripts.push(uri);
                return window.SingularResourceHandler;
            },
            setFavicon: function (uri) {
                favicon = uri;
                return window.SingularResourceHandler;
            },
            onLoaded: function (callback) {
                onloaded = callback;
                return window.SingularResourceHandler;
            },
            apply: function () {
                _apply();
            }
        }
    }());
}());