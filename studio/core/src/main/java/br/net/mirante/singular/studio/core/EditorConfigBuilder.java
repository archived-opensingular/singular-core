package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeSimple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class EditorConfigBuilder {

    private List<Pair<String, STypeSimple<?, ?>>> columns = new ArrayList<>();
    private Integer defaultSortColumnIndex;
    private Class<? extends SPackage> formPackage;
    private String formName;


    public EditorConfigBuilder(Class<? extends SPackage> formPackage, String formName) {
        this.formPackage = formPackage;
        this.formName = formName;
    }

    public ListConfigurer list() {
        return new ListConfigurer();
    }

    public class ListConfigurer {


        public ColumnConfigurer column(STypeSimple column) {
            return column(column.asAtr().getLabel(), column);
        }

        public ColumnConfigurer column(String caption, STypeSimple<?, ?> column) {
            columns.add(Pair.of(caption, column));
            return new ColumnConfigurer(this);
        }

        public ListConfigurer delete(String s, STypeSimple<?, ?> column) {
            return this;
        }

        public ListConfigurer disableDelete() {
            return this;
        }

        public ListConfigurer disableInsert() {
            return this;
        }

        public ListConfigurer disableView() {
            return this;
        }

        public ListConfigurer disableEdit() {
            return this;
        }

        public FormConfigurer form() {
            return new FormConfigurer();
        }
    }

    public class ColumnConfigurer {

        private final ListConfigurer listConfigurer;

        public ColumnConfigurer(ListConfigurer listConfigurer) {
            this.listConfigurer = listConfigurer;
        }

        public ColumnConfigurer column(STypeSimple<?, ?> column) {
            return column(column.asAtr().getLabel(), column);
        }

        public ColumnConfigurer column(String caption, STypeSimple<?, ?> column) {
            columns.add(Pair.of(caption, column));
            return this;
        }

        public ListConfigurer sortBy(STypeSimple<?, ?> column) {
            for (int i = 0; i < columns.size(); i++) {
                Pair p = columns.get(i);
                if (p.getValue().equals(column)) {
                    defaultSortColumnIndex = i;
                    break;
                }
            }
            if (defaultSortColumnIndex == null) {
                throw new SingularStudioException("A coluna utilizada no sortBy não foi declarada previamente.");
            }
            return listConfigurer;
        }

        public ColumnConfigurer rowsPerPage(int i) {
            return this;
        }

        public ColumnConfigurer disableQuickFilter() {
            return this;
        }
    }


    public class FormConfigurer {
        public FormConfigurer validateBeforeSave(boolean b) {
            return this;
        }

        public FormConfigurer disableSave() {
            return this;
        }

        public FormConfigurer disableValidate() {
            return this;
        }

        public FormConfigurer disableClose() {
            return this;
        }
    }
}
