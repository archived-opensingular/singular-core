package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public abstract class InitScriptBehaviour extends Behavior {

    private final String jQueryDocumentReady = ";(function(){$(document).ready(function(){%s});})();";

    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component);
        component.add($b.onReadyScript(c -> String.format(jQueryDocumentReady, getScript(c))));
    }

    public abstract String getScript(Component component);

}
