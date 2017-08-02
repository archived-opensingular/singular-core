package org.opensingular.lib.wicket.util.menu;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

public class AjaxMenuItemTest extends WicketTestCase {

    @Test
    public void testRendering() throws Exception {
        AjaxMenuItem ajaxMenuItem = new AjaxMenuItem(DefaultIcons.DOCS, null) {
            @Override
            protected void onAjax(AjaxRequestTarget target) {

            }
        };
        tester.startComponentInPage(ajaxMenuItem);
        tester.assertComponent("menu-item", AjaxMenuItem.class);
    }
}