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