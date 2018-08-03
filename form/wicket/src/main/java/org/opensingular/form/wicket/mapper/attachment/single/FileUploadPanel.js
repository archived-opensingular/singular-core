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
    if(window.FileUploadPanel === undefined){
        window.FileUploadPanel = function(){};

        window.FileUploadPanel.setup = function(params) {
            $('#' + params.progress_bar_id).hide();

            var panelId = $('#' + params.panel_id);
            var choose_btn = panelId.find('.file-choose-button');
            var trash_btn = panelId.parent().find('.file-trash-button');

            function verifyIfButtonUploadIsDisplayed() {
                return (choose_btn.css('display') === 'block') && (trash_btn.css('display') === 'none') && !$(choose_btn).hasClass('disabled');
            }

            var clickWhitespaceToSelectFile = function () {
                var formControl = panelId.find('.form-control');

                formControl.on("hover", function () {
                    if (verifyIfButtonUploadIsDisplayed()) {
                        $(this).css('cursor', 'pointer');
                    } else {
                        $(this).css('cursor', 'default');
                    }
                });
                formControl.on("click", function () {
                    if (verifyIfButtonUploadIsDisplayed()) {
                        $('#' + params.file_field_id).trigger("click");
                    }
                });
            };

            trash_btn.on("click", function () {
                $('#' + params.file_field_id).trigger("singular:process");
            });

            clickWhitespaceToSelectFile();

            var updateActionButtons = function () {
                if ($('#' + params.panel_id + ' a ').text()) {
                    // trash_btn.css('display','block').css('width','35px'); // Somewhat, we need this in order to not destroy the layout
                    trash_btn.show();
                    trash_btn.css('display', 'block');
                    choose_btn.hide();
                    choose_btn.css('display', 'none');
                } else {
                    choose_btn.show();
                    choose_btn.css('display', 'block');
                    trash_btn.hide();
                    trash_btn.css('display', 'none');
                }
            };

            updateActionButtons();

            if (verifyIfButtonUploadIsDisplayed()) {
                $('#' + params.file_field_id).fileupload({
                    maxChunkSize: ${maxChunkSize},
                    url: params.upload_url,
                    paramName: params.param_name,
                    singleFileUploads: true,
                    dropZone: panelId,
                    dataType: 'json',
                    sequentialUploads: true,
                    limitConcurrentUploads: 1,
                    formData: {
                        'upload_id': params.upload_id
                    },
                    add: function (e, data) {
                        if (!FileUploadPanel.validateInputFile(
                                e,
                                data,
                                params.max_file_size,
                                params.allowed_file_types,
                                params.allowed_file_extensions)) {
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
                        $('#' + params.files_id).html('');
                        $('#' + params.progress_bar_id).hide();
                        $('#' + params.progress_bar_id + ' .progress-bar').css('width', '0%');
                    },
                    done: function (e, data) {
                        //console.log('done',e,data);
                        $.each(data.result, function (index, fileString) {
                            var resp = JSON.parse(fileString);
                            // console.log('f',resp, $('#' + params.files_id ));
                            if (resp.errorMessage) {
                                updateActionButtons();
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
                                            function (url) {
                                                $link.attr('href', url);
                                                if (params.preview_update_callback) {
                                                    Wicket.Ajax.post({u: params.preview_update_callback});
                                                }
                                            }
                                        );
                                        $link.attr('title', dataSInstance.name);
                                        if (DownloadSupportedBehavior.isContentTypeBrowserFriendly(dataSInstance.name)) {
                                            $link.attr('target', '_blank');
                                        }
                                        ;
                                        $('#' + params.files_id).empty().append($link);
                                        $('#' + params.progress_bar_id).hide();

                                        updateActionButtons();
                                        $('#' + params.file_field_id).trigger("singular:process");
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
                    .on('focus', function () {
                        $(this).closest('.fileinput').addClass('focus');
                    })
                    .on('blur', function () {
                        $(this).closest('.fileinput').removeClass('focus');
                    })
                    .prop('disabled', !$.support.fileInput)
                    .parent().addClass($.support.fileInput ? undefined : 'disabled');
            }
            ;
        }

        // Legacy for multple files

        window.FileUploadPanel.validateInputFile = function (e, data, maxSize, allowed_file_types, allowed_file_extensions) {
            if (data.files[0].size === 0) {
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
            	var extension 		 = file.name.substring(file.name.lastIndexOf(".") + 1).toLocaleLowerCase();
            	var invalidType 	 = (jQuery.inArray(file.type, allowed_file_types) < 0);
            	var invalidExtension = (jQuery.inArray(extension, allowed_file_extensions) < 0);
	        	if (invalidType && invalidExtension) {
	        		toastr.error("Tipo de arquivo não permitido.<BR>Permitido apenas: " + allowed_file_extensions.join());
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
        };

        window.FileUploadPanel.humaneSize = function(size){
            var remainder = size;
            var index = 0;
            var names = ['bytes', 'KB', 'MB', 'GB', 'TB'];
            while ((remainder >= 1024) && (index < names.length - 1)) {
                remainder /= 1024; index ++;
            }
            return Math.round(remainder) +" "+ names[index];
        };

    }
})();