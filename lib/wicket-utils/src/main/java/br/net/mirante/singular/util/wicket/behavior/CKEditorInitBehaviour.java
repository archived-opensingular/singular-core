package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;

public class CKEditorInitBehaviour extends InitScriptBehaviour {

    private static final String CONFIG;

    static {
        CONFIG = ""
                + "{"
                + " skin : 'office2013', "
                + " language : 'pt-br', "
                + " toolbar : [ "
                + "     { name: 'document', items: [ 'Source', '-', 'Save', 'NewPage', 'Preview', 'Print'] },"
                + "     { name: 'clipboard', items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },"
                + "     { name: 'editing', items: [ 'Find', 'Replace', '-', 'Scayt' ] },"
                + "     { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },"
                + "     { name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'] },"
                + "     { name: 'links', items: [ 'Link', 'Unlink' ] },"
                + "     { name: 'insert', items: [  'Table', 'HorizontalRule', 'SpecialChar', 'PageBreak'] },"
                + "     '/',"
                + "     { name: 'styles', items: [ 'Styles', 'Format', 'FontSize' ] },"
                + "     { name: 'colors', items: [ 'TextColor', 'BGColor' ] },"
                + "     { name: 'tools', items: [ 'Maximize', 'ShowBlocks' ] }"
                + "  ]"
                + "}";
    }

    @Override
    public String getScript(Component component) {
        return String.format(getScriptString(), component.getMarkupId(true));
    }

    public String getScriptString() {
        return ""
                + " (function(id) { "
                + "         CKEDITOR.replace(id, " + CONFIG + " );"
                + " }('%s')); ";
    }

}