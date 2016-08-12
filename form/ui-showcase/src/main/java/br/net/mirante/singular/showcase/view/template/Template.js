jQuery(document).ready(function () {

        Wicket.Event.subscribe('/ajax/call/beforeSend', function (evt, attrs, jqXHR, settings) {
        	var blockImmediately = $('#'+attrs.c).is('button,input[type=button],input[type=submit],a,i');
        	var showLoading = enableAJAXPageBlock;
            if(attrs && attrs['ep']){
                $.each(attrs['ep'], function(i,v){ //console.log("v",v);
                    if(v["name"] == "forceDisableAJAXPageBlock"){
                        showLoading = !v["value"]; //console.log('showLoading', showLoading);
                    }
                    if(v["name"] == "forceImmediateAJAXPageBlock"){
                    	blockImmediately = v["value"]; //console.log('showLoading', showLoading);
                    }
                });
            }

            if (showLoading) {
            	if (blockImmediately) {
            		$('#blocking_overlay').css('opacity', '0.2').show();
            	}
                window.blocking_overlay_timeoutId = setTimeout(function () {
                	$('#blocking_overlay').css('opacity', '0.5').show();
                    App.startPageLoading({animate: true});
                }, 1200);
            }

            toastr.clear();
        });
        Wicket.Event.subscribe('/ajax/call/complete', function (evt, attrs, jqXHR, textStatus) {
            if (enableAJAXPageBlock) {
                $('#blocking_overlay').hide();
                $('#blocking_overlay').css('opacity', '0.0');
                App.stopPageLoading();
                if (window.blocking_overlay_timeoutId) {
                    clearTimeout(window.blocking_overlay_timeoutId);
                }
            }

            $('[data-toggle="tooltip"]').tooltip();
        });

        $('[data-toggle="tooltip"]').tooltip();
    });