var Page = function() {

    function handleSlimScroll(){
        $('.scroller').slimScroll({});
    }

    function handleDatePickers() {
        if (jQuery().datepicker) {
            $('.date-picker').datepicker({
                rtl: Metronic.isRTL(),
                orientation: "right",
                autoclose: true,
                language: 'pt-BR'
            });
        }
    }

    function handleBootstrapSelect() {
        $('.bs-select').selectpicker({
            iconBase: 'fa',
            tickIcon: 'fa-check'
        });
    }

    function handleMultiSelect() {
        $('.multi-select').multiSelect();
    }

    return {
        init: function() {
            handleDatePickers();
            handleBootstrapSelect();
            handleMultiSelect();
            handleSlimScroll();
        }
    };

}();
