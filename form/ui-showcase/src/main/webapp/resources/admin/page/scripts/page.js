var handleDatePickers = function () {
    if (jQuery().datepicker) {
        $('.date-picker').datepicker({
            rtl: Metronic.isRTL(),
            orientation: "right",
            autoclose: true,
            format: 'dd/mm/yyyy',
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
