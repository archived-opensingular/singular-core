package org.opensingular.form.wicket.mapper.decorator;

import java.util.Arrays;

import org.apache.wicket.Component;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

public class WicketSIconActionDelegateTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(c -> c.addField("bla", STypeString.class).asAtr().help("HELP!!!"));
        tester.startDummyPage();

        WicketSIconActionDelegate delegate = new WicketSIconActionDelegate(() -> null, Arrays.asList(tester.getLastRenderedPage()));
        delegate.showMessage("Text", "text");
        delegate.showMessage("HTML", "<b>html</b>");
        delegate.showMessage("Markdown", "**markdown***");
        delegate.openForm("form", () -> tester.getDummyPage().getInstance(), Arrays.asList());
        delegate.closeForm(tester.getDummyPage().getInstance());
        delegate.getInstanceRef();
        delegate.getInternalContext(Component.class);
    }

}
