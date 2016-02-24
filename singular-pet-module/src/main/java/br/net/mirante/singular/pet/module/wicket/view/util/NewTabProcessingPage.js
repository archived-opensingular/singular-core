var NewTabProcessingPage = function () {

    var _reloadOpener = function () {
        window.opener.location.reload();
    }

    var _redirectTo = function (url) {
        window.location = url;
    }

    var _redirectToAndReloadOpener = function (url) {
        _reloadOpener();
        _redirectTo(url);
    }

    var _close = function () {
        window.close();
    }

    var _closeThisAndReloadOpener = function () {
        _reloadOpener();
        _close();
    }

    return {
        closeThisAndReloadOpener: _closeThisAndReloadOpener,
        redirectTo: _redirectTo,
        redirectToAndReloadOpener: _redirectToAndReloadOpener
    }
}();