/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function (htmlContainer, hiddenInput, callbackUrl, isEnabled, showSaveButton, buttonsList, submitButtonId, classDisableDoubleClick) {


    $(document).ready(function () {
        appendFunctions(window.opener);
    });

    function appendFunctions(opener) {
        $(function () {

            if (!opener) {
                var msgException = "A página do requerimento foi aberta de forma indevida! <b>Não será possivel salvar o Requerimento.</b>";
                toastr.options = {
                    "timeOut": "10000",
                    "positionClass": "toast-top-center"
                };
                toastr.error(msgException);
                isEnabled = false;
            }

            var plugin;
            if (isEnabled === "true") {
                if (showSaveButton === "true") {
                    plugin = 'saveButton,closed';
                } else {
                    plugin = 'finishAndClose,cancel';
                }
            } else {
                CKEDITOR.config.readOnly = true;
                plugin = 'closed';
            }

            var ids = "";
            //Foi utilizado ',,' para separar cada botão adicionado no RichText.
            var buttonsExtra = buttonsList.split(",,");
            if (buttonsExtra) {
                buttonsExtra.forEach(function (b) {
                    //Foi utilizado #$ para separar cada atributo do botão.
                    var texts = b.split("#$");
                    var id;
                    if (texts[3] === "true") {
                        //É adicionado extra nos botões que é para ser exibido com a label ao lado.
                        id = 'extra' + texts[0];
                    } else {
                        id = texts[0];
                    }
                    ids += id + ",";
                });
                ids = ids.slice(0, -1);
            }
            var editor = CKEDITOR.replace("ck-text-area", {
                extraPlugins: plugin,
                allowedContent: true,
                skin: 'office2013',
                language: 'pt-br',
                width: '215mm',
                buttonPlugin: {
                    onEvent: function (data) {

                        $('#ck-text-area').val(data);
                        $('#' + submitButtonId).click();
                        var jQuerRefOfHtmlContainer = opener.$('#' + htmlContainer);
                        jQuerRefOfHtmlContainer.html(data);

                        var jQueryRefOfHiddenInput = opener.$('#' + hiddenInput);
                        jQueryRefOfHiddenInput.val(data);
                        jQueryRefOfHiddenInput.trigger("singular:process");
                    },

                    onSaveAction: function (data) {

                        var msgException = "A página do requerimento foi fechada, ou foi aberta de forma indevida."
                            + "<p> Não será possivel salvar o Requerimento.</p>";
                        if (window.opener) {
                            var jQuerRefOfHtmlContainer = opener.$('#' + htmlContainer);
                            jQuerRefOfHtmlContainer.html(data);

                            var jQueryRefOfHiddenInput = opener.$('#' + hiddenInput);
                            jQueryRefOfHiddenInput.val(data);
                            jQueryRefOfHiddenInput.trigger("singular:process");

                            $('#ck-text-area').val(data);
                            $('#' + submitButtonId).click();

                            try {
                                if (window.opener.AbstractFormPage) {
                                    window.opener.AbstractFormPage.onSave();
                                    toastr.success("Requerimento salvo com sucesso.");
                                } else {
                                    toastr.error(msgException);
                                }
                            } catch (e) {
                                toastr.error("Ocorreu um erro ao salvar o requerimento.");
                            }
                        } else {
                            toastr.error(msgException);
                        }

                    }
                },
                toolbar: [
                    {name: 'document', items: ['SaveButton', 'Closed', 'FinishAndClose', 'Cancel', 'Preview', 'Print']},
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
                    {name: 'tools', items: ['ShowBlocks']},
                    {name: 'others', items: ids.split(",")}
                ],
                on: {
                    'instanceReady': function () {
                        $('.cke_contents').height($('#bodyPage').height() - $('.cke_contents').offset().top - $('.cke_bottom').height() - 20);
                        configureIconButtons();
                    }
                }
            });

            CKEDITOR.config.disableNativeSpellChecker = false;
            configureDisabledDoubleClick(editor);

            buttonsExtra.forEach(function (b) {

                /**
                 * [0] = ID
                 * [1] = Label
                 * [2] = The css of Icon
                 * [3] = If have to show the label inline.
                 */

                    //Foi utilizado #$ para separar cada atributo do botão.
                var texts = b.split("#$");

                var id;
                if (texts[3] === "true") {
                    //É adicionado extra nos botões que é para ser exibido com a label ao lado.
                    id = 'extra' + texts[0];
                } else {
                    id = texts[0];
                }

                editor.ui.addButton(id,
                    {
                        label: texts[1],
                        command: texts[0]
                    });

                editor.addCommand(texts[0], {
                    exec: function () {
                        var selected = editor.getSelection().getSelectedText();
                        var innerText = editor.document.getBody().getText();

                        Wicket.Ajax.post({
                            u: callbackUrl,
                            ep: {'innerText': innerText, 'index': texts[0], 'selected': selected}
                        });

                    }
                });


            });

        });

    }

    /**
     * Method to configure the disabled double click buttons.
     * If the view contains this class, the double click will do nothing
     * @param editor The CKeditor instance.
     */
    function configureDisabledDoubleClick(editor) {
        editor.on('doubleclick', function (evt) {
            var element = evt.data.element;
            var classesDoubleClick = classDisableDoubleClick.split(", ");

            if (element.hasClass(classesDoubleClick)) {
                evt.stop();
            }
        }, null, null, 1);
    }

    /**
     * Method to configure the icon of the buttons.
     * This will use the value [2] that contains the class of the button,
     *  and will add the font-awesome (fa-fa-user), or the icon-simple-line (icon-user).
     */
    function configureIconButtons() {
        if (buttonsList) {
            buttonsList.split(",,").forEach(function (b) {
                var texts = b.split("#$");

                var id;
                if (texts[3] === "true") {
                    id = 'extra' + texts[0];
                } else {
                    id = texts[0];
                }


                var classeIcon;
                if (texts[2].indexOf('fa fa-') >= 0) {
                    classeIcon = ' cke_singular_icon-font-awesome ';
                } else {
                    classeIcon = ' cke_singular_icon-simple-line ';
                }
                $('.cke_button__' + id + '_icon').addClass(texts[2] + classeIcon);

            });
        } else {
            console.log("Don't find extra buttons!");
        }

    }


})('${htmlContainer}', '${hiddenInput}', '${callbackUrl}', '${isEnabled}', '${showSaveButton}', '${buttonsList}', '${submitButtonId}', '${classDisableDoubleClick}');
