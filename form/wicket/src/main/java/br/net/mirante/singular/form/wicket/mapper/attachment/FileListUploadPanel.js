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

        $('#' + params.file_field_id).fileupload({
            url: params.upload_url,
            paramName: params.param_name,
            singleFileUploads: true,
            dataType: 'json',
            formData:{
                'upload_id' : params.upload_id,
            },
            send: function (e, data) {
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

                $('#'+params.component_id).find('.list-detail-empty').hide();
                fileList.append(fileElement);
                $('#progress_bar_'+data.files[0].fake_id).hide();

                return true;
            },
            done: function (e, data) {
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
                                    var box = $('#upload-box-'+fake_id);
                                    box.find('.fa-file-text')
                                        .removeClass('fa-file-text')
                                        .addClass('fa-check');
                                    box.find('.list-item-uploading')
                                        .removeClass('list-item-uploading')
                                        .addClass('list-item-uploaded');
                                    box.find('.list-item-content')
                                        .css("width","80%");
                                    box.find('.download-link')
                                        .attr('href',params.download_url +
                                            '&fileId='+file.fileId+
                                            '&fileName='+file.name);
                                    box.find('.list-action-remove')
                                        .click(function (e) {
                                            $.getJSON(params.remove_url,
                                                {   fileId: file.fileId,
                                                }, function (data, status, jqXHR) {
                                                    if (status == 'success'){
                                                        $('#upload-box-'+fake_id).remove();
                                                        var fileList = $('#' + params.fileList_id).find('li');
                                                        if(fileList.length == 0){
                                                            $('#'+params.component_id).find('.list-detail-empty').show();
                                                        }
                                                    }
                                                }
                                            );
                                        });
                                }
                            });
                });
            },
            progress: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#progress_bar_'+data.files[0].fake_id).show();
                $('#progress_bar_'+data.files[0].fake_id+' .progress-bar')
                    .css( 'width', progress + '%' );
            }
        }).prop('disabled', !$.support.fileInput)
            .parent().addClass($.support.fileInput ? undefined : 'disabled');
    }

}
