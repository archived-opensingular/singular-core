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

        var self = this;
        self.last_id = 1;

        console.log('setup',params);

        $('#' + params.file_field_id).fileupload({
            url: params.upload_url,
            paramName: params.param_name,
            singleFileUploads: true,
            dataType: 'json',
            formData:{
                'upload_id' : params.upload_id,
            },
            send: function (e, data) {
                console.log('send',e,data);
                var name = '?', fake_id = -1;
                $.each(data.files, function (index, file) {
                    file['fake_id'] = fake_id = self.last_id ++;
                    name = file.name;
                });
                var fileList = $('#' + params.fileList_id);
                var fileElement = $('<li id="upload-box-'+fake_id+'">').addClass('upload-list-item')
                    .append(
                        $('<div>').addClass('list-item-icon')
                            .append(
                                $('<a class="list-item-uploading">').attr('href','#')
                                    .append(
                                        $('<i class="fa fa-file-text"></i>')
                                    )
                            ),
                        $('<div>').addClass('list-item-content')
                            .append($('<a>').attr('href','#').addClass('download-link')
                                .append($('<span>').text(name))
                            ),
                        $('<div class="list-item-progress" id="progress_bar_'+fake_id+'">')
                            .append($('<div class="progress-bar" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>')),

                        $('<div class="list-item-action">')
                            .append($('<a href="#" class="list-action-remove">')
                                .append($('<i class="fa fa-close">'))
                            )
                        );

                fileList.append(fileElement);
                $('#progress_bar_'+data.files[0].fake_id).hide();
                // $('#' + params.files_id ).html('');
                // $('#' + params.progress_bar_id).hide();
                // $('#' + params.progress_bar_id + ' .progress-bar').css('width','0%');

                return true;
            },
            done: function (e, data) {
                console.log('done',e,data);
                $.each(data.result.files, function (index, file) {
                    var fake_id = data.files[index].fake_id;
                        $.getJSON(params.add_url,
                            {
                                name: file.name,
                                fileId: file.fileId,
                                hashSHA1: file.hashSHA1,
                                size: file.size,

                            }, function (data, status, jqXHR) {
                                if (status == 'success'){
                                    $('#progress_bar_'+fake_id).hide();
                                    $('#upload-box-'+fake_id).find('.fa-file-text')
                                        .removeClass('fa-file-text').addClass('fa-check');
                                    $('#upload-box-'+fake_id).find('.list-item-uploading')
                                        .removeClass('list-item-uploading')
                                        .addClass('list-item-uploaded');
                                    $('#upload-box-'+fake_id).find('.download-link')
                                        .attr('href',params.download_url +
                                            '&fileId='+file.fileId+
                                            '&fileName='+file.name);
                                    $('#upload-box-'+fake_id).find('.list-action-remove')
                                        .click(function (e) {
                                            $.getJSON(params.remove_url,
                                                {   fileId: file.fileId,
                                                }, function (data, status, jqXHR) {
                                                    if (status == 'success'){
                                                        $('#upload-box-'+fake_id).remove();
                                                    }
                                                }
                                            );
                                        });
                                }
                            });
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
                });
            },
            progress: function (e, data) {
                console.log('progress',data, data.loaded , data.total);
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#progress_bar_'+data.files[0].fake_id).show();
                $('#progress_bar_'+data.files[0].fake_id+' .progress-bar')
                    .css( 'width', progress + '%' );
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
