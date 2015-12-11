package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;

import java.util.LinkedHashMap;
import java.util.Map;

public class MListMasterDetailView extends MView {

    private boolean editElement = true;
    private boolean newElement = true;
    private boolean deleteElements = true;
    private Map<String, String> columns = new LinkedHashMap<>();

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoLista;
    }

    public MListMasterDetailView col(MTipoSimples tipo) {
        columns.put(tipo.getNome(), null);
        return this;
    }

    public MListMasterDetailView col(MTipoSimples tipo, String customLabel) {
        columns.put(tipo.getNome(), customLabel);
        return this;
    }


    public MListMasterDetailView disableEdit() {
        this.editElement = false;
        return this;
    }

    public MListMasterDetailView disableDelete() {
        this.deleteElements = false;
        return this;
    }

    public MListMasterDetailView disableNew() {
        this.newElement = false;
        return this;
    }

    public boolean isEditElement() {
        return editElement;
    }

    public boolean isNewElement() {
        return newElement;
    }

    public boolean isDeleteElements() {
        return deleteElements;
    }

    public Map<String, String> getColumns() {
        return columns;
    }
}
