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
                var preview = $(this),
                    parent = preview.parent()[0];
                if (typeof parent === 'undefined') {
                    return;
                }
                var parentOffsetLeft = $(parent).offset().left;
                if (parentOffsetLeft < ($(window).width() - parentOffsetLeft)) {
                    preview.css('right', 'auto');
                    preview.css('left', '0');
                } else {
                    preview.css('right', '0');
                    preview.css('left', 'auto');
                }
            }
        );
    }

    $(window).resize(configureDynamicAnnotations);
    $(window).ready(configureDynamicAnnotations);
    Wicket.Event.subscribe("/ajax/call/complete", configureDynamicAnnotations);

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
                    var field = fieldsList[i];
                    var fieldHeight = field.height();
                    if (maxFieldHeight < fieldHeight) {
                        maxFieldHeight = fieldHeight;
                    }
                    
                    //redimensionar labels
                    field.children().each(function(){  
                    	var label = $(this).closest( $("label"));
                    	if(label.height() !== null && label.height() !== 0){
                    		removeStyle(label);
                    	}
                    });

                    field.children().each(function(){  
                    	var label = $(this).closest( $("label"));
                    	if(label.height() !== null && label.height() !== 0){
                            if (maxLabelHeight < label.height()) {
                            	maxLabelHeight = label.height();
                            }
                        }
                    });
                   
                    field.children().each(function(){  
                    	var label = $(this).closest( $("label"));
                    	if(label.height() !== null && label.height() !== 0){
                    		applyStyle(label, maxLabelHeight ); 
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

    function applyStyle(field, maxFieldHeight){
        field.css("min-height", maxFieldHeight);
        //field.css("max-height", maxFieldHeight);// max height gera efeito colateral negativo no STypeHTML do showcase
    }

    function removeStyle(field){
        field.css("min-height", "");
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
    
    function alignHelpBlockAndErros() {
        align("div > div.can-have-error");
        align("div > span.subtitle_comp");
    }

    //Registrando função em vários momentos 
    
    //Não remover
    alignHelpBlockAndErros();

    //Não remover
    $(document).ready(function(){
    	alignHelpBlockAndErros(); 
    });

    //registra na abertura da modal
    $('body').on('shown.bs.modal', '.modal', function() {
    	alignHelpBlockAndErros();
    });

    //registra a cada chamda ajax
    Wicket.Event.subscribe("/ajax/call/complete", alignHelpBlockAndErros);
  
    //registra no resize do browser
    $(window).resize(function() {
    	delay(function(){
	   	  	align('div > div.can-have-error');
	   	  	align('div > span.help-block');
    	}, 10);
    });
    
});