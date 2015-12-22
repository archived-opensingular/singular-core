package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.showcase.CaseBase;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreInteger;
import br.net.mirante.singular.wicket.ShowcaseApplication;
import javax.inject.Inject;
import junit.framework.TestCase;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class ItemCasePanelTest extends TestCase {

    WicketTester wt;
    CaseBase cb;

    @Inject
    private ShowcaseApplication app;

    @Before
    public void setup() {
        wt = new WicketTester(app, false);
        cb = new CaseInputCoreInteger();
    }

    @Test
    public void testRendering() {
        ItemCasePanel icp = new ItemCasePanel("icp", $m.ofValue(cb));
        assertNotNull(icp);
    }

    @Test
    public void testeSaveForm() {
        ItemCasePanel icp = new ItemCasePanel("icp", $m.ofValue(cb));
        wt.startComponentInPage(icp);
        FormTester formTester = wt.newFormTester("icp:form");
        assertNotNull(formTester);
    }
}