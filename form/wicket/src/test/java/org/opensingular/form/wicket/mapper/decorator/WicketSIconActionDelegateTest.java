package org.opensingular.form.wicket.mapper.decorator;

import java.util.Arrays;

import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.junit.Test;
import org.opensingular.form.decorator.action.SInstanceAction.FormDelegate;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.lib.commons.ref.Out;

public class WicketSIconActionDelegateTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(c -> c.addField("bla", STypeString.class).asAtr().help("HELP!!!"));
        tester.startDummyPage();

        Out<FormDelegate> fd = new Out<>();

        WicketSIconActionDelegate delegate = new WicketSIconActionDelegate(Model.of(), Arrays.asList(tester.getLastRenderedPage()));
        delegate.showMessage("Text", "text");
        delegate.showMessage("HTML", "<b>html</b>");
        delegate.showMessage("Markdown", "**markdown***");
        delegate.openForm(fd, "form", () -> tester.getDummyPage().getInstance(), Arrays.asList());
        fd.get().close();
        delegate.getInstanceRef();
        delegate.getInternalContext(Component.class);
    }

}
