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
    if (window.FileListUploadPanel == undefined) {
        window.FileListUploadPanel = function () {
        };

        window.FileListUploadPanel.setUploadItemState = function(panel_id, box_id, state, errorMessage) {
        	var $panel = $('#' + panel_id);
        	var $box   = $('#upload-box-' + box_id);

    		$panel.toggleClass('FileListUploadPanel_empty', ($panel.find('.upload-list-item').length == 0));

        	$box
        	    .removeClass('FileListUploadPanel_uploading')
        	    .removeClass('FileListUploadPanel_success')
        	    .removeClass('FileListUploadPanel_error')
        	    .addClass('FileListUploadPanel_' + state);
        	
        	if (state == 'error') {
        		$box.addClass('FileListUploadPanel_error');
            	$box.find('.fa-file-text'        ).removeClass('fa-file-text'        ).addClass('fa-remove'          ).css('color','red');
                $box.find('.list-item-uploading' ).removeClass('list-item-uploading' ).addClass('list-item-uploaded' );
                $box.find('.list-action-remove'  ).removeClass('hidden').click(function (e) {
                	$box.remove();
                	window.FileListUploadPanel.setUploadItemState(panel_id, box_id, null);
            	});

                //toastr.error(errorMessage);
                $box.find('a').attr('href','javascript:void(0)').tooltip({
        			trigger  : 'hover',
        			title    : errorMessage});
        	}
        };
        window.FileListUploadPanel.setup = function (params) {
            var self = this;
            if (self.last_id == undefined) {
                self.last_id = 1;
            }
            FileListUploadPanel.setUploadItemState(params.component_id, null, null);

            $('#' + params.file_field_id).fileupload({
                url: params.upload_url,
                paramName: params.param_name,
                singleFileUploads: true,
                dropZone: $('#' + params.component_id),
                dataType: 'json',
                limitConcurrentUploads: 1,
                sequentialUploads: true,
                formData: {
                    'upload_id': params.upload_id
                },
                add: function (e, data) {
                    var name = '?';
                    var fake_id = -1;
                    $.each(data.files, function (index, file) {
                        file['fake_id'] = fake_id = self.last_id++;
                        name = file.name;
                    });
                    var fileList = $('#' + params.fileList_id);
                    
                    var fileElement = $(''
                        + '<li id="upload-box-' + fake_id + '" class="upload-list-item">'
                        + '  <div class="list-item-icon">'
                        + '    <a class="list-item-uploading" href="#"><i class="fa fa-file-text"></i></a>'
                        + '  </div>'
                        + '  <div class="list-item-content">'
                        + '    <a target="_blank" href="#" class="download-link"><span>' + name + '</span></a>'
                        + '  </div>'
                        + '  <div class="list-item-action">'
                        + '    <div class="list-action-uploading" id="progress_bar_' + fake_id + '">'
                        + '      <div class="slice">'
                        + '        <div class="bar"></div>'
                        + '        <div class="fill"></div>'
                        + '      </div>'
                        + '    </div>'
                        + '    <a href="javascript:void(0);" class="list-action-remove hidden"><i class="fa fa-close"></i></a>'
                        + '  </div>'
                        + '</li>'
                        + '');
                    FileListUploadPanel.setUploadItemState(params.component_id, fake_id, 'uploading');
                    fileList.append(fileElement);
                    //$('#progress_bar_' + fake_id).hide();
                    
                    if (FileListUploadPanel.validateInputFile(
                    		e,
                    		data,
                    		params.component_id,
                    		params.max_file_size,
                    		params.allowed_file_types)) {
                        data.submit();
                    }
                    
                    return true;
                },
                done: function (e, data) {
                    $.each(data.result, function (index, fileString) {
                        var resp = JSON.parse(fileString);
                        var fake_id = data.files[index].fake_id;
                        
                        if (resp.errorMessage) {
                            FileListUploadPanel.setUploadItemState(params.component_id, fake_id, 'error', resp.errorMessage);

                        } else {
                            $.getJSON(
                                params.add_url,
                                {
                                    name: resp.name,
                                    fileId: resp.fileId,
                                    hashSHA1: resp.hashSHA1,
                                    size: resp.size
                                },
                                function (dataSInstance, status, jqXHR) {
                                    if (status == 'success') {
                                    	FileListUploadPanel.setUploadItemState(params.component_id, fake_id, 'success');
                                        //$('#progress_bar_' + fake_id).hide();
                                        var $box = $('#upload-box-' + fake_id);
                                        $box.find('.fa-file-text'        ).removeClass('fa-file-text'        ).addClass('fa-check'            );
                                        $box.find('.list-item-uploading' ).removeClass('list-item-uploading' ).addClass('list-item-uploaded' );
                                        $box.find('.list-action-remove'  ).removeClass('hidden')
                                            .click(function (e) {
                                                $.getJSON(params.remove_url,
                                                    {
                                                        fileId: dataSInstance.fileId
                                                    },
                                                    function (data, status, jqXHR) {
                                                        if (status == 'success') {
                                                            $('#upload-box-' + fake_id).remove();
                                                            var fileList = $('#' + params.fileList_id).find('li');
                                                            if (fileList.length == 0) {
                                                            	FileListUploadPanel.setUploadItemState(params.component_id, fake_id, 'empty');
                                                                //$('#' + params.component_id).find('.list-detail-empty').show();
                                                                //$('#' + params.component_id).find('.upload-list-add').hide();
                                                            }
                                                        }
                                                    }
                                                );
                                            });
                                        DownloadSupportedBehavior.resolveUrl(
                                                params.download_url,
                                                dataSInstance.fileId,
                                                dataSInstance.name,
                                                function (url) { $box.find('.download-link').attr('href', url); }
                                        );
                                    }
                                }
                            );
                        }
                        
                    });
                },
                progress: function (e, data) {
                	var fake_id = data.files[0].fake_id;
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    var $item = $('#upload-box-' + fake_id);

                    if (progress > 50) {
                    	$item.find('.slice ').addClass('slice-50');
                        $item.find('.slice > .bar ').addClass('bar-50');
                        $item.find('.slice > .fill').addClass('fill-50');
                    } else {
                        $item.find('.slice').removeClass('slice-50');
                        $item.find('.slice > .bar ').removeClass('bar-50');
                        $item.find('.slice > .fill').removeClass('fill-50');
                    }

                    $item.find('.slice > .bar').css('transform', 'rotate(' + (360 / 100 * progress) + 'deg)');
                }
            })
            .prop('disabled', !$.support.fileInput)
            .parent().addClass($.support.fileInput ? undefined : 'disabled');
        };

        window.FileListUploadPanel.resetFormElement = function (e) {
            var $input = $(e.target || e.srcElement);
            $input.wrap('<form>').closest('form').get(0).reset();
            $input.unwrap();

            // Prevent form submission
            e.stopPropagation();
            e.preventDefault();
        };

        window.FileListUploadPanel.validateInputFile = function (e, data, panel_id, maxSize, allowed_file_types) {
            if (maxSize && data.files[0].size > maxSize) {
                FileListUploadPanel.setUploadItemState(panel_id, data.files[0].fake_id, 'error', "Arquivo não pode ser maior que " + FileListUploadPanel.humaneSize(maxSize));
                //FileListUploadPanel.resetFormElement(e);
                return false;
            }
            
            if (allowed_file_types && allowed_file_types.length > 0) {
            	var file = data.files[0];
            	var extension 		 = file.name.substring(file.name.lastIndexOf("/") + 1);
            	var invalidType 	 = (jQuery.inArray(file.type, allowed_file_types) < 0);
            	var invalidExtension = (jQuery.inArray(extension, allowed_file_types) < 0);
	        	if (invalidType && invalidExtension) {
	        		FileListUploadPanel.setUploadItemState(panel_id, data.files[0].fake_id, 'error', "Tipo de arquivo não permitido");
	        		return false;
	        	}
        	}
            return true;
        };

        window.FileListUploadPanel.humaneSize = function (size) {
            var remainder = size;
            var index = 0;
            var names = ['bytes', 'KB', 'MB', 'GB', 'TB'];
            while ((remainder >= 1024) && (index < names.length - 1)) {
                remainder /= 1024;
                index++;
            }
            return Math.round(remainder) + " " + names[index];
        }
    }
})();
