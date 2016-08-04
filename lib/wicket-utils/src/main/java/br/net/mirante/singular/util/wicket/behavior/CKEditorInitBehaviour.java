package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;

public class CKEditorInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {
        return String.format("CKEDITOR.replace('%s', {skin : 'flat'} );", component.getMarkupId(true));
    }

}