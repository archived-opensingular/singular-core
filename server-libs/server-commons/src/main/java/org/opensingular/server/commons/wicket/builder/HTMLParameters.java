package org.opensingular.server.commons.wicket.builder;

import java.util.HashMap;
import java.util.Map;

public class HTMLParameters {

    final Map<String, String> params = new HashMap<>();

    public HTMLParameters styleClass(String value) {
        return this.add("class", value);
    }

    public HTMLParameters add(String key, String value) {
        params.put(key, value);
        return this;
    }

    public Map<String, String> getParametersMap() {
        return params;
    }

}
