(function () {
    var $timepicker = $("#${timePickerMarkupId}");

    $timepicker.timepicker(${jsonParams});

    if (${onInit}) {
        $timepicker.timepicker().on('show.timepicker', function (e) {

            if (e.time.value == '0:00') {
                $timepicker.timepicker('setTime', '00:00 AM');
            }
        });
    }

    $timepicker.on('hide.timepicker', function(e) {
        $timepicker.trigger('onUpdateTime');
    });

    $timepicker.on('keydown', function (e) {
        switch (e.keyCode) {
            case 9:
                $(this).timepicker('hideWidget');
        }
    });

    $timepicker.on('keyup', function (e) {
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

})();