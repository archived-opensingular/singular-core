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

(function (label, htmlContainer, hiddenInput, html) {

    var newWindow;

    window['openNewTabWithCKEditor${hash}'] = function () {
        if (typeof newWindow != "undefined") {
            newWindow.close();
        }
        newWindow = window.open("", label);
        appendFunctions(newWindow);
        newWindow.document.open();
        newWindow.document.write(html);
        newWindow.document.close();
        newWindow.document.title = label;
    };

    function appendFunctions(nw) {
        nw.createCKEditor = function () {
            nw.document.getElementById('ck-text-area').value = $('#' + htmlContainer).html();
            nw.CKEDITOR.replace("ck-text-area", {
                extraPlugins: 'saveAndClose',
                allowedContent: true,
                skin: 'office2013',
                language: 'pt-br',
                width: '215mm',
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
                        nw.$('.cke_contents').height(nw.$('html').height() - nw.$('.cke_contents').offset().top - nw.$('.cke_bottom').height() - 20);
                    }
                }
            });
        };
    }
})('${label}', '${htmlContainer}', '${hiddenInput}', '${html}');