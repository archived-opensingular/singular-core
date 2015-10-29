package br.net.mirante.singular.view;

import javax.inject.Inject;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.net.mirante.singular.view.page.form.ListPage;
import br.net.mirante.singular.wicket.ShowcaseApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class HomeTest {

    private WicketTester driver;

    @Inject
    private ShowcaseApplication app;

    @Before
    public void setup() {
        driver = new WicketTester(app);
    }

    @Test
    public void what() {
        driver.startPage(ListPage.class);
        driver.assertRenderedPage(ListPage.class);
    }
}
