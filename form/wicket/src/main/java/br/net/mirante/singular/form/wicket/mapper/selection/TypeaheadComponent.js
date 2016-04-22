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
            
            var clear = "<a id='" + container + "_clear' style='position:absolute;top:8px;right:10px;'><span class='glyphicon glyphicon-remove tt-clear-icon'></span></a>";
            var jqInputField = $('#' + container + ' > span  > input');
            
            $(jqInputField).on('typeahead:selected', function (event, selection) {
                var _jqValueField = $('#' + valueField);
                var _jqInputField = $('#' + container + ' > span  > input');
                _jqValueField.val(selection.key);
                _jqValueField.trigger('change');
                _jqInputField.attr('readonly', true);
                _jqInputField.after(clear);
                _jqInputField.blur();
                $('#' + container + '_clear').on('click', function () {
                    $(this).remove();
                    _jqInputField.typeahead('val', '');
                    _jqInputField.removeAttr('readonly');
                    _jqValueField.val('');
                    _jqValueField.trigger('change');
                });
            });

            jqInputField.on('keydown', function (e) {
                var code = e.keyCode || e.which;
                if (code === 9) {
                    if ($(this).val() != '') {
                        e.preventDefault();
                        var downKey = jQuery.Event('keydown');
                        downKey.keyCode = downKey.which = 40;
                        $(this).trigger(downKey);
                        var enter = jQuery.Event('keydown');
                        enter.keyCode = enter.which = 13;
                        $(this).trigger(enter);
                    }
                }
            });

            if (jqInputField.val()) {
                jqInputField.attr('readonly', true);
                jqInputField.after(clear);
                $('#' + container + '_clear').on('click', function () {
                    var _jqValueField = $('#' + valueField);
                    var _jqInputField = $('#' + container + ' > span  > input');
                    $(this).remove();
                    _jqInputField.removeAttr('readonly');
                    _jqValueField.val('');
                    _jqValueField.trigger('change');
                });
            }

        };
    }

}());
