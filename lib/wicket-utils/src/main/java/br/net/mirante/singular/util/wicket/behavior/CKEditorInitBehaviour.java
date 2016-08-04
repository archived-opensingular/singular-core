package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;

public class CKEditorInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {

        String js = "";

        js += " (function(id){";
//        js += "     if( typeof CKEDITOR.instances[id] === 'undefined' ) { ";
        js += "         CKEDITOR.replace(id, {skin : 'office2013', language : 'pt-br'} );";
//        js += "     }";
        js += " }('%s'));";

        return String.format(js, component.getMarkupId(true));
    }

}