package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.servlet.KeepSessionAliveServlet;

import java.util.HashMap;
import java.util.Map;

public class KeepSessionAliveBehaviour extends Behavior implements Loggable {

    public final static String KEEP_ALIVE_JS = "KeepSessionAliveBehaviour.js";

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(getKeepAliveScript().asString(getKeepAliveParametersMap())));
    }

    private PackageTextTemplate getKeepAliveScript() {
        return new PackageTextTemplate(getClass(), KEEP_ALIVE_JS);
    }

    private Map<String, Object> getKeepAliveParametersMap() {
        final Map<String, Object> params = new HashMap<>();
        params.put("callbackUrl", KeepSessionAliveServlet.ENDPOINT);
        return params;
    }

}