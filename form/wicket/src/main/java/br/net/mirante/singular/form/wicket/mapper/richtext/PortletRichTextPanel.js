(function (label, htmlContainer, hiddenInput) {

    var newWindow;

    window.openNewTabWithCKEditor = function () {
        if (typeof newWindow != "undefined") {
            newWindow.close();
        }
        newWindow = window.open("", label);
        appendFunctions(newWindow);
        var html = '<html style="width: 100%; height: 100%;background-color: #e8e9f0;">'
            + '<head>'
            + '<title>' + label + '</title>'
            + '<script type="application/javascript" src="/singular-static/resources/singular/plugins/ckeditor/ckeditor.js"></script>'
            + '<script type="text/javascript" src="/singular-static/resources/metronic/global/plugins/jquery.min.js"></script>'
            + '<style type="text/css">.cke_button__saveandclose_label{display : inline !important;}</style> '
            + '</head>'
            + '<body style="width: 210mm;margin-top: 10px; margin-right: auto; margin-left: auto;">'
            + '<textarea id="ck-text-area"></textarea> '
            + '<script type="text/javascript">$(document).ready(function(){createCKEditor();});</script>'
            + '</body>'
            + '</html>';
        newWindow.document.open();
        newWindow.document.write(html);
        newWindow.document.close();
    };

    function appendFunctions(nw) {
        nw.createCKEditor = function () {
            nw.document.getElementById('ck-text-area').value = $('#' + htmlContainer).html();
            nw.CKEDITOR.replace("ck-text-area", {
                extraPlugins: 'saveAndClose',
                skin: 'office2013',
                language: 'pt-br',
                width: '210mm',
                height: '80%',
                savePlugin: {
                    onSave: function (data) {
                        $('#' + htmlContainer).html(data);
                        $('#' + hiddenInput).val(data);
                    }
                },
                toolbar: [
                    {name: 'document', items: ['SaveAndClose', '-', 'NewPage', 'Preview', 'Print']},
                    {
                        name: 'clipboard',
                        items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo']
                    },
                    {name: 'editing', items: ['Find', 'Replace', '-', 'Scayt']},
                    {
                        name: 'basicstyles',
                        items: ['Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat']
                    },
                    {
                        name: 'paragraph',
                        items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']
                    },
                    {name: 'links', items: ['Link', 'Unlink']},
                    {name: 'insert', items: ['Table', 'HorizontalRule', 'SpecialChar', 'PageBreak']},
                    '/',
                    {name: 'styles', items: ['Styles', 'Format', 'FontSize']},
                    {name: 'colors', items: ['TextColor', 'BGColor']},
                    {name: 'tools', items: ['ShowBlocks']}
                ],
                on: {
                    'instanceReady': function (evt) {

                    }
                }
            });
        };
    }
})('${label}', '${htmlContainer}', '${hiddenInput}');