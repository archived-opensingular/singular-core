package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.showcase.ShowCaseTable;
import br.net.mirante.singular.wicket.ShowcaseApplication;
import junit.framework.TestCase;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class ComponentPageTest extends TestCase {

    private WicketTester wt;

    @Inject
    private ShowcaseApplication app;

    @Before
    public void setup() {
        wt = new WicketTester(app, false);
    }

    @Test
    public void testRendering() {
        new ShowCaseTable().getGroups().forEach((group -> {
            group.getItens().forEach(item -> {
                wt.startPage(ComponentPage.class, new PageParameters().add("cn", item.getComponentName().toLowerCase()));
                wt.assertRenderedPage(ComponentPage.class);
            });
        }));
    }

}