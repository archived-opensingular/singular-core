CKEDITOR.plugins.add('finishAndClose',
    {
        init: function (editor) {
            editor.ui.addButton('FinishAndClose',
                {
                    label: 'Concluir e Fechar',
                    command: 'finishAndClose',
                    icon: this.path + 'finish.gif',
                    toolbar: 'Concluir e Fechar'
                });
            editor.addCommand('finishAndClose', {
                exec: function () {
                    editor.config.savePlugin.onSave(editor.getData());
                    window.close();
                }
            });
        }
    });