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

package org.opensingular.lib.wicket.util.bootstrap.layout.table;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol.BSGridSize;

public class BSTableGridTest {

    @Test
    public void test() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                BSTableGrid grid = new BSTableGrid(contentId)
                    .setDefaultGridSize(BSGridSize.MD);

                grid.newTHead()
                    .newRow()
                    .newTHeaderCell($m.ofValue("Header"));

                grid.newTBody()
                    .setDefaultGridSize(BSGridSize.MD)
                    .newColInRow();

                grid.newRow();

                grid.newTSection((id, size) -> new BSTSection(id, $m.ofValue()).setDefaultGridSize(size))
                    .appendRow(id -> new BSTRow(id, BSGridSize.MD)
                        .appendColTag(1, "td", new Label("bla", "empty"))
                        .appendCol(1, BSTDataCell::new));

                grid.newTFoot()
                    .newColInRow();

                return grid;
            }
        });
    }

}
