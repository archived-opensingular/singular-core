jQuery(document).ready(function () {

        Wicket.Event.subscribe('/ajax/call/beforeSend', function (evt, attrs, jqXHR, settings) {
            if (enableAJAXPageBlock) {
                $('#blocking_overlay').show();
                window.blocking_overlay_timeoutId = setTimeout(function () {
                    $('#blocking_overlay').css('opacity', '0.5');
                    App.startPageLoading({animate: true});
                }, 1200);
            }

            toastr.clear();
        });
        Wicket.Event.subscribe('/ajax/call/complete', function (evt, attrs, jqXHR, textStatus) {
            if (enableAJAXPageBlock) {
                $('#blocking_overlay').hide();
                $('#blocking_overlay').css('opacity', '0.0');
                App.stopPageLoading();
                if (window.blocking_overlay_timeoutId) {
                    clearTimeout(window.blocking_overlay_timeoutId);
                }
            }

            $('[data-toggle="tooltip"]').tooltip();
        });

        $('[data-toggle="tooltip"]').tooltip();
    });