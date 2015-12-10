package br.net.mirante.singular.form.wicket.mapper.selection;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

@SuppressWarnings({ "serial", "rawtypes" })
class MSelectionInstanceModel implements IModel<SelectOption>,
    IMInstanciaAwareModel<SelectOption>{

    private IModel<? extends MInstancia> model;
    
    public MSelectionInstanceModel(IModel<? extends MInstancia> instanciaModel) {
        this.model = instanciaModel;
    }
    
    @Override
    public SelectOption getObject() {
        if(model.getObject() instanceof MISimples){
            Object value = ((MISimples) model.getObject()).getValor();
            String v = value != null ? value.toString() : null;
            return new SelectOption<String>(v, v);
        }else if (model.getObject() instanceof MISelectItem){
            MISelectItem item = (MISelectItem) model.getObject();
            return new SelectOption<String>(item.getFieldId(), item.getFieldValue());
        }
        return null;
    }

    @Override
    public void setObject(SelectOption object) {
        MInstancia instance = model.getObject();
        if(instance instanceof MISimples){
            Object value = null;
            if(object != null) value = object.getValue();
            instance.setValor(value);
        }
        else if(instance instanceof MISelectItem) {
            MISelectItem item = (MISelectItem) instance;
            if(object != null){
                item.setValorItem(object.getKey(), object.getValue());
            }else{
                item.setValorItem(null, null); 
            }
        }
    }

    @Override
    public void detach() {
        model.detach();
    }

    @Override
    public MInstancia getMInstancia() {
        return model.getObject();
    }
    
}