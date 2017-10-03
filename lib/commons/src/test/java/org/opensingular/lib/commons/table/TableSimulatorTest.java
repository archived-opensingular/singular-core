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

package org.opensingular.lib.commons.table;

import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

/**
 * @author Daniel C. Bordin on 21/04/2017.
 */
public class TableSimulatorTest {

    @Test
    public void simpleUse() {
        TableSimulator table = new TableSimulator();
        table.addLine().add(0).add(1);
        table.addLine().add(10);
        table.addLine().add(20).add(21).add(22);

        table.assertLinesSize(3);
        table.assertLine(0, 0, 1);
        table.assertLine(1, 10);
        table.assertLine(2, 20, 21, 22);
    }

    @Test
    public void simpleColSpan() {
        TableSimulator table = new TableSimulator();
        table.addLine().add(1, 2, 0).add(2);
        table.addLine().add(1, 4, 10);
        table.addLine().add(20).add(1, 2, 21).add(23);

        table.assertLinesSize(3);
        table.assertLine(0, 0, 0, 2);
        table.assertLine(1, 10, 10, 10, 10);
        table.assertLine(2, 20, 21, 21, 23);
    }

    @Test
    public void simpleRolSpan() {
        TableSimulator table = new TableSimulator();
        table.addLine().add(0).add(3, 1, 1);
        table.addLine().add(10).add(12);

        table.assertLinesSize(2);
        table.assertLine(0, 0, 1);
        table.assertLine(1, 10, 1, 12);
    }

    @Test
    public void simpleRolSpan2() {
        TableSimulator table = new TableSimulator();
        table.addLine().add(0).add(1).add(3, 1, 2);
        table.addLine().add(10);
        table.addLine().add(20).add(21).add(23);
        table.addLine().add(30).add(31).add(32).add(33);

        table.assertLinesSize(4);
        table.assertLine(0, 0, 1, 2);
        table.assertLine(1, 10, null, 2);
        table.assertLine(2, 20, 21, 2, 23);
        table.assertLine(3, 30, 31, 32, 33);
    }

    @Test
    public void simpleRolSpan3() {
        TableSimulator table = new TableSimulator();
        table.addLine().add(0).add(3,1,1).add(2);
        table.addLine().add(10).add(3,1,12);
        table.addLine().add(20);
        table.addLine().add(30).add(31).add(33);

        table.assertLinesSize(4);
        table.assertLine(0, 0, 1, 2);
        table.assertLine(1, 10, 1, 12);
        table.assertLine(2, 20, 1, 12);
        table.assertLine(3, 30, 31, 12, 33);
    }

    @Test
    public void simpleRolSpan4() {
        TableSimulator table = new TableSimulator();
        table.addLine().add(0).add(3,1,1);
        table.addLine();
        table.addLine().add(20);

        table.assertLinesSize(3);
        table.assertLine(0, 0, 1);
        table.assertLine(1, null, 1 );
        table.assertLine(2, 20, 1);
    }

    @Test
    public void simpleRolSpanColSpan() {
        TableSimulator table = new TableSimulator();
        table.addLine().add(0).add(3,2,1);
        table.addLine();
        table.addLine().add(20).add(23);

        table.assertLinesSize(3);
        table.assertLine(0, 0, 1, 1);
        table.assertLine(1, null, 1, 1 );
        table.assertLine(2, 20, 1, 1, 23);
    }

    @Test
    public void simpleRolSpanColSpanColision() {
        TableSimulator table = new TableSimulator();
        table.addLine().add(0).add(2, 1, 1);
        SingularTestUtil.assertException(() -> table.addLine().add(1,2,10), RuntimeException.class, "colision");
    }
}
