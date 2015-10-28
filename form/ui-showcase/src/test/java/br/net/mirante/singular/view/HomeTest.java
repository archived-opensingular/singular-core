package br.net.mirante.singular.view;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        //driver = new WicketTester(new ShowcaseApplication());
        driver = new WicketTester(app);
    }

    @Test
    public void what() {
//        driver.startPage(FormPage.class);
//        driver.assertRenderedPage(FormPage.class);
    }
}
