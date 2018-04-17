(function () {
    var $datepicker = $("#${datePickerMarkupId}");
    var $input = $("#${inputMarkupId}");

    var settings = {
        rtl: App.isRTL(),
        orientation: 'right',
        autoclose: true,
        language: 'pt-BR'
    };

    if (${configureBeforeShowDay}) {
        settings.beforeShowDay = function (date) {
            var dd = date.getDate();
            if (dd.toString().length === 1) {
                dd = "0" + dd;
            }
            var mm = date.getMonth() + 1;
            if (mm.toString().length === 1) {
                mm = "0" + mm;
            }
            var yyyy = date.getFullYear();
            return ${enabledDates}.indexOf(dd + "/" + mm + "/" + yyyy) >= 0;
        }
    }

    if (!$input.prop('disabled')) {
        $datepicker.datepicker(settings).on('changeDate', function () {
            var input = $input;
            var format = $datepicker.data('dateFormat').toUpperCase();
            if (format === 'DD/MM/YYYY' && /\d{1,2}\/\d{1,2}\/\d{4}/.test(input.val())
                || format === 'DD/MM' && /\d{1,2}\/\d{1,2}/.test(input.val())) {
                input.trigger('${changeEvent}');
            }
        });
    }
})();