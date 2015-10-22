var handleDatePickers = function () {
    if (jQuery().datepicker) {
        $('.date-picker').datepicker({
            rtl: Metronic.isRTL(),
            orientation: "right",
            autoclose: true,
            language: 'pt-BR'
        });
    }
};

var Page = function () {

    return {
        init: function () {
            handleDatePickers();
        }
    };

}();
