package org.opensingular.lib.wicket.util.datatable;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableTreeProvider;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.datatable.column.BSTreeColumn;
import org.opensingular.lib.wicket.util.datatable.column.MetronicStatusColumn;
import org.opensingular.lib.wicket.util.datatable.column.MetronicStatusColumn.BadgeTypeMapper;
import org.opensingular.lib.wicket.util.resource.Icone;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class BSDataTableTest {

    @Test
    public void testDataTable() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                BaseDataProvider<BSDataTableTest.TestTO, Integer> dataProvider = new BaseDataProvider<BSDataTableTest.TestTO, Integer>() {
                    @Override
                    public long size() {
                        return TestTO.LIST.size();
                    }
                    @Override
                    public Iterator<? extends TestTO> iterator(int first, int count, Integer sortProperty, boolean ascending) {
                        return TestTO.page(first, count).iterator();
                    }
                };
                return new TemplatePanel(contentId, ""
                    + "<table wicket:id='table'></table>")
                        .add(BSDataTableBuilder.create(TestTO.class, Integer.class)
                            .setDataProvider(dataProvider.setSortDesc(0).setSortAsc(0))
                            .setRowsPerPage(3)
                            .setStripedRows(true)
                            .setHoverRows(true)
                            .setBorderedTable(true)
                            .setCondensedTable(true)
                            .withNoRecordsToolbar()
                            .appendPropertyColumn("ID", 0, "id")
                            .appendPropertyColumn("ID", 0, it -> it.id)
                            .appendPropertyColumn("Name1", 1, it -> it.name).configurePreviousColumn(col -> {})
                            .appendActionColumn("Actions", col -> col
                                .appendAction($m.ofValue("remove"), Icone.REMOVE, IBSAction.noop())
                                .appendAction($m.ofValue("edit"), $m.ofValue(Icone.PENCIL), IBSAction.noopIfNull(null)))
                            .build("table"));
            }
        });

        WicketUtils.findChildren(tester.getLastRenderedPage(), AbstractLink.class)
            .forEach(link -> {
                if (link.getDefaultModelObject() instanceof TestTO)
                    tester.clickLink(link.getPageRelativePath());
                if ((link instanceof OrderByLink) && WicketUtils.findClosestParent(link, Page.class).isPresent())
                    tester.clickLink(link.getPageRelativePath());
            });
    }

    @Test
    public void testFlexTable() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                BaseDataProvider<BSDataTableTest.TestTO, Integer> dataProvider = new BaseDataProvider<BSDataTableTest.TestTO, Integer>() {
                    @Override
                    public long size() {
                        return TestTO.LIST.size();
                    }
                    @Override
                    public Iterator<? extends TestTO> iterator(int first, int count, Integer sortProperty, boolean ascending) {
                        return TestTO.page(first, count).iterator();
                    }
                };
                BadgeTypeMapper<TestTO> badgeTypeMapper = (c, m) -> MetronicStatusColumn.BagdeType.INFO;
                return new TemplatePanel(contentId, ""
                    + "<table wicket:id='table'></table>")
                        .add(BSDataTableBuilder.create(TestTO.class, Integer.class)
                            .setDataProvider(dataProvider)
                            .appendPropertyColumn("ID", 0, it -> it.id).configurePreviousColumn(col -> col.setRowMergeIdFunction(it -> it.id))
                            .appendColumn(new MetronicStatusColumn<>($m.ofValue("Status"), it -> it.name))
                            .appendColumn(new MetronicStatusColumn<>($m.ofValue("Status"), "name"))
                            .appendColumn(new MetronicStatusColumn<>($m.ofValue("Status"), it -> it.name, badgeTypeMapper))
                            .appendColumn(new MetronicStatusColumn<>($m.ofValue("Status"), "name", badgeTypeMapper))
                            .appendColumn(new MetronicStatusColumn<>($m.ofValue("Status"), 1, it -> it.name))
                            .appendColumn(new MetronicStatusColumn<>($m.ofValue("Status"), 1, "name"))
                            .appendColumn(new MetronicStatusColumn<>($m.ofValue("Status"), 1, it -> it.name, badgeTypeMapper))
                            .appendColumn(new MetronicStatusColumn<>($m.ofValue("Status"), 1, "name", badgeTypeMapper))
                            .buildFlex("table"));
            }
        });
    }

    @Test
    public void testTreeTable() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                SortableTreeProvider<TestTO, Integer> treeProvider = new SortableTreeProvider<BSDataTableTest.TestTO, Integer>() {
                    @Override
                    public IModel<TestTO> model(TestTO object) {
                        return $m.ofValue(object);
                    }
                    @Override
                    public boolean hasChildren(TestTO node) {
                        return false;
                    }
                    @Override
                    public Iterator<? extends TestTO> getRoots() {
                        return TestTO.LIST.iterator();
                    }
                    @Override
                    public Iterator<? extends TestTO> getChildren(TestTO node) {
                        return new ArrayList<TestTO>().iterator();
                    }
                };
                return new TemplatePanel(contentId, ""
                    + "<table wicket:id='table'></table>")
                        .add(BSDataTableBuilder.create(TestTO.class, Integer.class)
                            .setTreeProvider(treeProvider)
                            .appendColumn(new BSTreeColumn<>($m.ofValue(), 1))
                            .appendPropertyColumn("ID", 0, it -> it.id)
                            .buildTree("table"));
            }
        });
    }

    private static class TestTO implements Serializable {
        public static final List<TestTO> LIST = new ArrayList<>();
        static {
            for (int i = 0; i < 10; i++)
                LIST.add(new TestTO(i, "item-" + i));
        }

        public int    id;
        public String name;
        public TestTO(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public static List<TestTO> page(long first, long count) {
            int start = (int) first;
            int end = (int) (first + count);
            return LIST.subList(start, end);
        }
    }
}
