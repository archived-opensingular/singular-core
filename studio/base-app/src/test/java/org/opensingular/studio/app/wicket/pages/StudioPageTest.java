package org.opensingular.studio.app.wicket.pages;

import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminApp;
import org.opensingular.studio.app.menu.StudioMenuItem;
import org.opensingular.studio.core.menu.GroupMenuEntry;
import org.opensingular.studio.core.menu.StudioMenu;

public class StudioPageTest extends WicketTestCase {

    private ApplicationContextMock applicationContextMock;

    @Before
    public void setUp() throws Exception {
        applicationContextMock = new ApplicationContextMock();
        tester
                .getApplication()
                .getComponentInstantiationListeners()
                .add(new SpringComponentInjector(tester.getApplication(), applicationContextMock));

    }

    @Override
    protected WebApplication newApplication() {
        return new MyApp();
    }

    class MyApp extends MockApplication implements SingularAdminApp {
    }

    @Test
    public void testRender() throws Exception {
        applicationContextMock.putBean(new StudioMenu());
        tester.startPage(StudioPage.class);
        tester.assertRenderedPage(StudioPage.class);
    }

    @Test
    public void testeRenderMenu() throws Exception {
        StudioMenu menu = new StudioMenu();
        GroupMenuEntry group = menu.add(new GroupMenuEntry(DefaultIcons.CHECK, "Group"));
        group.add(new StudioMenuItem(DefaultIcons.WRENCH, "Mock", null));
        applicationContextMock.putBean(menu);
        tester.startPage(StudioPage.class);
        tester.assertComponent("app-body:menu:itens:0:menu-item", MetronicMenuGroup.class);
        tester.assertComponent("app-body:menu:itens:0:menu-item:menu-group:sub-menu:itens:0:menu-item", MetronicMenuItem.class);
    }
}