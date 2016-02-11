package br.net.mirante.singular.form.mform.basic.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.lambda.IFunction;

public class MListMasterDetailView extends MView {

    private boolean editElementEnabled = true;
    private boolean newElementEnabled = true;
    private boolean deleteElementsEnabled = true;
    private List<Column> columns = new ArrayList<>();

    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return tipo instanceof STypeLista;
    }

    public MListMasterDetailView col(STypeSimple tipo) {
        columns.add(new Column(tipo.getNome(), null, null));
        return this;
    }

    public MListMasterDetailView col(STypeSimple tipo, String customLabel) {
        columns.add(new Column(tipo.getNome(), customLabel, null));
        return this;
    }

    public MListMasterDetailView col(STypeSimple tipo, IFunction<SInstance, String> displayFunction) {
        columns.add(new Column(tipo.getNome(), null, displayFunction));
        return this;
    }

    public MListMasterDetailView disableEdit() {
        this.editElementEnabled = false;
        return this;
    }

    public MListMasterDetailView disableDelete() {
        this.deleteElementsEnabled = false;
        return this;
    }

    public MListMasterDetailView disableNew() {
        this.newElementEnabled = false;
        return this;
    }

    public boolean isEditElementEnabled() {
        return editElementEnabled;
    }

    public boolean isNewElementEnabled() {
        return newElementEnabled;
    }

    public boolean isDeleteElementsEnabled() {
        return deleteElementsEnabled;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public class Column implements Serializable {

        private String typeName;
        private String customLabel;
        private IFunction<SInstance, String> displayValueFunction;

        public Column() {
        }

        public Column(String typeName, String customLabel, IFunction<SInstance, String> displayValueFunction) {
            this.typeName = typeName;
            this.customLabel = customLabel;
            this.displayValueFunction = displayValueFunction;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getCustomLabel() {
            return customLabel;
        }

        public IFunction<SInstance, String> getDisplayValueFunction() {
            return displayValueFunction;
        }
    }
}
