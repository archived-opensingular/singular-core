package org.opensingular.form.wicket.mapper.decorator;

import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

public class SInstanceActionsProvidersTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(c -> c.addField("bla", STypeString.class)
            .asAtr().help("HELP!!!"));
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();
    }

}
