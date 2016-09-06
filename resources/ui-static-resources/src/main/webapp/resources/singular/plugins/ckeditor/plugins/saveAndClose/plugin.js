CKEDITOR.plugins.add('saveAndClose',
    {
        init: function (editor) {
            editor.ui.addButton('SaveAndClose',
                {
                    label: 'Salvar e Fechar',
                    command: 'saveAndClose',
                    icon: this.path + 'save.gif',
                    toolbar: 'Salvar e Fechar'
                });
            editor.addCommand('saveAndClose', {
                exec: function () {
                    editor.config.savePlugin.onSave(editor.getData());
                    window.close();
                }
            });
        }
    });