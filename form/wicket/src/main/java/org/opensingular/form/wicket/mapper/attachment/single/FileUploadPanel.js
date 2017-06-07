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

(function () {
    "use strict";
    if(window.FileUploadPanel == undefined){
        window.FileUploadPanel = function(){};

        window.FileUploadPanel.setup = function(params) {
            $('#' + params.progress_bar_id).hide();

            var click_whitespace_to_select_file = function () {
                var formControl = $('#' + params.panel_id).find('.form-control');
                var fileField = $('#'+ params.file_field_id);
                
                var choose_btn = $('#' + params.panel_id).find('.file-choose-button');
                var trash_btn = $('#' + params.panel_id).parent().find('.file-trash-button');

                formControl.on("hover", function () {
                    if((choose_btn.css('display') == 'block') && (trash_btn.css('display') == 'none') ){
                        $(this).css( 'cursor', 'pointer' );
                    }else{
                        $(this).css( 'cursor', 'default' );
                    }
                });
                
                formControl.on("click", function (){
                    if((choose_btn.css('display') == 'block') && (trash_btn.css('display') == 'none') ){
                        fileField.trigger("click");
                    }
                });
            }
            
            click_whitespace_to_select_file();

            var update_action_buttons = function () {
                var choose_btn = $('#' + params.panel_id).find('.file-choose-button');
                var trash_btn = $('#' + params.panel_id).parent().find('.file-trash-button');

                if($('#' + params.panel_id + ' a ').text()){
                    // trash_btn.css('display','block').css('width','35px'); // Somewhat, we need this in order to not destroy the layout
                    trash_btn.show()
                    trash_btn.css('display','block')
                    choose_btn.hide();
                    choose_btn.css('display','none')
                }else{
                    choose_btn.show();
                    choose_btn.css('display','block')
                    trash_btn.hide();
                    trash_btn.css('display','none')
                }
            }

            update_action_buttons();

            $('#' + params.file_field_id).fileupload({
                url: params.upload_url,
                paramName: params.param_name,
                singleFileUploads: true,
                dropZone: $('#' + params.panel_id ),
                dataType: 'json',
                sequentialUploads: true,
                limitConcurrentUploads: 1,
                formData:{
                    'upload_id' : params.upload_id,
                },
                add: function(e,data) {
                    if (!FileUploadPanel.validateInputFile(
                    		e,
                    		data,
                    		params.max_file_size,
                    		params.allowed_file_types)) {
                        return false;
                    }
                    if (data.autoUpload || (data.autoUpload !== false && $(this).fileupload('option', 'autoUpload'))) {
                        data.process().done(function () {
                            data.submit();
                        });
                    }
                    return true;
                },
                start: function (e, data) {
                    // console.log($('#files_" + fieldId + "'));
                    $('#' + params.files_id ).html('');
                    $('#' + params.progress_bar_id).hide();
                    $('#' + params.progress_bar_id + ' .progress-bar').css('width','0%');
                },
                done: function (e, data) {
                    //console.log('done',e,data);
                    $.each(data.result, function (index, fileString) {
                        var resp = JSON.parse(fileString);
                        console.log('f',resp, $('#' + params.files_id ));
                        if (resp.errorMessage) {
                        	update_action_buttons();
                        	toastr.error(resp.name + ': ' + resp.errorMessage);
                        	$('#' + params.progress_bar_id).hide();

                        } else {
	                        $.getJSON(params.add_url,
	                            {
	                                name: resp.name,
	                                fileId: resp.fileId,
	                                hashSHA1: resp.hashSHA1,
	                                size: resp.size
	                            },
	                            function (dataSInstance, status, jqXHR) {
	                                var $link = $('<a></a>').text(dataSInstance.name);
	                                DownloadSupportedBehavior.resolveUrl(
	                            		params.download_url,
	                            		dataSInstance.fileId,
	                            		dataSInstance.name,
	                            		function(url) { $link.attr('href', url); }
	                        		);
                                    if(DownloadSupportedBehavior.isContentTypeBrowserFriendly(dataSInstance.name)){
                                        $link.attr('target', '_blank');
                                    }
	                                $('#' + params.files_id).empty().append($link);
	                                $('#' + params.progress_bar_id).hide();
	
	                                update_action_buttons();
	                            }
                            );
                        }
                    });
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    //console.log($('#' + params.progress_bar_id));
                    $('#' + params.progress_bar_id).show()
                    	.find('.progress-bar').css('width', progress + '%');
                }
            })
            .on('focus', function() { $(this).closest('.fileinput')   .addClass('focus'); })
            .on('blur' , function() { $(this).closest('.fileinput').removeClass('focus'); })
            .prop('disabled', !$.support.fileInput)
            .parent().addClass($.support.fileInput ? undefined : 'disabled');
        }

        // Legacy for multple files

        window.FileUploadPanel.validateInputFile = function (e, data, maxSize, allowed_file_types) {
            if (data.files[0].size == 0) {
            	toastr.error("Arquivo não pode ser de tamanho 0 (zero)");
                FileUploadPanel.resetFormElement(e);
                return false;
            }
            
            if (maxSize && data.files[0].size > maxSize) {
            	toastr.error("Arquivo não pode ser maior que " + FileUploadPanel.humaneSize(maxSize));
            	FileUploadPanel.resetFormElement(e);
            	return false;
            }
            
            if (allowed_file_types && allowed_file_types.length > 0) {
            	var file = data.files[0];
            	var extension 		 = file.name.substring(file.name.lastIndexOf(".") + 1);
            	var invalidType 	 = (jQuery.inArray(file.type, allowed_file_types) < 0);
            	var invalidExtension = (jQuery.inArray(extension, allowed_file_types) < 0);
	        	if (invalidType && invalidExtension) {
	        		toastr.error("Tipo de arquivo não permitido");
	        		FileUploadPanel.resetFormElement(e);
	        		return false;
	        	}
        	}
            return true;
        };

        window.FileUploadPanel.resetFormElement = function(e) {
            var $input = $(e.target || e.srcElement);
            $input.wrap('<form>').closest('form').get(0).reset();
            $input.unwrap();

            // Prevent form submission
            e.stopPropagation();
            e.preventDefault();
        }

        window.FileUploadPanel.humaneSize = function(size){
            var remainder = size;
            var index = 0;
            var names = ['bytes', 'KB', 'MB', 'GB', 'TB'];
            while ((remainder >= 1024) && (index < names.length - 1)) {
                remainder /= 1024; index ++;
            }
            return Math.round(remainder) +" "+ names[index];
        }

    }
})();