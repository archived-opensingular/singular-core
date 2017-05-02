package org.opensingular.lib.wicket.util.menu;

import org.apache.wicket.Component;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.resource.Icone;

public class MetronicMenuTest {

    @Test
    public void test() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                return new TemplatePanel(contentId, "<div wicket:id='menu'></div>")
                    .add(new MetronicMenu("menu")
                        .addItem(new MetronicMenuGroup("group")
                            .addItem(new MetronicMenuItem(Icone.BAN, "item", "http://localhost:8080"))));
            }
        });
        
        tester.assertNoErrorMessage();
    }

}
