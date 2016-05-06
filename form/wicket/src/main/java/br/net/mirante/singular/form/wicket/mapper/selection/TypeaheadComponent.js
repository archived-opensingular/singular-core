;(function () {
    "use strict";
    if (window.substringMatcher == undefined) {
        window.substringMatcher = function (value_list) {
            var clearText = function (x) {
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

(function ($) {
    "use strict";
    window.SingularTypeahead = {
        configure: function (container, valueField) {
        	var clearText = function (x) {return S(x).latinise().s.toUpperCase();};

            var clear =
                    "<a id='" + container + "_clear' style='position:absolute;top:8px;right:10px;'>" +
                    "   <span class='glyphicon glyphicon-remove tt-clear-icon'></span>" +
                    "</a>",
            typeaheadField = '#' + container + ' > span  > input',
            $typeaheadField = $(typeaheadField),
            subscriberMetadata = {jumpToNext: false,factor: 0},
            clearCalled = false;
            
            var Typeahead_ = $typeaheadField.data('tt-typeahead');

            function hasAjaxUpdate() {
                var events = $('#' + valueField).data('events');
                if (events && events.hasOwnProperty('change')) {
                    for (var i = 0; i <= events.change.length; i += 1) {
                        if (events.change[i].handler.toString().indexOf('Wicket') > 0) {
                            return true;
                        }
                    }
                }
                return false;
            }
            if (hasAjaxUpdate()) {
                Wicket.Event.subscribe(Wicket.Event.Topic.AJAX_CALL_COMPLETE, function (e,at) {
                    if (subscriberMetadata.jumpToNext) {
                        var focusables = $(":focusable");
                        $(focusables[focusables.index($(typeaheadField)) + subscriberMetadata.factor]).focus();
                        subscriberMetadata.jumpToNext = false;
                        clearCalled = false;
                    }
                })
            }
            function updateValue(newValue) {
                var $valueField = $('#' + valueField);
                $valueField.val(newValue);
                $valueField.trigger('change');
            }
            
            function onClear(e, value) {
            	clearCalled = true;
                $('#' + container + '_clear').remove();
                $typeaheadField.removeAttr('readonly');
                Typeahead_.enable();
                $typeaheadField.typeahead('val', value || '');
            	updateValue(value || '');
                $typeaheadField.focus();
            }

            function focusNextComponent(keydownEvent) {
                if (hasAjaxUpdate()) {
                    subscriberMetadata.jumpToNext = true;
                    subscriberMetadata.factor = keydownEvent.shiftKey ? -1 : 1;
                } else {
                    var focusables = $(":focusable");
                    $(focusables[focusables.index($(typeaheadField)) + (keydownEvent.shiftKey ? -1 : 1)]).focus();
                }
            }

            function appendClearButton() {
                $typeaheadField.attr('readonly', true);
                $typeaheadField.after(clear);
                $('#' + container + '_clear').on('click', onClear);
                Typeahead_.disable();
            }

            $typeaheadField.on('typeahead:selected', function (event, selection) {
            	clearCalled = false;
                updateValue(selection.key);
                appendClearButton();
                if (subscriberMetadata.jumpToNext) {
                	$typeaheadField.blur();
                }
            });
            $typeaheadField.on('keydown', function (event) {
            	var code = event.keyCode || event.which;
            	// ativa o campo se estiver readonly e backspace ou delete
            	if ($typeaheadField.attr('readonly') && (code === 8) || (code === 46)) {
            		event.stopPropagation();
            		event.preventDefault();
            		onClear(event);
            	}
            	console.log('keydown');
            });
            $typeaheadField.on('keypress', function (event) {
            	var code = event.keyCode || event.which;
            	// ativa o campo se estiver readonly e diferente de tab
            	if ($typeaheadField.attr('readonly') && code !== 9) {
            		event.preventDefault();
            		$typeaheadField.typeahead('val', String.fromCharCode(code));
            		$('#' + valueField).val(String.fromCharCode(code));
            		
            		$('#' + container + '_clear').remove();
                    $typeaheadField.removeAttr('readonly');
                    Typeahead_.enable();
                    $typeaheadField.focus();
            	}
            	console.log('keypress');
            });
            $typeaheadField.on('keyup', function (event) {
            	var code = event.keyCode || event.which;
            	//diferente de up arrow, down arrow, tab
            	if(!$typeaheadField.attr('readonly') && code !== 38 && code !== 40 && code !== 9 && $typeaheadField.val()){
            		// mantém o primeiro da lista selecionado
            		if(Typeahead_.menu.getActiveSelectable() !== Typeahead_.menu.getTopSelectable()){
            			Typeahead_.menu.setCursor(Typeahead_.menu.getTopSelectable());
            		}
            	} else if((code === 8 || code === 46) && !$typeaheadField.val() && !clearCalled){
            		event.preventDefault();
            		onClear(event);
            		console.log('keyup');
            	}
            });
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
                } else if(possuiDadoInvalido() && hasAjaxUpdate()){
                	// necessário para manter o foco após o ajax
                	focusNextComponent($e);
        		}
            }
            function possuiDadoInvalido(){
            	if(!$typeaheadField.attr('readonly') && $typeaheadField.val()){
        			var $selectad = $.grep(Typeahead_.menu._getSelectables(), function(obj,i){
        				return clearText($typeaheadField.val()) == clearText(Typeahead_.menu.getSelectableData($(obj)).val);
        			});
        			return Typeahead_.menu._getSelectables().size() == 0 || $selectad.length == 0;
        		}
            	return false;
            }
            // limpa o campo se tiver um valor inválido
            $typeaheadField.on('blur', function (keydownEvent) {
        		if(possuiDadoInvalido()){
        			$typeaheadField.typeahead('val','');
        			updateValue('');
        			//TODO Lucas Lopes - esta validando obrigatoriedade
        		}
            });
            
            if ($typeaheadField.val() && possuiDadoInvalido()) {
                appendClearButton();
            }
        }
    };
}(jQuery));