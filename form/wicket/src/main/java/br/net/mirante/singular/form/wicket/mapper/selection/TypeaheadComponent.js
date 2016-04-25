;(function () {
    "use strict";
    if (window.substringMatcher == undefined) {
        window.substringMatcher = function (value_list) {
            this.clearText = function (x) {
                return S(x).latinise().s;
            };
            return function findMatches(q, cb) {
                var matches = [];
                var substrRegex = new RegExp(clearText(q), 'i');
                $.each(value_list, function (i, value) {
                    if (substrRegex.test(clearText(value['value']))) {
                        matches.push(value);
                    }
                });
                cb(matches);
            };
        };
    }
}());

(function () {
    "use strict";
    window.SingularTypeahead = window.SingularTypeahead || {};
    if (!SingularTypeahead.configure) {

        SingularTypeahead.configure = function (container, valueField) {

            var clear =
                    "<a id='" + container + "_clear' style='position:absolute;top:8px;right:10px;'>" +
                    "   <span class='glyphicon glyphicon-remove tt-clear-icon'></span>" +
                    "</a>",
                containerSelector = '#' + container + ' > span  > input',
                jqInputField = $(containerSelector);

            function updateValue(newValue) {
                var _jqValueField = $('#' + valueField);
                _jqValueField.val(newValue);
                _jqValueField.trigger('change');
            }

            function onClear() {
                var _jqInputField = $(containerSelector);
                $(this).remove();
                _jqInputField.typeahead('val', '');
                _jqInputField.removeAttr('readonly');
                updateValue('');
            }

            $(jqInputField).on('typeahead:selected', function (event, selection) {
                var _jqInputField = $(containerSelector);
                updateValue(selection.key);
                _jqInputField.attr('readonly', true);
                _jqInputField.after(clear);
                $('#' + container + '_clear').on('click', onClear);
                _jqInputField.blur();
            });

            jqInputField.on('keydown', function (e) {
                var code = e.keyCode || e.which;
                if (code === 9) {
                    if ($(this).val()) {
                        e.preventDefault();
                        var downKey = jQuery.Event('keydown');
                        downKey.keyCode = downKey.which = 40;
                        $(this).trigger(downKey);
                        var enter = jQuery.Event('keydown');
                        enter.keyCode = enter.which = 13;
                        $(this).trigger(enter);
                        var tab = jQuery.Event('keydown');
                        tab.keyCode = tab.which = 9;
                        var focusables = $(":focusable");
                        $(focusables[focusables.index($(containerSelector)) + (e.shiftKey ? -1 : 1)]).focus();
                    }
                }
            });

            if (jqInputField.val()) {
                jqInputField.attr('readonly', true);
                jqInputField.after(clear);
                $('#' + container + '_clear').on('click', onClear);
            }

        };
    }

}());