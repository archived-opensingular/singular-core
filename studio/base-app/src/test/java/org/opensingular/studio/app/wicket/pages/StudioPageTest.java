package org.opensingular.studio.app.wicket.pages;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.SIComposite;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.SingularStudioSimpleCRUDPanel;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminApp;
import org.opensingular.studio.app.definition.StudioDefinition;
import org.opensingular.studio.app.menu.StudioMenuItem;
import org.opensingular.studio.core.menu.GroupMenuEntry;
import org.opensingular.studio.core.menu.StudioMenu;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import static org.opensingular.studio.app.wicket.pages.StudioPage.STUDIO_ROOT_PATH;

public class StudioPageTest extends WicketTestCase {

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
            new AnnotatedMountScanner().scanPackage("org.opensingular.studio.app").mount(this);
        }
    }

    @Test
    public void testRender() throws Exception {
        applicationContextMock.putBean(new StudioMenu(null));
        tester.startPage(StudioPage.class);
        tester.assertRenderedPage(StudioPage.class);
    }

    @Test
    public void testRenderMenu() throws Exception {
        StudioMenu menu = new StudioMenu(null);
        GroupMenuEntry group = menu.add(new GroupMenuEntry(DefaultIcons.CHECK, "Group"));
        group.add(new StudioMenuItem(DefaultIcons.WRENCH, "Mock", null));
        applicationContextMock.putBean(menu);
        tester.startPage(StudioPage.class);
        tester.assertComponent("app-body:menu:itens:0:menu-item", MetronicMenuGroup.class);
        tester.assertComponent("app-body:menu:itens:0:menu-item:menu-group:sub-menu:itens:0:menu-item", MetronicMenuItem.class);
    }

    @Test
    public void testPathParamLookup() throws Exception {
        applicationContextMock.putBean(new StudioMenu(null));
        tester.executeUrl("./" + STUDIO_ROOT_PATH + "/foo/bar");
        StudioPage lastRenderedPage = (StudioPage) tester.getLastRenderedPage();
        assertEquals("foo/bar", lastRenderedPage.getMenuPath());
    }

    @Test
    public void testAcessMenu() throws Exception {
        StudioMenu menu = new StudioMenu(null);
        GroupMenuEntry group = menu.add(new GroupMenuEntry(DefaultIcons.CHECK, "Group"));
        StudioMenuItem mockMenuItem = group.add(new StudioMenuItem(DefaultIcons.WRENCH, "Mock", null));
        applicationContextMock.putBean(menu);
        tester.executeUrl("." + mockMenuItem.getEndpoint());
        tester.assertRenderedPage(StudioPage.class);
        StudioPage lastRenderedPage = (StudioPage) tester.getLastRenderedPage();
        assertEquals("group/mock", lastRenderedPage.getMenuPath());
    }

    @Test
    public void testAcessMenuWithDefinition() throws Exception {
        StudioDefinition definition = new StudioDefinition() {
            @Override
            public String getRepositoryBeanName() {
                return "mockRepository";
            }

            @Override
            public void configureDatatableColumns(BSDataTableBuilder<SIComposite, String, IColumn<SIComposite, String>> dataTableBuilder) {

            }

            @Override
            public String getTitle() {
                return null;
            }
        };

        StudioMenu menu = new StudioMenu(null);
        GroupMenuEntry group = menu.add(new GroupMenuEntry(DefaultIcons.CHECK, "Group"));
        StudioMenuItem mockMenuItem = group.add(new StudioMenuItem(DefaultIcons.WRENCH, "Mock", definition));

        applicationContextMock.putBean(menu);
        applicationContextMock.putBean("mockRepository", Mockito.mock(FormRespository.class));

        tester.executeUrl("." + mockMenuItem.getEndpoint());
        tester.assertRenderedPage(StudioPage.class);

        tester.assertComponent("form:crud", SingularStudioSimpleCRUDPanel.class);
    }
}