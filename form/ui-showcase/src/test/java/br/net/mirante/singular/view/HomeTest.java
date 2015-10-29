package br.net.mirante.singular.view;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.net.mirante.singular.view.page.form.ListPage;
import br.net.mirante.singular.view.page.form.crud.CrudPage;
import br.net.mirante.singular.view.page.form.examples.ExamplePackage;
import br.net.mirante.singular.wicket.ShowcaseApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class HomeTest {

    private WicketTester driver;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private ShowcaseApplication app;

    @Before
    public void setup() {
        driver = new WicketTester(app);
    }

    @Test
    public void onlyShowTheNewButtonAfterTemplateIsSelected() {
        driver.startPage(CrudPage.class);
        driver.assertRenderedPage(CrudPage.class);
//        TagTester optionsForm = driver.getTagByWicketId("optionsForm");
        driver.assertInvisible("pageBody:_Content:form:insert");
	FormTester options = driver.newFormTester("pageBody:_Content:optionsForm", false);
        options.setValue("options", ExamplePackage.Types.ORDER.name);
        options.submit();
//        driver.assertVisible("pageBody:_Content:form:insert");
        
    }
}
