(function () {
    var $timepicker = $("#${timePickerMarkupId}");

    $timepicker.timepicker(${jsonParams});

    if (${onInit}) {
        $timepicker.timepicker().on('show.timepicker', function (e) {
            if (e.time.value == '0:00') {
                $timepicker.timepicker('setTime', '00:00 AM');
            }
        });

        $timepicker.on('keydown', function (e) {
            switch (e.keyCode) {
                case 9:
                    $(this).timepicker('hideWidget');
            }
        });
        $timepicker.on('remove',
            function (e) {
                console.log('teste');
                $(this).timepicker('remove');
            });
    }
})();