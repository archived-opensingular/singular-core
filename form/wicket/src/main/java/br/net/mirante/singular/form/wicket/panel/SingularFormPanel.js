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