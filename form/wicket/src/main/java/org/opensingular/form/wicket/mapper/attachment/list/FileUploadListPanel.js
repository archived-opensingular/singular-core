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
    if (window.FileUploadListPanel == undefined) {
        window.FileUploadListPanel = function () {
        };

        window.FileUploadListPanel.setUploadItemState = function (panel_id, box_id, state, errorMessage) {
            var $panel = $('#' + panel_id);
            var $box = $('#upload-box-' + box_id);

            $panel.toggleClass('FileUploadListPanel_empty', ($panel.find('.upload-list-item').length == 0));

            $box
                .removeClass('FileUploadListPanel_uploading')
                .removeClass('FileUploadListPanel_success')
                .removeClass('FileUploadListPanel_error')
                .addClass('FileUploadListPanel_' + state);

            if (state == 'error') {
                $box.addClass('FileUploadListPanel_error');
                $box.find('.fa-file-text').removeClass('fa-file-text').addClass('fa-remove').css('color', 'red');
                $box.find('.list-item-uploading').removeClass('list-item-uploading').addClass('list-item-uploaded');
                $box.find('.list-action-remove').removeClass('hidden').click(function (e) {
                    $box.remove();
                    window.FileUploadListPanel.setUploadItemState(panel_id, box_id, null);
                });

                //toastr.error(errorMessage);
                $box.find('a').attr('href', 'javascript:void(0)').tooltip({
                    trigger: 'hover',
                    html: true,
                    title: errorMessage
                });
            }

            if (state == 'success') {
                $('#progress_bar_' + box_id).hide();
            }

            if (state == 'empty') {
                $('#' + panel_id).find('.list-detail-empty').show();
                $('#' + panel_id).find('.upload-list-add').hide();
            }

            if (state == 'uploading') {
                $('#progress_bar_' + box_id).show();
                $('#' + panel_id).find('.list-detail-empty').hide();
                $('#' + panel_id).find('.upload-list-add').show();
            }
        };
        window.FileUploadListPanel.setup = function (params) {
            var self = this;
            if (self.last_id == undefined) {
                self.last_id = 1;
            }
            FileUploadListPanel.setUploadItemState(params.component_id, null, null);

            var modalId = 'modal-' + params.component_id;

            var popupTemplate =
                '<div id="' + modalId + '" class="modal fade">' +
                '  <div class="modal-dialog modal-belver">' +
                '    <div class="modal-content">' +
                '      <div class="modal-header">' +
                '        <button type="button" class="close" data-dismiss="modal">&times;</button>' +
                '        <h4 class="modal-title">Excluir anexo selecionado</h4>' +
                '      </div>' +
                '      <div class="modal-body" >' +
                '        <div class="form-group">' +
                '          <label>Confirma a exclusão?</label>' +
                '        </div >' +
                '      </div >' +
                '      <div class="modal-footer">' +
                '        <button type="button" class="btn cancel-btn" data-dismiss="modal">Cancelar</button>' +
                '        <button type="button" class="btn confirm-btn" data-dismiss="modal">Remover</button>' +
                '      </div>' +
                '    </div>' +
                '  </div>' +
                '</div>';

            var $modal = $(popupTemplate).modal({show: false});

            $('#' + params.file_field_id).fileupload({
                maxChunkSize: ${maxChunkSize},
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
                        + '    <a href="#" title="'+name+'" class="download-link"><span>' + name + '</span></a>'
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
                    FileUploadListPanel.setUploadItemState(params.component_id, fake_id, 'uploading');
                    fileList.append(fileElement);

                    if (FileUploadListPanel.validateInputFile(
                            e,
                            data,
                            params.component_id,
                            params.max_file_size,
                            params.allowed_file_types,
                            params.allowed_file_extensions)) {
                        data.submit();
                        if(${showPageBlock}) {
                            $('#blocking_overlay').css('opacity', '0.2').show();
                            window.blocking_overlay_timeoutId = setTimeout(function () {
                                $('#blocking_overlay').css('opacity', '0.5').show();
                                App.startPageLoading({animate: true});
                            }, 1200);
                        }
                    }

                    return true;
                },
                done: function (e, data) {
                    $.each(data.result, function (index, fileString) {

                        var resp = JSON.parse(fileString);
                        var fake_id = data.files[index].fake_id;

                        if (resp.errorMessage) {
                            FileUploadListPanel.setUploadItemState(params.component_id, fake_id, 'error', resp.errorMessage);
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
                                        FileUploadListPanel.setUploadItemState(params.component_id, fake_id, 'success');
                                        var $box = $('#upload-box-' + fake_id);
                                        $box.find('.fa-file-text').removeClass('fa-file-text').addClass('fa-check');
                                        $box.find('.list-item-uploading').removeClass('list-item-uploading').addClass('list-item-uploaded');
                                        $box.find('.list-action-remove').removeClass('hidden')
                                            .click(function (e) {
                                                $modal.modal('show');
                                                $modal.find('.confirm-btn').click(function (e) {
                                                    $.getJSON(params.remove_url,
                                                        {
                                                            fileId: dataSInstance.fileId
                                                        },
                                                        function (data, status, jqXHR) {
                                                            if (status == 'success') {
                                                                $('#upload-box-' + fake_id).remove();
                                                                var fileList = $('#' + params.fileList_id).find('li');
                                                                if (fileList.length == 0) {
                                                                    FileUploadListPanel.setUploadItemState(params.component_id, fake_id, 'empty');
                                                                }
                                                            }
                                                        }
                                                    ).fail(function (jqxhr, textStatus, error) {
                                                        var err = textStatus + ", " + error;
                                                        console.log("Request Failed: " + err);
                                                    });
                                                    $modal.modal('hide');
                                                });
                                            });
                                        DownloadSupportedBehavior.resolveUrl(
                                            params.download_url,
                                            dataSInstance.fileId,
                                            dataSInstance.name,
                                            function (url) {
                                                $box.find('.download-link').attr('href', url);
                                                if (DownloadSupportedBehavior.isContentTypeBrowserFriendly(dataSInstance.name)) {
                                                    $box.find('.download-link').attr('target', '_blank');
                                                }
                                            }
                                        );
                                        $("#" + params.component_id).trigger("singular:process");
                                    }
                                }
                            );
                        }
                        if(${showPageBlock}) {
                            var $blocking_overlay = $('#blocking_overlay');
                            $blocking_overlay.hide();
                            $blocking_overlay.css('opacity', '0.0');
                            App.stopPageLoading();
                            if (window.blocking_overlay_timeoutId) {
                                clearTimeout(window.blocking_overlay_timeoutId);
                            }
                        }
                    });
                },
                progress: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);

                    if(progress > 50) {
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

        window.FileUploadListPanel.resetFormElement = function (e) {
            var $input = $(e.target || e.srcElement);
            $input.wrap('<form>').closest('form').get(0).reset();
            $input.unwrap();

            // Prevent form submission
            e.stopPropagation();
            e.preventDefault();
        };

        window.FileUploadListPanel.validateInputFile = function (e, data, panel_id, maxSize, allowed_file_types, allowed_file_extensions) {
            if (data.files[0].size == 0) {
                FileUploadListPanel.setUploadItemState(panel_id, data.files[0].fake_id, 'error', "Arquivo não pode ser de tamanho 0 (zero)");
                return false;
            }

            if (maxSize && data.files[0].size > maxSize) {
                FileUploadListPanel.setUploadItemState(panel_id, data.files[0].fake_id, 'error', "Arquivo não pode ser maior que " + FileUploadListPanel.humaneSize(maxSize));
                return false;
            }

            if (allowed_file_types && allowed_file_types.length > 0) {
                var file = data.files[0];
                var extension = file.name.substring(file.name.lastIndexOf(".") + 1).toLocaleLowerCase();
                var invalidType = (jQuery.inArray(file.type, allowed_file_types) < 0);
                var invalidExtension = (jQuery.inArray(extension, allowed_file_extensions) < 0);
                if (invalidType && invalidExtension) {
                    FileUploadListPanel.setUploadItemState(panel_id, data.files[0].fake_id, 'error', "Tipo de arquivo não permitido.<BR>Permitido apenas: " + allowed_file_extensions.join());
                    return false;
                }
            }
            return true;
        };

        window.FileUploadListPanel.humaneSize = function (size) {
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
