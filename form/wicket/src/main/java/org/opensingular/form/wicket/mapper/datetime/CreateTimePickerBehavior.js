(function () {

    var $timepicker = $("#${timePickerMarkupId}");

    if ($('.bootstrap-timepicker-widget').size() < 1) {
        configTimerPicker();
        removeWidgetWhenClickedOutside();

    }

    function configTimerPicker() {

        if ($timepicker.is(":visible")) {
            $timepicker.timepicker(${jsonParams});
            $timepicker.timepicker().on('show.timepicker', function (e) {
                if (e.time.value == '0:00') {
                    $timepicker.timepicker('setTime', '00:00 AM');
                }
                removeExtraTimepickerWidget();
            });

            /**
             * This function is extremely important. This solve this problem:
             * A timePicker who target another timePicker, and the user use the click mouse to navigate between them in the same time will create two timePicker in the together.
             * The problem is that the second timePicker will be show twice, and the second one will lost the capacity to be destroy when the mouse is clicked in the page.
             * So, this function will verify if have a timerpicker-widget open, if it have, it will be destroy.
             */
            function removeExtraTimepickerWidget() {
                if ($('.bootstrap-timepicker-widget').size() > 1) {
                    $('.bootstrap-timepicker-widget')[0].remove();
                }
            }

            /**
             * The process will be used when Change.
             * This event should be there, for the case of visible attribute depends on TimePicker.
             */
            $timepicker.on('change', function (e) {
                $timepicker.trigger('onProcessTime');
            });

            /**
             * The validate will be used when the timePicker hide.
             */
            $timepicker.on('hide.timepicker', function (e) {
                $timepicker.trigger('onValidateTime');
            });

            $timepicker.on('remove', function (e) {
                $timepicker.timepicker('remove');

            });

            $timepicker.on('keydown', function (e) {
                switch (e.keyCode) {
                    case 9: {
                        $timepicker.timepicker('hideWidget');
                        $('.bootstrap-timepicker-widget').remove();
                    }
                }
            });

            $timepicker.on('keyup', function (e) {
                /**
                 * Logica responsável por atualizar o valor durante a digitação, para caso seja digitado 900 fique 9:00 ao invez de 90:00
                 */
                if (!(e.keyCode == 8 || e.keyCode == 46)) {
                    var timeArray = $timepicker.val().replace(/[^0-9\:]/g, '').split(':');

                    hour = timeArray[0] ? timeArray[0].toString() : timeArray.toString();
                    minute = timeArray[1] ? timeArray[1].toString() : '';

                    if (hour.length == 2 && minute.length == 1) {
                        minute = hour.charAt(1) + minute;
                        hour = hour.charAt(0);
                        $timepicker.val(hour + ":" + minute);
                    } else if (minute.length == 2 && hour.length == 1 && ((e.keyCode >= 48 && e.keyCode <= 57) || (e.keyCode >= 96 && e.keyCode <= 105))) {
                        hour = hour.charAt(0) + minute.charAt(0);
                        minute = minute.charAt(1) + e.key;
                        $timepicker.val(hour + ":" + minute);
                    }

                }
            });
        }
    }

    /**
     * This will garanted that the timePicker will be closed when the click it outside the timePicker element.
     * The class css 'sgl-timer-picker' have to be included in the input field.
     */
    function removeWidgetWhenClickedOutside() {

        $(document).on('click', function (e) {
            if ($('.bootstrap-timepicker-widget').size() >= 1 && !$(e.target).attr('class').includes('sgl-timer-picker')) {
                $('.bootstrap-timepicker-widget')[0].remove();
            }
        });
    }

})();