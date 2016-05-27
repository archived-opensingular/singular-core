if(window.FileUploadPanel == undefined){
    window.FileUploadPanel = function(){};

    window.FileUploadPanel.setup = function(params) {
        console.log("params", params);
        $('#' + params.file_field_id).fileupload({
            url: params.upload_url,
            paramName: params.param_name,
            singleFileUploads: true,
            dataType: 'json',
            start: function (e, data) {
                // console.log($('#files_" + fieldId + "'));
                $('#' + params.files_id ).html('');
                $('#' + params.progress_bar_id + ' .progress-bar').css('width','0%');
            },
            done: function (e, data) {
                console.log(e,data);
                $.each(data.result.files, function (index, file) {
                    $('#' + params.files_id ).append(
                        $('<p/>').append(
                            $('<a />')
                                .attr('href',params.download_url + '&fileId='+file.fileId+'&fileName='+file.name)
                                .text(file.name)
                        )
                    );
                    $('#' + params.name_id).val(file.name);
                    $('#' + params.id_id).val(file.fileId);
                    $('#' + params.hash_id).val(file.hashSHA1);
                    $('#' + params.size_id).val(file.size);
                });
            },
            progressall: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#' + params.progress_bar_id + ' .progress-bar').css( 'width',
                    progress + '%' );
            }
        }).prop('disabled', !$.support.fileInput)
            .parent().addClass($.support.fileInput ? undefined : 'disabled');
    }
}
