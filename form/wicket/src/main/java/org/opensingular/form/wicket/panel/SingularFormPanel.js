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
(function($){

$(function () {
    "use strict";

    /**
     * Solução temporaria para configuração da direção das anotações, o componente deve ser
     * refatorado de modo que não seja necessarios hacks
     */
    function configureDynamicAnnotations() {
        $('.singular-form-action-preview').each(function () {
            var $preview = $(this);
            var parent = $preview.parent()[0];
            if (typeof parent === 'undefined') {
                return;
            }
            var parentOffsetLeft = $(parent).offset().left;
            var previewWidth = $preview.width();
            var windowWidth = $(window).width();
            
            if (previewWidth < (windowWidth - parentOffsetLeft)) {
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
    Wicket.Event.subscribe("/ajax/call/complete", function() { setTimeout(configureDynamicAnnotations, 500); });

    function topAsFloat(el) {
        if (!el) return undefined;
        var rect = el.getBoundingClientRect();
        //return rect.top;
        
        // subtracting children's margin-top to correct for checkbox 
        var marginTop = parseFloat($(el).css('marginTop'));
        var tops = [rect.top].concat($.map($(el).children().filter(':visible'), function(it) { return $(it).offset().top - parseInt($(it).css('marginTop')); }));
        var minTop = Math.min.apply(null, tops);
        return minTop;
    }
    function heightAsFloat(el) {
        if (!el) return undefined;
        var rect = el.getBoundingClientRect();
        return (rect.height) ? rect.height : (rect.bottom - rect.top);
    }
    function widthAsFloat(el) {
        if (!el) return undefined;
        var rect = el.getBoundingClientRect();
        return (rect.width) ? rect.width : (rect.right - rect.left);
    }
    
    function max($elements, func) {
        return ($elements.length == 0) ? 0: Math.max.apply(null, $.map($elements, func));
    }
    function min($elements, func) {
        return ($elements.length == 0) ? 0: Math.min.apply(null, $.map($elements, func));
    }
    
    function normalizeHeight($elements) {
        var h = max($elements, heightAsFloat);
        $elements.css('min-height', h);
    }
    
    function normalizeLabelSubtitleHeight($elements) {
        function selectLabels    ($el) { return $el.children('.labelBar');}//.find('label.control-label'); };
        function selectSubtitles ($el) { return $el.children('.subtitle_comp'); };
        function selectCheckboxes($el) { return $el.children('.checkbox'); };
        
        var $labels    = selectLabels($elements);
        var $subtitles = selectSubtitles($elements);
        
        $elements .css("min-height", "");
        $labels   .css("min-height", "");
        $subtitles.css("min-height", "");

        var totalPreFieldHeight = max($elements, function(it) {
            var $it = $(it);
            var $label = selectLabels($it);
            var $subtitle = selectSubtitles($it);
            var labelHeight    = heightAsFloat($label   [0]) || 0;
            var subtitleHeight = heightAsFloat($subtitle[0]) || 0;
            return labelHeight + subtitleHeight;
        });
        
        // adjusting common fields
        if ($subtitles.is(':visible')) {
            $subtitles.css("min-height", "").css("display", "block");
            
            for (var i=0; i<$elements.length; i++) {
                var $label    = selectLabels   ($($elements[i]));
                var $subtitle = selectSubtitles($($elements[i]));
                
                $subtitle.css("min-height", totalPreFieldHeight - heightAsFloat($label[0]));
            }

        } else {
            normalizeHeight($labels);
        }
        
        if ($labels.is(':visible')) {
            
            // adjusting checkboxes
            var $checkboxes = selectCheckboxes($elements);
            if ($checkboxes.is(':visible')) {
                $checkboxes.children('label')
                .css('marginTop', totalPreFieldHeight + 'px');
                $checkboxes.children('.decorator-actions.align-left')
                .removeClass('align-left')
                .css('margin-top', '-' + $checkboxes.css('margin-top'))
                .css('position', 'absolute')
                .css('top', 0)
                .css('right', 0);
            }
        }
    }

    var delay = (function(){
        var timer = 0;
        return function(callback, ms){
            clearTimeout(timer);
            timer = setTimeout(callback, ms);
        };
    })();

    function configureLabelBarWidths() {
        $('.labelBar').each(function () {
            var $labelBar       = $(this);
            var $controlLabel   = $labelBar.find('label.control-label');
            var $actionBars     = $labelBar.children('.decorator-actions').children();
            var widths          = $.map($actionBars, widthAsFloat);
            var actionBarsWidth = widths.reduce(function(a,b) { return a + b; }, 10);
            
            $controlLabel.css('max-width', (actionBarsWidth) ? ('calc(100% - ' + actionBarsWidth + 'px)') : '100%');
        });
    }

    function alignFields() {
        var fields     = $.makeArray($('.singular-form-panel-generated,.singular-form-panel-body-container').find('.can-have-error.form-group'));
        var ttl = 10;
        
        function iterate() {
            if (ttl-- <= 0) return;
            
            var minTop     = min(fields, topAsFloat);
            var topFields  = fields.filter(function(it){ return Math.abs(topAsFloat(it) - minTop) <= 2; });
            var $topFields = $(topFields);
    
            normalizeLabelSubtitleHeight($topFields);
    
            normalizeHeight($topFields);
            
            for (var i=fields.length-1; i>=0; i--) {
                if (topFields.includes(fields[i]))
                    fields.splice(i, 1);
            }
            if (fields.length > 0)
                iterate();
        }
        iterate();
    }

    if (window.SingularFormPanel === undefined) {
        window.SingularFormPanel = function () {};

        window.SingularFormPanel.initFocus = function (containerId) {
            if (document.activeElement === window.document.body) {
                // only if no other component is focused
                $("#" + containerId)
                    .find("input:not([type=hidden]),select,textarea,button,object,a")
                    .filter(":visible")
                    .first()
                    .each(function() { this.focus(); });
            }
        };
    } 
    
    function alignRowComponents() {
        configureLabelBarWidths();
        alignFields();
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

})(jQuery);
