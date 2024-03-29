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

import org.opensingular.lib.commons.canvas.table.TableBodyCanvas;
import org.opensingular.lib.commons.canvas.table.TableRowCanvas;

import java.util.ArrayList;
import java.util.List;

public class MockTableBodyCanvas implements TableBodyCanvas{
    private List<MockTableRowCanvas> tabRowCanvasMocks = new ArrayList<>();

    @Override
    public TableRowCanvas addRow() {
        MockTableRowCanvas mockTableRowCanvas = new MockTableRowCanvas();
        tabRowCanvasMocks.add(mockTableRowCanvas);
        return mockTableRowCanvas;
    }

    public MockTableRowCanvas getMockTableRowCanvas(int index){
        if (tabRowCanvasMocks.size() < index + 1) {
            throw new AssertionError("table body rows size is "+tabRowCanvasMocks.size());
        }
        return tabRowCanvasMocks.get(index);
    }
}
