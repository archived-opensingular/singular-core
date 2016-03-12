package br.net.mirante.singular.form.mform.basic.view;

import java.util.Collections;
import java.util.List;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.options.SSelectionableType;
import static com.google.common.collect.Lists.newArrayList;

@SuppressWarnings("serial")
public class SViewSelectionBySearchModal extends SView {

    private String modalTitle;

    private List<String> searchFields = newArrayList();

    @Override
    public boolean isApplicableFor(SType<?> type) {
        return type instanceof SSelectionableType;
    }

    public SViewSelectionBySearchModal setTituloModal(String modalTitle){
        this.modalTitle = modalTitle;
        return this;
    }


    public void setAdditionalFields(String ... fields) {
        Collections.addAll(searchFields, fields);
    }

    public void setAdditionalFields(STypeSimple... tipos) {
        for(STypeSimple f : tipos){
            searchFields.add(f.getNameSimple());
        }
    }

    public List<String> searchFields(){
        return newArrayList(searchFields);
    }
}
