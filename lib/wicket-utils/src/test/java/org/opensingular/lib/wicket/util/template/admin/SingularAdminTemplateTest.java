package org.opensingular.lib.wicket.util.template.admin;

import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;

public class SingularAdminTemplateTest extends WicketTestCase {
    @Test
    public void testRendering() throws Exception {
        tester.startPage(new SingularAdminTemplate() {
            @Override
            protected String getContentTitle() {
                return "";
            }

            @Override
            protected String getContentSubtitle() {
                return "";
            }

            @Override
            protected boolean isWithMenu() {
                return false;
            }
        });
        assertTrue(SingularAdminTemplate.class.isAssignableFrom(tester.getLastRenderedPage().getClass()));
    }

    @Override
    protected WebApplication newApplication() {
        return new AdminApp();
    }

    private class AdminApp extends MockApplication implements SingularAdminApp {
    }
}