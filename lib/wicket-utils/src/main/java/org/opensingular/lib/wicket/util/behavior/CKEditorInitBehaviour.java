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

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;

public class CKEditorInitBehaviour extends InitScriptBehaviour {

    private static final String CONFIG_WITH_WIDTH;
    private static final String CONFIG_WITHOUT_WIDTH;
    private boolean disablePageLayout;
    static {
        String CONFIG = ""
                + "{"
                + " allowedContent : true, "
                + " skin : 'office2013', "
                + " language : 'pt-br', "
                + " toolbar : [ "
                + "     { name: 'document', items: ['NewPage', 'Preview', 'Print'] },"
                + "     { name: 'clipboard', items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },"
                + "     { name: 'editing', items: [ 'Find', 'Replace', '-', 'Scayt' ] },"
                + "     { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },"
                + "     { name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'] },"
                + "     { name: 'links', items: [ 'Link', 'Unlink' ] },"
                + "     { name: 'insert', items: [  'Table', 'HorizontalRule', 'SpecialChar'] },"
                + "     '/',"
                + "     { name: 'styles', items: [ 'Styles', 'Format', 'FontSize' ] },"
                + "     { name: 'colors', items: [ 'TextColor', 'BGColor' ] },"
                + "     { name: 'tools', items: [ 'Maximize' ] }"
                + "  ]";

        CONFIG_WITHOUT_WIDTH = CONFIG + "}";
        CONFIG_WITH_WIDTH = CONFIG + " , width : '215mm'} ";
    }

    public CKEditorInitBehaviour(boolean disablePageLayout) {
        this.disablePageLayout = disablePageLayout;
    }

    @Override
    public String getScript(Component component) {
        return String.format(getScriptString(), component.getMarkupId(true));
    }

    public String getScriptString() {
        return ""
                + " (function(id) { "
                + "         CKEDITOR.replace(id, " + (disablePageLayout ? CONFIG_WITHOUT_WIDTH : CONFIG_WITH_WIDTH) + " );"
                + " }('%s')); ";
    }

}