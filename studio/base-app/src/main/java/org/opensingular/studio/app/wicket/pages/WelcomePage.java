package org.opensingular.studio.app.wicket.pages;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class WelcomePage extends StudioTemplate {
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("path", getMenuPath()).add(WicketUtils.$b.visibleIf(() -> StringUtils.isNotEmpty(getMenuPath()))));
    }
}