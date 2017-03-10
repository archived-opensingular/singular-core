package org.opensingular.server.commons.util.url;

import org.apache.wicket.request.Url;

public class UrlToolkitBuilder {

    public UrlToolkit build(Url url){
        return new UrlToolkit(url);
    }

}