CKEDITOR.plugins.add('cancel',
    {
        init: function (editor) {
            editor.ui.addButton('Cancel',
                {
                    label: 'Cancelar',
                    command: 'cancel',
                    icon: this.path + 'cancel.gif',
                    toolbar: 'Cancelar'
                });
            editor.addCommand('cancel', {
                exec: function () {
                    window.close();
                }
            });
        }
    });