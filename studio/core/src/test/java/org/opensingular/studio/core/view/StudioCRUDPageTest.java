package org.opensingular.studio.core.view;

import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.SingularStudioSimpleCRUDPanel;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminApp;
import org.opensingular.studio.core.definition.StudioDefinition;
import org.opensingular.studio.core.menu.*;
import org.opensingular.studio.core.util.StudioWicketUtils;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

public class StudioCRUDPageTest extends WicketTestCase {

    private ApplicationContextMock applicationContextMock;

    @Before
    public void setUp() throws Exception {
        SingularContextSetup.reset();
        applicationContextMock = new ApplicationContextMock();
        tester
                .getApplication()
                .getComponentInstantiationListeners()
                .add(new SpringComponentInjector(tester.getApplication(), applicationContextMock));
        new ApplicationContextProvider().setApplicationContext(applicationContextMock);

    }

    @Override
    protected WebApplication newApplication() {
        return new MyApp();
    }

    class MyApp extends MockApplication implements SingularAdminApp {
        @Override
        protected void init() {
            super.init();
            new AnnotatedMountScanner().scanPackage("org.opensingular.studio").mount(this);
        }
    }

    @Test
    public void testRender() throws Exception {
        applicationContextMock.putBean(new StudioMenu(null));
        tester.startPage(StudioCRUDPage.class);
        tester.assertRenderedPage(StudioCRUDPage.class);
    }

    @Test
    public void testRenderMenu() throws Exception {
        StudioMenu menu = new StudioMenu(null);
        GroupMenuEntry group = menu.add(new GroupMenuEntry(DefaultIcons.CHECK, "Group", new SidebarMenuView()));
        group.add(new ItemMenuEntry("Mock", new HTTPEndpointMenuView("/")));
        applicationContextMock.putBean(menu);
        tester.executeUrl("." + group.getEndpoint());
        tester.assertComponent("app-body:menu:itens:0:menu-item", MetronicMenuGroup.class);
        tester.assertComponent("app-body:menu:itens:0:menu-item:menu-group:sub-menu:itens:0:menu-item", MetronicMenuItem.class);
    }

    @Test
    public void testPathParamLookup() throws Exception {
        applicationContextMock.putBean(new StudioMenu(null));
        tester.executeUrl("." + StudioWicketUtils.getMergedPathIntoURL(StudioCRUDPage.class, "foo/bar"));
        StudioCRUDPage lastRenderedPage = (StudioCRUDPage) tester.getLastRenderedPage();
        assertEquals("foo/bar", lastRenderedPage.getMenuPath());
    }

    @Test
    public void testAcessMenu() throws Exception {
        StudioMenu menu = new StudioMenu(null);
        StudioDefinition studioDefinition = Mockito.mock(StudioDefinition.class);
        GroupMenuEntry group = menu.add(new GroupMenuEntry(DefaultIcons.CHECK, "Group", new SidebarMenuView()));
        ItemMenuEntry mockMenuItem = group.add(new ItemMenuEntry("Mock", new StudioMenuView(studioDefinition)));
        applicationContextMock.putBean(menu);
        tester.executeUrl("." + mockMenuItem.getEndpoint());
        tester.assertRenderedPage(StudioCRUDPage.class);
        StudioCRUDPage lastRenderedPage = (StudioCRUDPage) tester.getLastRenderedPage();
        assertEquals("group/mock", lastRenderedPage.getMenuPath());
    }

    @Test
    public void testAcessMenuWithDefinition() throws Exception {
        FormRespository formRespository = Mockito.mock(FormRespository.class);
        StudioDefinition definition = new StudioDefinition() {

            @Override
            public Class<? extends FormRespository> getRepositoryClass() {
                return formRespository.getClass();
            }

            @Override
            public void configureStudioDataTable(StudioDataTable studioDataTable) {

            }

            @Override
            public String getTitle() {
                return null;
            }
        };

        StudioMenu menu = new StudioMenu(null);
        GroupMenuEntry group = menu.add(new GroupMenuEntry(DefaultIcons.CHECK, "Group"));
        ItemMenuEntry mockMenuItem = group.add(new ItemMenuEntry(DefaultIcons.WRENCH, "Mock", new StudioMenuView(definition)));

        applicationContextMock.putBean(menu);
        applicationContextMock.putBean(formRespository);

        tester.executeUrl("." + mockMenuItem.getEndpoint());
        tester.assertRenderedPage(StudioCRUDPage.class);

        tester.assertComponent("form:crud", SingularStudioSimpleCRUDPanel.class);
    }

}