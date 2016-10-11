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

        window.FileListUploadPanel.setup = function (params) {
            var self = this;
            if (self.last_id == undefined) {
                self.last_id = 1;
            }
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
                    if (!FileListUploadPanel.validateInputFile(e, data, params.max_file_size)) {
                        return false;
                    }
                    var name = '?', fake_id = -1;
                    $.each(data.files, function (index, file) {
                        file['fake_id'] = fake_id = self.last_id++;
                        name = file.name;
                    });
                    var fileList = $('#' + params.fileList_id);
                    var fileElement = $('<li id="upload-box-' + fake_id + '">').addClass('upload-list-item')
                        .append(
                            $('<div>').addClass('list-item-icon')
                                .append($('<a class="list-item-uploading" href="#">')
                                    .append($('<i class="fa fa-file-text"></i>'))),
                            $('<div>').addClass('list-item-content')
                                .append($('<a target="_blank" href="#" class="download-link"></a>')
                                    .append($('<span>').text(name))
                                ),
                            // $('<div class="list-item-progress" id="progress_bar_'+fake_id+'">')
                            //     .append($('<div class="progress-bar" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>')),

                            $('<div class="list-item-action">')
                                .append($('<div class="list-action-uploading" id="progress_bar_' + fake_id + '"><div class="slice"> <div class="bar"></div> <div class="fill"></div></div></div>'))
                                .append($('<a href="javascript:void(0);" class="list-action-remove hidden">')
                                    .append($('<i class="fa fa-close">')))
                        );


                    $('#' + params.component_id).find('.list-detail-empty').hide();
                    $('#' + params.component_id).find('.upload-list-add').show();
                    fileList.append(fileElement);
                    $('#progress_bar_' + data.files[0].fake_id).hide();

                    data.submit();
                    return true;
                },
                done: function (e, data) {
                    $.each(data.result, function (index, fileString) {
                        var resp = JSON.parse(fileString);
                        var fake_id = data.files[index].fake_id;
                        
                        if (resp.errorMessage) {
                        	toastr.error(resp.name + ': ' + resp.errorMessage);

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
                    					$('#progress_bar_' + fake_id).hide();
                    					var box = $('#upload-box-' + fake_id);
                    					box.find('.fa-file-text'        ).removeClass('fa-file-text'        ).addClass('fa-check'            );
                    					box.find('.list-item-uploading' ).removeClass('list-item-uploading' ).addClass('list-item-uploaded' );
                    					box.find('.list-item-content'   );
                    					box.find('.list-action-remove'  ).removeClass('hidden')
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
	                											$('#' + params.component_id).find('.list-detail-empty').show();
	                											$('#' + params.component_id).find('.upload-list-add').hide();
	                										}
	                									}
	                								}
	                    						);
	                    					});
                    					DownloadSupportedBehavior.resolveUrl(
                    							params.download_url,
                    							dataSInstance.fileId,
                    							dataSInstance.name,
                    							function (url) { box.find('.download-link').attr('href', url); }
                    					);
                    				}
                    			}
                        	);
                        }
                        
                    });
                },
                progress: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);

                    if (progress > 50) {
                        $('#progress_bar_' + data.files[0].fake_id + ' > .slice ').addClass('slice-50');
                        $('#progress_bar_' + data.files[0].fake_id + ' > .slice > .bar ').addClass('bar-50');
                        $('#progress_bar_' + data.files[0].fake_id + ' > .slice > .fill').addClass('fill-50');
                    } else {
                        $('#progress_bar_' + data.files[0].fake_id + ' > .slice').removeClass('slice-50');
                        $('#progress_bar_' + data.files[0].fake_id + ' > .slice > .bar ').removeClass('bar-50');
                        $('#progress_bar_' + data.files[0].fake_id + ' > .slice > .fill').removeClass('fill-50');
                    }

                    $('#progress_bar_' + data.files[0].fake_id).show();
                    $('#progress_bar_' + data.files[0].fake_id + ' > .slice > .bar ').css('transform', 'rotate(' + (360 / 100 * progress) + 'deg)')
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


        window.FileListUploadPanel.validateInputFile = function (e, data, maxSize) {
            if (maxSize && data.files[0].size > maxSize) {
                toastr.error("Arquivo não pode ser maior que " + FileListUploadPanel.humaneSize(maxSize));
                FileListUploadPanel.resetFormElement(e);
                return false;
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
