package br.net.mirante.singular.view;

import javax.inject.Inject;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.net.mirante.singular.view.page.form.crud.CrudPage;
import br.net.mirante.singular.wicket.ShowcaseApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class HomeTest {

    private static final String ROOT_PATH = "pageBody:_Content",
	    			OPTIONS_FORM = ROOT_PATH+":optionsForm",
	    			NEW_BUTTON = ROOT_PATH+":form:insert";

    private WicketTester driver;

    @Inject
    private ShowcaseApplication app;

    @Before
    public void setup() {
        driver = new WicketTester(app);
    }

    @Test
    public void onlyShowTheNewButtonAfterTemplateIsSelected() {
        driver.startPage(CrudPage.class);
        driver.assertRenderedPage(CrudPage.class);
        driver.assertInvisible(NEW_BUTTON);
	FormTester options = driver.newFormTester(OPTIONS_FORM, false);
        options.select("options", 0);
        driver.assertVisible(NEW_BUTTON);
        
    }
}
