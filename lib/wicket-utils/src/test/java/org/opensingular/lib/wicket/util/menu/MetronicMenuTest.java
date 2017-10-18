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

package org.opensingular.lib.wicket.util.menu;

import org.apache.wicket.Component;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

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
                            .addItem(new MetronicMenuItem(DefaultIcons.BAN, "item", "http://localhost:8080"))));
            }
        });
        
        tester.assertNoErrorMessage();
    }

}
