(function () {
    var enabledDates = ${enabledDates},
        autoclose = ${autoclose},
        clearBtn = ${clearBtn},
        todayBtn = ${todayBtn},
        todayHighlight = ${todayHighlight},
        showOnFocus = ${showOnFocus},
        datePickerMarkupId = "${datePickerMarkupId}",
        inputMarkupId = "${inputMarkupId}",
        configureBeforeShowDay = ${configureBeforeShowDay},
        startDate = "${startDate}",
        changeEvent = "${changeEvent}";

    var $datepicker = $("#" + datePickerMarkupId),
        $input = $("#" + inputMarkupId),
        settings = {
            rtl: App.isRTL(),
            orientation: 'right',
            autoclose: autoclose,
            language: 'pt-BR',
            clearBtn: clearBtn,
            todayBtn: todayBtn,
            todayHighlight: todayHighlight,
            showOnFocus: showOnFocus,
            format: "dd/mm/yyyy"
        };

    if (startDate && startDate.match(/\d{2}\/\d{2}\/\d{4}/)) {
        settings.startDate = startDate;
    }

    if (configureBeforeShowDay) {
        settings.beforeShowDay = function (date) {
            return enabledDates.indexOf(date.toISOString().substring(0, 10)) >= 0;
        }
    }

    if (!$input.prop('disabled')) {
        $datepicker.datepicker(settings).on('changeDate', function () {
            var input = $input;
            var format = $datepicker.data('dateFormat').toUpperCase();
            if (format === 'DD/MM/YYYY' && /\d{1,2}\/\d{1,2}\/\d{4}/.test(input.val())
                || format === 'DD/MM' && /\d{1,2}\/\d{1,2}/.test(input.val())) {
                input.trigger(changeEvent);
            }
        });
    }
})();