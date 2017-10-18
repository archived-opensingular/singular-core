/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
