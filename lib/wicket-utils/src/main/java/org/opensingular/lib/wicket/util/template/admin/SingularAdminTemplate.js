;(function () {
    Wicket.Event.subscribe('/ajax/call/beforeSend', function (evt, attrs) {
        var $c = (typeof attrs.c === 'string') ? $('#' + attrs.c) : $(attrs.c);
        var blockImmediately = $c.is('button,input[type=button],input[type=submit],a,i');
        var showLoading = enableAJAXPageBlock;
        if (attrs && attrs['ep']) {
            $.each(attrs['ep'], function (i, v) { //console.log("v",v);
                if (v["name"] === "forceDisableAJAXPageBlock") {
                    showLoading = !v["value"]; //console.log('showLoading', showLoading);
                }
                if (v["name"] === "forceImmediateAJAXPageBlock") {
                    blockImmediately = v["value"]; //console.log('showLoading', showLoading);
                }
            });
        }
        if (showLoading) {
            if (blockImmediately) {
                $('#blocking_overlay').css('opacity', '0.2').show();
            }
            window.blocking_overlay_timeoutId = setTimeout(function () {
                $('#blocking_overlay').css('opacity', '0.5').show();
                App.startPageLoading({animate: true});
            }, 1200);
        }
        toastr.clear();
    });
    Wicket.Event.subscribe('/ajax/call/complete', function () {
        if (enableAJAXPageBlock) {
            var $blocking_overlay = $('#blocking_overlay');
            $blocking_overlay.hide();
            $blocking_overlay.css('opacity', '0.0');
            App.stopPageLoading();
            if (window.blocking_overlay_timeoutId) {
                clearTimeout(window.blocking_overlay_timeoutId);
            }
        }
        $('[data-toggle="tooltip"]').tooltip({trigger: 'hover'});
    });
    $('[data-toggle="tooltip"]').tooltip({trigger: 'hover'});
})();