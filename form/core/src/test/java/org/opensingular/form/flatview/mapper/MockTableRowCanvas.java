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
