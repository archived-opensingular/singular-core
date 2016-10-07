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

            var update_action_buttons = function () {
                var choose_btn = $('#' + params.panel_id).find('.file-choose-button');
                var trash_btn = $('#' + params.panel_id).parent().find('.file-trash-button');

                if($('#' + params.panel_id + ' a ').text()){
                    // trash_btn.css('display','block').css('width','35px'); // Somewhat, we need this in order to not destroy the layout
                    trash_btn.show()
                    trash_btn.css('display','block')
                    choose_btn.hide();
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
            		if (!FileUploadPanel.validateInputFile(e, data, params.max_file_size)) {
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
                    $.each(data.result, function (index, file) {
                        //console.log('f',file, $('#' + params.files_id ));
                        $.getJSON(params.add_url,
                            {
                                name: file.name,
                                fileId: file.fileId,
                                hashSHA1: file.hashSHA1,
                                size: file.size,

                            }, function (dataSInstance, status, jqXHR) {
                            	var $link = $('<a target="_blank"></a>').text(dataSInstance.name);
                            	DownloadSupportedBehavior.resolveUrl(params.download_url, dataSInstance.fileId, dataSInstance.name, function(url){
                            		$link.attr('href', url);
                            	});
                                $('#' + params.files_id).empty();
                                $('#' + params.files_id).append($link);
                                $('#' + params.progress_bar_id).hide();

                                update_action_buttons();
                            });
                    });
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    //console.log($('#' + params.progress_bar_id));
                    $('#' + params.progress_bar_id).show();
                    $('#' + params.progress_bar_id + ' .progress-bar').css( 'width',
                        progress + '%' );
                }
            })
	            .on('focus', function() { $(this).closest('.fileinput')   .addClass('focus'); })
	            .on('blur' , function() { $(this).closest('.fileinput').removeClass('focus'); })
            	.prop('disabled', !$.support.fileInput)
                .parent().addClass($.support.fileInput ? undefined : 'disabled');
            
        }

        // Legacy for multple files

        window.FileUploadPanel.validateInputFile = function(e, data, maxSize){
            if ( maxSize && data.files[0].size  > maxSize) {
                toastr.error("Arquivo não pode ser maior que "+FileUploadPanel.humaneSize(maxSize));
                FileUploadPanel.resetFormElement(e);
                return false;
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