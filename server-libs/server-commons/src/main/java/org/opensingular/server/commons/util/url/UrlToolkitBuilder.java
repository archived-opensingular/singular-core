package org.opensingular.server.commons.util.url;

import org.apache.wicket.request.Url;

import java.io.Serializable;

public class UrlToolkitBuilder implements Serializable {

    public UrlToolkit build(Url url) {
        return new UrlToolkit(url);
    }

}