if(window.FileListUploadPanel == undefined){
    window.FileListUploadPanel = function(){};

    window.FileListUploadPanel.setup = function(params) {
        // $('#' + params.progress_bar_id).hide();

        var update_action_buttons = function () {
            /*var choose_btn = $('#' + params.name_id).parent().find('.file-choose-button');
            var trash_btn = $('#' + params.name_id).parent().find('.file-trash-button');

            if($('#' + params.id_id).val()){
                trash_btn.css('display','block').css('width','35px'); // Somewhat, we need this in order to not destroy the layout
                choose_btn.hide();
            }else{
                choose_btn.show();
                trash_btn.hide();
            }*/
        }

        // update_action_buttons();

        console.log('setup',params);

        $('#' + params.file_field_id).fileupload({
            url: params.upload_url,
            paramName: params.param_name,
            singleFileUploads: true,
            dataType: 'json',
            formData:{
                'upload_id' : params.upload_id,
            },
            start: function (e, data) {
                console.log('start',e,data);
                var fileList = $('#' + params.fileList_id);
                var fileElement = $('<li>').addClass('upload-list-item')
                    .append(
                        $('<div>').addClass('list-item-icon')
                            .append(
                                $('<a>').attr('href','#').addClass('list-item-uploading')
                                    .append(
                                        $('<i class="fa fa-file-text"></i>')
                                    )
                            ),
                        $('<div>').addClass('list-item-content')
                            .append(
                                $('<span>').text(data)
                            ),
                        $('<div class="list-item-progress">')
                            .append($('<div class="progress-bar" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 40%"></div>')),

                        $('<div class="list-item-action">')
                            .append($('<a href="#" class="list-action-remove">')
                                .append($('<i class="fa fa-close">'))
                            )
                        );

                fileList.append(fileElement);
                // $('#' + params.files_id ).html('');
                // $('#' + params.progress_bar_id).hide();
                // $('#' + params.progress_bar_id + ' .progress-bar').css('width','0%');
            },
            done: function (e, data) {
                console.log('done',e,data);
                // $.each(data.result.files, function (index, file) {
                //     console.log('f',file, $('#' + params.files_id ));
                //     $('#' + params.files_id ).append(
                //         $('<p/>').append(
                //             $('<a />')
                //                 .attr('href',params.download_url + '&fileId='+file.fileId+'&fileName='+file.name)
                //                 .text(file.name)
                //         )
                //     );
                //     $('#' + params.progress_bar_id).hide();
                //     $('#' + params.name_id).val(file.name);
                //     $('#' + params.id_id).val(file.fileId);
                //     $('#' + params.hash_id).val(file.hashSHA1);
                //     $('#' + params.size_id).val(file.size);
                //
                //     update_action_buttons();
                // });
            },
            progress: function (e, data) {
                console.log('progress',data, data.loaded , data.total);
                files[0].lastModified +"-"+ files[0].size
                // var progress = parseInt(data.loaded / data.total * 100, 10);
                // $('#' + params.progress_bar_id).show();
                // $('#' + params.progress_bar_id + ' .progress-bar').css( 'width',
                //     progress + '%' );
            },
            progressall: function (e, data) {
                console.log('progressall',data.loaded , data.total);
                // var progress = parseInt(data.loaded / data.total * 100, 10);
                // $('#' + params.progress_bar_id).show();
                // $('#' + params.progress_bar_id + ' .progress-bar').css( 'width',
                //     progress + '%' );
            }
        }).prop('disabled', !$.support.fileInput)
            .parent().addClass($.support.fileInput ? undefined : 'disabled');
    }

}
