package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;

public class CKEditorInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {
        return String.format(getScriptString(), component.getMarkupId(true));
    }

    public String getScriptString() {
        return ""
                + " (function(id) { "
                + "         CKEDITOR.replace(id, {skin : 'office2013', language : 'pt-br'} );"
                + " }('%s')); ";
    }

}