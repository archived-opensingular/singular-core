RangeSliderMapper = {
    init: function (parent, fieldOneId, fieldTwoId, disable) {

        var dummyInput = document.createElement("input");

        var fieldOneVal = $(fieldOneId).val();
        var fieldTwoVal = $(fieldTwoId).val();

        if (fieldOneVal && fieldTwoVal) {
            $(dummyInput).val(fieldOneVal + ';' + fieldTwoVal);
        }

        parent.appendChild(dummyInput);

        $(dummyInput).ionRangeSlider({
            keyboard: true,
            min: 0,
            max: 120,
            type: 'double',
            disable: disable
        });

        $(dummyInput).on('change', function () {
            var v = $(dummyInput).val().split(';');
            $(fieldOneId).val(v[0]);
            $(fieldTwoId).val(v[1]);
        });
    }
};