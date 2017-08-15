package org.opensingular.form.decorator.action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SInstance;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeString;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class SInstanceHelpActionsProviderTest extends TestCaseForm {

    public static final class MockSInstanceActionCapable implements ISInstanceActionCapable {
        public Map<ISInstanceActionsProvider, Integer> providers = new LinkedHashMap<>();
        @Override
        public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
            providers.put(provider, sortPosition);
        }
    }

    public SInstanceHelpActionsProviderTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void test() {

        SInstance instance = super.createSerializableTestInstance(STypeString.class);

        Iterable<SInstanceAction> preHelp = new SInstanceHelpActionsProvider().getActions(new MockSInstanceActionCapable(), instance);
        assertFalse(preHelp.iterator().hasNext());

        instance.asAtr().help("HELP!!!");

        Iterable<SInstanceAction> postHelp = new SInstanceHelpActionsProvider().getActions(new MockSInstanceActionCapable(), instance);
        assertTrue(postHelp.iterator().hasNext());

        List<SInstanceAction> actions = Lists.newArrayList(postHelp);
        assertEquals(1, actions.size());

        SInstanceAction helpAction = actions.get(0);

        assertNotNull(helpAction.getPreview());
    }

}
