jQuery(document).ready(function () {
    Wicket.Event.subscribe('/ajax/call/complete', function (evt, attrs, jqXHR, textStatus) {

        var fieldsByTopPosition = {};
        jQuery('div > div.can-have-error').each(function() {
            var $this = $(this);
            var topPosition = $this.offset().top;

            var fieldsList = fieldsByTopPosition[topPosition];
            if (fieldsList == undefined) {
                fieldsList = [];
                fieldsByTopPosition[topPosition] = fieldsList;
            }

            fieldsList.push($this);

        });

        for (var topPosition in fieldsByTopPosition) {
            if (fieldsByTopPosition.hasOwnProperty(topPosition)) {
                var maxFieldHeight = 0;
                var fieldsList = fieldsByTopPosition[topPosition];

                for (var i = 0; i < fieldsList.length; i++) {
                    var field = fieldsList[i];
                    var fieldHeight = field.height();

                    if (maxFieldHeight < fieldHeight) {
                        maxFieldHeight = fieldHeight;
                    }
                }

                for (var j = 0; j < fieldsList.length && maxFieldHeight > 0; j++) {
                    var field = fieldsList[j];
                    $(field).css('min-height', maxFieldHeight);
                }
            }
        }
    });
});