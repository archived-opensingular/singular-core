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
