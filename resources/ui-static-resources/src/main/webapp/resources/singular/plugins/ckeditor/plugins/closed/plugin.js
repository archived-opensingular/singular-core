CKEDITOR.plugins.add('closed',
    {
        init: function (editor) {
            editor.ui.addButton('Closed',
                {
                    label: 'Fechar',
                    command: 'closed',
                    icon: this.path + 'cancel.gif',
                    toolbar: 'Fechar'
                });
            editor.addCommand('closed', {
                exec: function () {
                    window.close();
                }, readOnly: 1, modes: {wysiwyg: 1}
            });
        }
    });