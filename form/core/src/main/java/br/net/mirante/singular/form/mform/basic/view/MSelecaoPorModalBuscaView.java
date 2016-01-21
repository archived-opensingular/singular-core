package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.options.MSelectionableType;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@SuppressWarnings("serial")
public class MSelecaoPorModalBuscaView extends MView {

    private String tituloModal;

    private List<String> searchFields = newArrayList();

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MSelectionableType;
    }

    public MSelecaoPorModalBuscaView setTituloModal(String tituloModal){
        this.tituloModal = tituloModal;
        return this;
    }


    public void setAdditionalFields(String ... fields) {
        for(String f : fields){
            searchFields.add(f);
        }
    }

    public void setAdditionalFields(MTipoSimples... tipos) {
        for(MTipoSimples f : tipos){
            searchFields.add(f.getNomeSimples());
        }
    }

    public List<String> searchFields(){
        return newArrayList(searchFields);
    }
}
