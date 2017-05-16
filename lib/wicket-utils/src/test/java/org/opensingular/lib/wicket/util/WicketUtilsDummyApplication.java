package org.opensingular.lib.wicket.util;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class WicketUtilsDummyApplication extends WebApplication {

    @Override
    protected void init() {
        super.init();
        getComponentInitializationListeners()
            .add(component -> component.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return SingleFormDummyPage.class;
    }
}
