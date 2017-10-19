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

package org.opensingular.form.wicket.test.base;

import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.helpers.DummyPage;
import org.opensingular.form.wicket.helpers.SingularWicketTester;

public class DummyPageTest {

    @Test
    public void testPageRendering() {
        SingularWicketTester tester = new SingularWicketTester();

        DummyPage dummyPage = new DummyPage();
        dummyPage.setTypeBuilder((x) -> {x.addFieldString("mockString");});

        dummyPage.setInstanceCreator( (refType) -> {
            SDocumentFactory factory = dummyPage.mockFormConfig.getDocumentFactory();
            return (SIComposite) factory.createInstance(refType);
        });
        tester.startPage(dummyPage);
        tester.assertRenderedPage(DummyPage.class);

        tester.getAssertionsForm().isNotNull();
        tester.getAssertionsForm().getSubCompomentWithId("mockString").isNotNull();
        tester.getAssertionsForSubComp("mockString").isNotNull();
    }
}
