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
    if (window.SingularFormPanel == undefined) {
        window.SingularFormPanel = function() {};
        
        window.SingularFormPanel.initFocus = function(containerId) {
        	if (document.activeElement === window.document.body) {
        		// only if no other component is focused
        		$('#'+containerId)
        			.find('input:not([type=hidden]),select,textarea,button,object,a')
        			.filter(':visible')
        			.first()
        			.each(function(){ this.focus(); });
        	}
        };
        
        Wicket.Event.subscribe('/ajax/call/complete', function (evt, attrs, jqXHR, textStatus) {
	
	        var fieldsByTopPosition = {};
	
	        jQuery('div > div.can-have-error').each(function () {
	            var $this       = $(this);
	            var topPosition = $this.offset().top;
	            var fieldsList  = fieldsByTopPosition[topPosition];
	
	            if (fieldsList == undefined) {
	                fieldsList                       = [];
	                fieldsByTopPosition[topPosition] = fieldsList;
	            }
	
	            fieldsList.push($this);
	        });
	
	        for (var topPosition in fieldsByTopPosition) {
	            if (fieldsByTopPosition.hasOwnProperty(topPosition)) {
	                var maxFieldHeight = 0;
	                var fieldsList     = fieldsByTopPosition[topPosition];
	                var i;
	
	                //cleanup
	                for (i = 0; i < fieldsList.length; i++) {
	                    $(fieldsList[i]).css('min-height', "");
	                }
	
	                for (i = 0; i < fieldsList.length; i++) {
	                    var field       = fieldsList[i];
	                    var fieldHeight = field.height();
	                    if (maxFieldHeight < fieldHeight) {
	                        maxFieldHeight = fieldHeight;
	                    }
	                }
	
	                for (i = 0; i < fieldsList.length && maxFieldHeight > 0; i++) {
	                    $(fieldsList[i]).css('min-height', maxFieldHeight);
	                }
	            }
	        }
        });
    }
});