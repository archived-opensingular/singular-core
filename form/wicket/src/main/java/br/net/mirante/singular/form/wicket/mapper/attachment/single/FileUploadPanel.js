(function () {
    "use strict";
    if(window.FileUploadPanel == undefined){
        window.FileUploadPanel = function(){};

        window.FileUploadPanel.setup = function(params) {
            $('#' + params.progress_bar_id).hide();

            var update_action_buttons = function () {
                var choose_btn = $('#' + params.panel_id).find('.file-choose-button');
                var trash_btn = $('#' + params.panel_id).parent().find('.file-trash-button');

                if($('#' + params.id_id).val()){
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
                dataType: 'json',
                formData:{
                    'upload_id' : params.upload_id,
                },
                start: function (e, data) {
                    // console.log($('#files_" + fieldId + "'));
                    $('#' + params.files_id ).html('');
                    $('#' + params.progress_bar_id).hide();
                    $('#' + params.progress_bar_id + ' .progress-bar').css('width','0%');
                },
                done: function (e, data) {
                    console.log('done',e,data);
                    $.each(data.result, function (index, file) {
                        console.log('f',file, $('#' + params.files_id ));
                        $.getJSON(params.add_url,
                            {
                                name: file.name,
                                fileId: file.fileId,
                                hashSHA1: file.hashSHA1,
                                size: file.size,

                            }, function (dataSInstance, status, jqXHR) {
                                $('#' + params.files_id).append(
                                    $('<a />')
                                        .on('click', function (event) {
                                            DownloadSupportedBehavior.ajaxDownload(params.download_url, dataSInstance.fileId, dataSInstance.name);
                                            event.preventDefault();
                                        })
                                        .text(dataSInstance.name)
                                );
                                $('#' + params.progress_bar_id).hide();

                                update_action_buttons();
                            });
                    });
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    console.log($('#' + params.progress_bar_id));
                    $('#' + params.progress_bar_id).show();
                    $('#' + params.progress_bar_id + ' .progress-bar').css( 'width',
                        progress + '%' );
                }
            }).prop('disabled', !$.support.fileInput)
                .parent().addClass($.support.fileInput ? undefined : 'disabled');
        }

        // Legacy for multple files

        window.FileUploadPanel.validateInputFile = function(input, maxSize){
            if( input.files[0].size  > maxSize) {
                toastr.error("Arquivo não pode ser maior que "+FileUploadPanel.humaneSize(maxSize));
                FileUploadPanel.resetFormElement(input);
                return false;
            }
            return true;
        };

        window.FileUploadPanel.resetFormElement = function(e) {
            e.wrap('<form>').closest('form').get(0).reset();
            e.unwrap();

            // Prevent form submission
            e.stopPropagation();
            e.preventDefault();
        }

        window.FileUploadPanel.humaneSize = function(size){
            var remainder = size;
            var index = 0;
            var names = ['bytes', 'KB', 'MB', 'GB', 'TB'];
            while(remainder >= 1024){
                remainder /= 1024; index ++;
            }
            return remainder +" "+ names[index];
        }

    }
})();