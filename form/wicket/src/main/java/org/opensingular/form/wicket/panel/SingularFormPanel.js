/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

jQuery(document).ready(function () {
    "use strict";

    /**
     * Solução temporaria para configuração da direção das anotações, o componente deve ser
     * refatorado de modo que não seja necessarios hacks
     */
    function configureDynamicAnnotations() {
        $('.singular-form-action-preview').each(function () {
            var $preview = $(this),
                parent = $preview.parent()[0];
            if (typeof parent === 'undefined') {
                return;
            }
            var parentOffsetLeft = $(parent).offset().left;
            if (parentOffsetLeft < ($(window).width() - parentOffsetLeft)) {
                $preview.css('right', 'auto');
                $preview.css('left', '0');
            } else {
                $preview.css('right', '0');
                $preview.css('left', 'auto');
            }
        });
    }
    
    $(window).resize(configureDynamicAnnotations);
    $(window).ready(configureDynamicAnnotations);
    // This execution has some delay to let some javascript that changes the HTML run, e.g. open a modal
    Wicket.Event.subscribe("/ajax/call/complete", function() {setTimeout(configureDynamicAnnotations, 500);});


    function align(selector) {
        var fieldsByTopPosition = {};

        jQuery(selector).each(function () {
            var $this = $(this);
            if (!$this.hasClass("upload-panel-body")) {//deve ignorar o painel de anexo e o ckeditor
                var topPosition = $this.offset().top;
                var fieldsList = fieldsByTopPosition[topPosition];

                if (fieldsList === undefined) {
                    fieldsList = [];
                    fieldsByTopPosition[topPosition] = fieldsList;
                }
                fieldsList.push($this);
            }
        });
        
        function heightAsFloat($el) {
        	if (!$el[0])
        		return undefined;
        	var rect = $el[0].getBoundingClientRect();
        	return (rect.height) ? rect.height : (rect.bottom - rect.top);
        }

        for (var topPosition in fieldsByTopPosition) {
            if (fieldsByTopPosition.hasOwnProperty(topPosition)) {
                var maxFieldHeight = 0;
                var maxLabelHeight = 0;
                var fieldsList = fieldsByTopPosition[topPosition];
                var i;

                //cleanup
                for (i = 0; i < fieldsList.length; i++) {
                    removeStyle($(fieldsList[i]));
                }

                //redimensionar div
                for (i = 0; i < fieldsList.length; i++) {
                    var $field = fieldsList[i];
                    var fieldHeight = heightAsFloat($field);
                    if (maxFieldHeight < fieldHeight) {
                        maxFieldHeight = fieldHeight;
                    }
                    
                    //redimensionar labels
                    $field.children().each(function(){  
                    	var $label = $(this).closest("label");
                    	var labelHeight = heightAsFloat($label);
                    	if(labelHeight !== null && labelHeight !== 0){
                    		removeStyle($label);
                    	}
                    });

                    $field.children().each(function(){  
                    	var $label = $(this).closest("label");
                    	var labelHeight = heightAsFloat($label);
                    	if(labelHeight !== null && labelHeight !== 0){
                            if (maxLabelHeight < labelHeight) {
                            	maxLabelHeight = labelHeight;
                            }
                        }
                    });
                   
                    $field.children().each(function(){  
                    	var $label = $(this).closest("label");
                    	var labelHeight = heightAsFloat($label);
                    	if(labelHeight !== null && labelHeight !== 0){
                    		applyStyle($label, maxLabelHeight ); 
                    	}
                    	if(i === (fieldsList.length-1)){
                    		maxLabelHeight = 0;                    		
                    	}
                    });
                    //redimensionar labels - fim
                }

                for (i = 0; i < fieldsList.length && maxFieldHeight > 0; i++) {
                    applyStyle($(fieldsList[i]), maxFieldHeight );
                }
            }
        }
    }

    function applyStyle($field, maxFieldHeight){
        $field.css("min-height", maxFieldHeight);
        //field.css("max-height", maxFieldHeight);// max height gera efeito colateral negativo no STypeHTML do showcase
    }

    function removeStyle($field){
        $field.css("min-height", "");
        //field.css("max-height", ""); // max height gera efeito colateral negativo no STypeHTML do showcase
    }

    if (window.SingularFormPanel === undefined) {
        window.SingularFormPanel = function () {
        };

        window.SingularFormPanel.initFocus = function (containerId) {
            if (document.activeElement === window.document.body) {
                // only if no other component is focused
                $("#" + containerId)
                    .find("input:not([type=hidden]),select,textarea,button,object,a")
                    .filter(":visible")
                    .first()
                    .each(function () {
                        this.focus();
                    });
            }
        };
    } 

    var delay = (function(){
        var timer = 0;
        return function(callback, ms){
            clearTimeout (timer);
            timer = setTimeout(callback, ms);
        };
    })();

    function configureLabelBarWidths() {
    	$('.labelBar').each(function () {
    		var $labelBar = $(this);
    		var $controlLabel = $labelBar.find('label.control-label')
    		var $actionBars = $labelBar.children('.decorator-actions').children();
    		var widths = $.map($actionBars, function(it) { return it.clientWidth; });
    		var actionBarsWidth = widths.reduce(function(a,b) { return a+b; }, 16);
    		
    		$controlLabel.css('max-width', (actionBarsWidth) ? ('calc(100% - ' + actionBarsWidth + 'px)') : '100%');
    	});
    }
    
    function alignRowComponents() {
    	configureLabelBarWidths();
    	align("div > div.labelBar");
        align("div > div.can-have-error");
        align("div > span.subtitle_comp");
    }

    //Registrando função em vários momentos 
    
    //Não remover
    alignRowComponents();

    //Não remover
    $(document).ready(function(){
    	alignRowComponents(); 
    });

    //registra na abertura da modal
    $('body').on('shown.bs.modal', '.modal', function() {
    	alignRowComponents();
    });

    //registra a cada chamda ajax
    Wicket.Event.subscribe("/ajax/call/complete", alignRowComponents);
  
    //registra no resize do browser
    $(window).resize(function() { delay(alignRowComponents, 10); });
    
});