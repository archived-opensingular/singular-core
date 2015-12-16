package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

@SuppressWarnings({ "serial", "rawtypes" })
class MSelectionInstanceModel<T> implements IModel<T>,
    IMInstanciaAwareModel<T>{

    private IModel<? extends MInstancia> model;
    
    public MSelectionInstanceModel(IModel<? extends MInstancia> instanciaModel) {
        this.model = instanciaModel;
    }
    
    @Override @SuppressWarnings("unchecked")
    public T getObject() {
        MInstancia target = getTarget();
        if(getTarget() instanceof MILista){
            MILista list = (MILista) getTarget();
            return (T) list.getAllChildren()
                    .stream()
                    .map( (x) -> getSimpleSelection(x))
                    .collect(Collectors.toList());
        }
        return getSimpleSelection(target);
        
    }

    @SuppressWarnings("unchecked")
    protected T getSimpleSelection(MInstancia target) {
        if(target instanceof MISimples){
            Object value = ((MISimples) target).getValor();
            String v = value != null ? value.toString() : null;
            return (T) new SelectOption<String>(v, v, target);
        }else if (target instanceof MISelectItem){
            MISelectItem item = (MISelectItem) target;
            return (T) new SelectOption<String>(item.getFieldId(), item.getFieldValue(), target);
        }
        return null;
    }

    private MInstancia getTarget() {
        return model.getObject();
    }

    @Override @SuppressWarnings("unchecked")
    public void setObject(T object) {
        if(object instanceof SelectOption){
            setValueAt(getTarget(), (SelectOption)object);
        }else if (object instanceof Collection){
            setListValueAt(getTarget(), (Collection)object);
        }
    }

    private void setValueAt(MInstancia instance, SelectOption object) {
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
    
    private void setListValueAt(MInstancia instance, 
                                    Collection<SelectOption<?>> data) {
        if(data != null && instance instanceof MILista){
            MILista<?> list = (MILista<?>) instance;
            list.clear();
            for(SelectOption o : data){
                MInstancia element = list.addNovo();
                setValueAt(element, o);
            }
        }
    }

    @Override
    public void detach() {
        model.detach();
    }

    @Override
    public MInstancia getMInstancia() {
        return getTarget();
    }
    
}