package br.net.mirante.singular.view.page.processo;

import static com.google.common.collect.Lists.*;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;

import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.view.SingularWicketContainer;

public class ProcessosPage extends WebPage implements SingularWicketContainer<ProcessosPage, Void> {

    public ProcessosPage() {

        BaseDataProvider<String, String> dataProvider = new BaseDataProvider<String, String>() {
            @Override
            public Iterator<? extends String> iterator(int first, int count, String sortProperty, boolean ascending) {
                List<String> list = newArrayList();
                for (int i = first; i < first + count; i++)
                    list.add(String.valueOf(i));
                return list.iterator();
            }
            @Override
            public long size() {
                return 100;
            }
        };

        add(new BSDataTableBuilder<>(dataProvider)
            .appendPropertyColumn($m.ofValue("A"), it -> it.toString())
            .build("processos"));
    }
}
