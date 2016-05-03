package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;

import java.io.Serializable;
import java.util.List;

public interface FilteredPagedProvider<R extends Serializable> extends Provider<R, SInstance> {

    @Override
    default List<R> load(ProviderContext<SInstance> context) {
        return load(context.getInstance(), context.getFilterInstance(), context.getFirst(), context.getCount());
    }

    void loadFilterDefinition(STypeComposite<?> filter);

    Long getSize(SInstance rootInstance, SInstance filter);

    List<R> load(SInstance rootInstance, SInstance filter, long first, long count);

    List<Column> getColumns();

    class Column implements Serializable {

        private String property;
        private String label;

        public static Column of(String property, String label) {
            return new Column(property, label);
        }

        public static Column of(String label) {
            return of(null, label);
        }

        Column(String property, String label) {
            this.property = property;
            this.label = label;
        }

        public String getProperty() {
            return property;
        }

        public String getLabel() {
            return label;
        }

    }
}

