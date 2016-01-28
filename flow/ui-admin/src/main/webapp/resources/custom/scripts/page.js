var Page = function () {

    var handleDatePickers = function () {
        if (jQuery().datepicker) {
            $('.date-picker').datepicker({
                rtl: App.isRTL(),
                orientation: "right",
                autoclose: true,
                language: 'pt-BR'
            });
        }
    };

    var handleBootstrapSelect = function () {
        $('.bs-select').selectpicker({
            iconBase: 'fa',
            tickIcon: 'fa-check'
        });
    };

    var handleMultiSelect = function () {
        $('.multi-select').multiSelect();
    };

    var handleSlimScroll = function () {
        $('.scroller').slimScroll({});
    };

    return {
        init: function () {
            handleDatePickers();
            handleBootstrapSelect();
            handleMultiSelect();
            handleSlimScroll();
        }
    };

}();
