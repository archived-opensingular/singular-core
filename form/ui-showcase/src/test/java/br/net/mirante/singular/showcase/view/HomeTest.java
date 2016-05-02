package br.net.mirante.singular.showcase.view;

import br.net.mirante.singular.showcase.SpringWicketTester;
import br.net.mirante.singular.showcase.view.page.form.crud.CrudPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class HomeTest {

    private static final String ROOT_PATH = "pageBody:_Content",
            OPTIONS_FORM = ROOT_PATH + ":optionsForm",
            NEW_BUTTON = ROOT_PATH + ":form:insert";

    @Inject
    private SpringWicketTester springWicketTester;

    @Test
    @Ignore
    //TODO DANILO revisar porque este teste quebra aleatoriamente
    public void onlyShowTheNewButtonAfterTemplateIsSelected() {
        final WicketTester driver = springWicketTester.wt();

        driver.startPage(CrudPage.class);
        driver.assertRenderedPage(CrudPage.class);
        driver.assertInvisible(NEW_BUTTON);
        FormTester options = driver.newFormTester(OPTIONS_FORM, false);
        options.select("options", 0);
        driver.assertVisible(NEW_BUTTON);
    }
}
