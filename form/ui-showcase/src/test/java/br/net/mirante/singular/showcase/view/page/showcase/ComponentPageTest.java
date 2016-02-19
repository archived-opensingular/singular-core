package br.net.mirante.singular.showcase.view.page.showcase;

import javax.inject.Inject;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.net.mirante.singular.showcase.SpringWicketTester;
import br.net.mirante.singular.showcase.component.ShowCaseTable;
import br.net.mirante.singular.showcase.view.page.ComponentPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class ComponentPageTest {

    @Inject
    private SpringWicketTester springWicketTester;

    @Test
    public void testRendering() {
        new ShowCaseTable().getGroups().forEach(group -> {
            group.getItens().forEach(item -> {
                springWicketTester.wt().startPage(ComponentPage.class, new PageParameters().add("cn", item.getComponentName().toLowerCase()));
                springWicketTester.wt().assertRenderedPage(ComponentPage.class);
            });
        });
    }

}