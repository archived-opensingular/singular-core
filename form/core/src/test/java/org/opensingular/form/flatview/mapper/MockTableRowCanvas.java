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

package org.opensingular.form.flatview.mapper;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.opensingular.lib.commons.canvas.table.TableRowCanvas;

import java.util.ArrayList;
import java.util.List;

public class MockTableRowCanvas implements TableRowCanvas {

    private List<String> columns = new ArrayList<>();

    @Override
    public void addColumn(String value) {
        columns.add(value);
    }

    public void assertColumn(int index, String val) {
        Assert.assertEquals(val, columns.get(index));
    }

    public void assertColumnCount(int count) {
        Assert.assertThat(columns, Matchers.hasSize(count));
    }

}
