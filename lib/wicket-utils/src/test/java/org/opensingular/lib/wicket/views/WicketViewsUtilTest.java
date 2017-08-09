/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.views;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.internal.lib.wicket.test.SingularSimpleWicketTester;
import org.opensingular.lib.commons.table.ColumnType;
import org.opensingular.lib.commons.table.PopulatorTable;
import org.opensingular.lib.commons.table.TableTool;

/**
 * @author Daniel Bordin on 11/02/2017.
 */
public class WicketViewsUtilTest {

    protected SingularSimpleWicketTester tester;
    protected boolean showContentOnDesktop = false;

    @Before
    public void setUp() {
        tester = new SingularSimpleWicketTester();
    }

    @Test
    public void testeWrongSerializationPage() {
        tester.startPage(WrongSerializationPage.class);
        tester.assertRenderedPage(WrongSerializationPage.class);
        if (showContentOnDesktop) {
            tester.showHtmlContentOnDesktopForUserAndWaitOpening();
        }
    }

    public static class WrongSerializationPage extends WebPage {

        public Object o = 1;

        public WrongSerializationPage() {
            Form form = new Form("form");
            form.add(new WicketViewWrapperForViewOutputHtml("content", this::createContent));
            add(form);
        }

        private TableTool createContent() {
            TableTool table = new TableTool();
            table.addColumn(ColumnType.STRING, "A");
            PopulatorTable populator = table.createSimpleTablePopulator();
            populator.insertLine("L0");
            return table;
        }
    }

}
