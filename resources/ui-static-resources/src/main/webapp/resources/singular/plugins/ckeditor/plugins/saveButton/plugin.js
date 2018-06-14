CKEDITOR.plugins.add('saveButton',
    {
        init: function (editor) {
            editor.ui.addButton('SaveButton',
                {
                    label: 'Salvar',
                    command: 'saveAction',
                    icon: this.path + 'save.png',
                    toolbar: 'Salvar'
                });
            editor.addCommand('saveAction', {
                exec: function () {
                    editor.config.buttonPlugin.onSaveAction(editor.getData());
                }
            });
        }
    });