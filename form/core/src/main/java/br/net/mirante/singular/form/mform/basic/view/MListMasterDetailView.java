package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.STypeSimple;

import java.util.LinkedHashMap;
import java.util.Map;

public class MListMasterDetailView extends MView {

    private boolean editElementEnabled = true;
    private boolean newElementEnabled = true;
    private boolean deleteElementsEnabled = true;
    private Map<String, String> columns = new LinkedHashMap<>();

    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return tipo instanceof STypeLista;
    }

    public MListMasterDetailView col(STypeSimple tipo) {
        columns.put(tipo.getNome(), null);
        return this;
    }

    public MListMasterDetailView col(STypeSimple tipo, String customLabel) {
        columns.put(tipo.getNome(), customLabel);
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

    public Map<String, String> getColumns() {
        return columns;
    }
}
