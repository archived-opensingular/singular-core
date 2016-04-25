package br.net.mirante.singular.showcase.view.page.showcase;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.showcase.SpringWicketTester;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreInteger;
import br.net.mirante.singular.showcase.view.page.ItemCasePanel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class ItemCasePanelTest {

    CaseBase cb;

    @Inject @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    @Inject
    private SpringWicketTester springWicketTester;

    @Before
    public void setup() {
        cb = new CaseInputCoreInteger();
    }

    @Test
    public void testRendering() {
        ItemCasePanel icp = new ItemCasePanel("icp", $m.ofValue(cb));
        assertNotNull(icp);
    }

    @Test
    public void testeSaveForm() {
        final WicketTester wt = springWicketTester.wt();
        ItemCasePanel icp = new ItemCasePanel("icp", $m.ofValue(cb));
        wt.startComponentInPage(icp);
        FormTester formTester = wt.newFormTester("icp:form");
        assertNotNull(formTester);
    }
}