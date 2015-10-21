var handleDatePickers = function () {
    if (jQuery().datepicker) {
        $('.date-picker').datepicker({
            rtl: Metronic.isRTL(),
            orientation: "right",
            autoclose: true
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
