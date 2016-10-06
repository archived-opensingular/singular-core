;(function () {
    "use strict";
    if (window.substringMatcher == undefined) {
        window.substringMatcher = function (value_list) {
            var clearText = function (x) {
                return S(x).latinise().s;
            };
            return function findMatches(q, cb) {
                var matches     = [];
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

(function ($) {
    "use strict";
    window.SingularTypeahead = {
        configure: function (container, valueField) {

            var SINGULAR_BLUR_KEY   = 'SingularBlurKey',
                SINGULAR_CHANGE_KEY = 'SingularChangeKey',
                SINGULAR_PROCESS    = 'singular:process',
                SINGULAR_VALIDATE   = 'singular:validate';

            //console.log(container);
            $('#' + container + ' span').first()
                                        .addClass("input-icon input-icon-sm right")
                                        .prepend('<i class="fa fa-chevron-down"></i>');

            var clearText = function (x) {
                return S(x).latinise().s.toUpperCase();
            };

            var clear =
                    "<a id='" + container + "_clear' style='position:absolute;top: calc(50% - 8px);right:10px;'>" +
                    "   <span class='glyphicon glyphicon-remove tt-clear-icon'></span>" +
                    "</a>";

            var typeaheadField          = '#' + container + ' > span  > input',
                typeaheadFieldJQueryRef = $(typeaheadField),
                subscriberMetadata      = {jumpToNext: false, factor: 0},
                clearCalled             = false,
                inputJQueryRef          = $('#' + valueField);

            var Typeahead_ = typeaheadFieldJQueryRef.data('tt-typeahead');

            function checkIfHasUpdate(eventName) {
                var events = $('#' + valueField).data('events');
                if (events && events.hasOwnProperty(eventName)) {
                    for (var i = 0; i <= events[eventName].length; i += 1) {
                        if (events[eventName][i] && events[eventName][i].handler.toString().indexOf('Wicket') > 0) {
                            return true;
                        }
                    }
                }
                return false;
            }

            function hasAjaxUpdate() {
                return checkIfHasUpdate('change') || checkIfHasUpdate(SINGULAR_PROCESS) || checkIfHasUpdate(SINGULAR_VALIDATE);
            }

            if (hasAjaxUpdate()) {
                Wicket.Event.subscribe(Wicket.Event.Topic.AJAX_CALL_COMPLETE, function (e, at) {
                    if (subscriberMetadata.jumpToNext) {
                        var focusables = $(":focusable");
                        $(focusables[focusables.index($(typeaheadField)) + subscriberMetadata.factor]).focus();
                        subscriberMetadata.jumpToNext = false;
                        clearCalled                   = false;
                    }
                })
            }

            function updateValue(newValue) {
                if (inputJQueryRef.data(SINGULAR_BLUR_KEY)) {
                    window.clearTimeout(inputJQueryRef.data(SINGULAR_BLUR_KEY));
                    inputJQueryRef.removeData(SINGULAR_BLUR_KEY);
                }
                inputJQueryRef.data(SINGULAR_CHANGE_KEY, window.setTimeout(function () {
                    var oldValue = inputJQueryRef.val();
                    inputJQueryRef.val(newValue);
                    if (oldValue !== newValue) {
                        console.log("triggered singular process");
                        inputJQueryRef.trigger(SINGULAR_PROCESS);
                    }
                    inputJQueryRef.removeData(SINGULAR_CHANGE_KEY);
                }, 50));
            }

            function onClearClick(e) {
                clearCalled = true;
                $('#' + container + '_clear').remove();
                $('#' + container + ' > span > .fa-chevron-down').show();
                typeaheadFieldJQueryRef.removeAttr('readonly');
                typeaheadFieldJQueryRef.typeahead('val', '');
                Typeahead_.enable();
                updateValue('');
                typeaheadFieldJQueryRef.focus();
            }

            function onClearMouseDown(e) {
                if (inputJQueryRef.data(SINGULAR_BLUR_KEY)) {
                    window.clearTimeout(inputJQueryRef.data(SINGULAR_BLUR_KEY));
                    inputJQueryRef.removeData(SINGULAR_BLUR_KEY);
                }
                inputJQueryRef.data(SINGULAR_CHANGE_KEY, "MOUSEDOWN");
            }

            function focusNextComponent(keydownEvent) {
                if (hasAjaxUpdate()) {
                    subscriberMetadata.jumpToNext = true;
                    subscriberMetadata.factor     = keydownEvent.shiftKey ? -1 : 1;
                } else {
                    var focusables = $(":focusable");
                    $(focusables[focusables.index($(typeaheadField)) + (keydownEvent.shiftKey ? -1 : 1)]).focus();
                }
            }

            function appendClearButton() {
                typeaheadFieldJQueryRef.attr('readonly', true);
                typeaheadFieldJQueryRef.after(clear);

                var jQueryRefClearButton = $('#' + container + '_clear');

                jQueryRefClearButton.on('click', onClearClick);
                jQueryRefClearButton.on('click', onClearMouseDown);

                $('#' + container + ' > span > .fa-chevron-down').hide();

                Typeahead_.disable();
            }

            typeaheadFieldJQueryRef.on('typeahead:selected', function (event, selection) {
                clearCalled = false;
                updateValue(selection.key);
                appendClearButton();
                if (subscriberMetadata.jumpToNext) {
                    onTypeaheadFieldBlur();
                }
            });

            typeaheadFieldJQueryRef.on('keydown', function (event) {
                var code = event.keyCode || event.which;
                if (code === 9 && hasAjaxUpdate()) {
                    focusNextComponent(event);
                }
                // ativa o campo se estiver readonly e backspace ou delete
                if (typeaheadFieldJQueryRef.attr('readonly') && (code === 8) || (code === 46)) {
                    event.stopPropagation();
                    event.preventDefault();
                    onClearClick(event);
                }
            });
            typeaheadFieldJQueryRef.on('keypress', function (event) {
                var code = event.keyCode || event.which;
                // ativa o campo se estiver readonly e diferente de tab
                if (typeaheadFieldJQueryRef.attr('readonly') && code !== 9) {
                    event.preventDefault();
                    typeaheadFieldJQueryRef.typeahead('val', String.fromCharCode(code));
                    $('#' + valueField).val(String.fromCharCode(code));

                    $('#' + container + '_clear').remove();
                    typeaheadFieldJQueryRef.removeAttr('readonly');
                    Typeahead_.enable();
                    Typeahead_.open();
                }
            });
            typeaheadFieldJQueryRef.on('keyup', function (event) {
                var code = event.keyCode || event.which;
                //diferente de up arrow, down arrow, tab
                if (!typeaheadFieldJQueryRef.attr('readonly') && code !== 38 && code !== 40 && code !== 9 && typeaheadFieldJQueryRef.val()) {
                    // mantém o primeiro da lista selecionado
                    if (Typeahead_.menu.getActiveSelectable() !== Typeahead_.menu.getTopSelectable()) {
                        Typeahead_.menu.setCursor(Typeahead_.menu.getTopSelectable());
                    }
                } else if ((code === 8 || code === 46) && !typeaheadFieldJQueryRef.val() && !clearCalled) {
                    event.preventDefault();
                    onClearClick(event);
                }
            });

            typeaheadFieldJQueryRef.on('blur', onTypeaheadFieldBlur);

            function onTypeaheadFieldBlur() {
                if (!inputJQueryRef.data(SINGULAR_CHANGE_KEY)) {
                    inputJQueryRef.data(SINGULAR_BLUR_KEY, window.setTimeout(function () {
                        if (!inputJQueryRef.data(SINGULAR_CHANGE_KEY)) {
                            inputJQueryRef.trigger(SINGULAR_VALIDATE);
                            console.log("triggered singular validate");
                        }
                        inputJQueryRef.removeData(SINGULAR_BLUR_KEY);
                    }, 100));
                }
            }

            //previne que ao teclar Tab, o primeiro item seja selecionado quando ninguém foi marcado
            Typeahead_._onTabKeyed = function onTabKeyed(type, $e) {
                var $selectable;
                if ($selectable = Typeahead_.menu.getActiveSelectable()) {
                    $e.preventDefault();
                    if (hasAjaxUpdate()) {
                        focusNextComponent($e);
                    }
                    Typeahead_.select($selectable);
                    if (!hasAjaxUpdate()) {
                        focusNextComponent($e);
                    }
                } else if (possuiDadoInvalido() && hasAjaxUpdate()) {
                    // necessário para manter o foco após o ajax
                    focusNextComponent($e);
                }
            };


            function possuiDadoInvalido() {
                if (!typeaheadFieldJQueryRef.attr('readonly') && typeaheadFieldJQueryRef.val()) {
                    var $selectad = $.grep(Typeahead_.menu._getSelectables(), function (obj, i) {
                        return clearText(typeaheadFieldJQueryRef.val()) == clearText(Typeahead_.menu.getSelectableData($(obj)).val);
                    });
                    return Typeahead_.menu._getSelectables().size() == 0 || $selectad.length == 0;
                }
                return false;
            }

            // limpa o campo se tiver um valor inválido
            typeaheadFieldJQueryRef.on('typeahead:change', function ($e, selection) {
                if (possuiDadoInvalido()) {
                    $e.stopPropagation();
                    Typeahead_.close();
                    typeaheadFieldJQueryRef.typeahead('val', '');
                    updateValue('');
                } else if (typeaheadFieldJQueryRef.val()) {
                    var $selectad = $.grep(Typeahead_.menu._getSelectables(), function (obj, i) {
                        return clearText(typeaheadFieldJQueryRef.val()) === clearText(Typeahead_.menu.getSelectableData($(obj)).val);
                    });
                    if ((Typeahead_.menu._getSelectables().size() == 1 || $selectad.length == 1) && typeaheadFieldJQueryRef.val() !== Typeahead_.menu.getSelectableData($selectad).val) {
                        $e.preventDefault();
                        Typeahead_.select($selectad);
                    }
                }
            });

            if (typeaheadFieldJQueryRef.val() && possuiDadoInvalido()) {
                appendClearButton();
            }
        }
    };
}(jQuery));