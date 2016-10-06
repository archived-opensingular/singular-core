package br.net.mirante.singular.studio.core;

import org.opensingular.singular.form.STypeSimple;
import org.apache.commons.lang3.tuple.Pair;

public class CollectionEditorConfigBuilder {

    private CollectionEditorConfig editor = new CollectionEditorConfig();

    CollectionEditorConfigBuilder() {
    }

    public ListConfigurer list() {
        return new ListConfigurer();
    }

    CollectionEditorConfig getEditor() {
        return editor;
    }

    public class ListConfigurer {


        public ColumnConfigurer column(STypeSimple column) {
            return column(column.asAtr().getLabel(), column);
        }

        public ColumnConfigurer column(String caption, STypeSimple<?, ?> column) {
            editor.getColumns().add(Pair.of(caption, column.getName()));
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
            editor.getColumns().add(Pair.of(caption, column.getName()));
            return this;
        }

        public ListConfigurer sortBy(STypeSimple<?, ?> column) {
            for (int i = 0; i < editor.getColumns().size(); i++) {
                Pair<String, String> p = editor.getColumns().get(i);
                if (p.getValue().equals(column.getName())) {
                    editor.setDefaultSortColumnIndex(i);
                    break;
                }
            }
            if (editor.getDefaultSortColumnIndex() == null) {
                throw new SingularStudioException("A coluna utilizada no sortBy não foi declarada previamente.");
            }
            return listConfigurer;
        }

        public ColumnConfigurer rowsPerPage(int i) {
            editor.setRowsPerPage(i);
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
