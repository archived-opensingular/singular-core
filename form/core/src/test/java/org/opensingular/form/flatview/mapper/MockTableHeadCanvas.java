package org.opensingular.form.flatview.mapper;

import org.opensingular.lib.commons.canvas.table.TableHeadCanvas;
import org.opensingular.lib.commons.canvas.table.TableRowCanvas;

import java.util.ArrayList;
import java.util.List;

public class MockTableHeadCanvas implements TableHeadCanvas {
    private List<MockTableRowCanvas> tabRowCanvasMocks = new ArrayList<>();

    @Override
    public TableRowCanvas addRow() {
        MockTableRowCanvas mockTableRowCanvas = new MockTableRowCanvas();
        tabRowCanvasMocks.add(mockTableRowCanvas);
        return mockTableRowCanvas;
    }

    public MockTableRowCanvas getMockTableRowCanvas(int index){
        if (tabRowCanvasMocks.size() < index + 1) {
            throw new AssertionError("table header rows size is "+tabRowCanvasMocks.size());
        }
        return tabRowCanvasMocks.get(index);
    }
}
