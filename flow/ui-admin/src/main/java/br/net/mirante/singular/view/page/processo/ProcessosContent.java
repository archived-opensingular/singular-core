package br.net.mirante.singular.view.page.processo;

import java.util.Iterator;
import java.util.List;

import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

import static org.apache.wicket.util.lang.Generics.newArrayList;

public class ProcessosContent extends Content implements SingularWicketContainer<ProcessosContent, Void> {

    public ProcessosContent(String id, boolean withSideBar) {
        super(id, false, withSideBar, true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BaseDataProvider<String, String> dataProvider = new BaseDataProvider<String, String>() {
            @Override
            public Iterator<? extends String> iterator(int first, int count, String sortProperty, boolean ascending) {
                List<String> list = newArrayList();
                for (int i = first; i < first + count; i++)
                    if (ascending) {
                        list.add(String.valueOf(i));
                    } else {
                        list.add(0, String.valueOf(i));
                    }
                return list.iterator();
            }

            @Override
            public long size() {
                return 100;
            }
        };

        add(new BSDataTableBuilder<>(dataProvider)
                .appendPropertyColumn(getMessage("label.table.column.name"), "name", String::toString)
                .appendPropertyColumn(getMessage("label.table.column.category"), "category", String::toString)
                .appendPropertyColumn(getMessage("label.table.column.version"), "version", String::toString)
                .appendActionColumn($m.ofValue(""), column -> {
                }).build("processos"));
    }

    @Override
    protected String getContentTitlelKey() {
        return "label.content.title";
    }

    @Override
    protected String getContentSubtitlelKey() {
        return "label.content.subtitle";
    }
}
