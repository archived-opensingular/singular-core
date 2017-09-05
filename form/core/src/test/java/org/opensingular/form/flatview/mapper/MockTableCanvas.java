package org.opensingular.form.flatview.mapper;

import org.opensingular.lib.commons.canvas.table.TableBodyCanvas;
import org.opensingular.lib.commons.canvas.table.TableCanvas;
import org.opensingular.lib.commons.canvas.table.TableHeadCanvas;

public class MockTableCanvas implements TableCanvas {
    MockTableBodyCanvas mockTableBodyCanvas = new MockTableBodyCanvas();
    MockTableHeadCanvas mockTableHeadCanvas = new MockTableHeadCanvas();

    @Override
    public TableHeadCanvas getTableHeader() {
        return mockTableHeadCanvas;
    }

    @Override
    public TableBodyCanvas getTableBody() {
        return mockTableBodyCanvas;
    }

    public MockTableHeadCanvas getMockTableHeader() {
        return mockTableHeadCanvas;
    }

    public MockTableBodyCanvas getMockTableBody() {
        return mockTableBodyCanvas;
    }
}
