package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import org.apache.wicket.model.IModel;

import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings({"serial", "rawtypes"})
public class MSelectionInstanceModel<T> implements IModel<T>,
        IMInstanciaAwareModel<T> {

    private IModel<? extends MInstancia> model;

    public MSelectionInstanceModel(IModel<? extends MInstancia> instanciaModel) {
        this.model = instanciaModel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        MInstancia target = getTarget();
        if (getTarget() instanceof MILista) {
            MILista list = (MILista) getTarget();
            return (T) list.getAllChildren()
                    .stream()
                    .map((x) -> getSimpleSelection(x))
                    .collect(Collectors.toList());
        }
        return getSimpleSelection(target);

    }

    @Override
    @SuppressWarnings("unchecked")
    public void setObject(T object) {
        if (object instanceof SelectOption) {
            setValueAt(getTarget(), (SelectOption) object);
        } else if (object instanceof Collection) {
            setListValueAt(getTarget(), (Collection) object);
        }
    }

    @SuppressWarnings("unchecked")
    protected T getSimpleSelection(MInstancia target) {
        if (target instanceof MSelectionableInstance) {
            MSelectionableInstance item = (MSelectionableInstance) target;
            return (T) new SelectOption<Object>(item.getSelectLabel(), item.getSelectValue(), target);
        }
        return null;
    }

    private MInstancia getTarget() {
        return model.getObject();
    }

    private void setValueAt(MInstancia instance, SelectOption object) {
        if (instance instanceof MSelectionableInstance) {
            MSelectionableInstance item = (MSelectionableInstance) instance;
            if (object != null) {
                item.setValueSelectLabel(object.getValue(), object.getSelectLabel());
            } else {
                item.setValueSelectLabel(null, null);
            }
        }
    }

    private void setListValueAt(MInstancia instance,
                                Collection<SelectOption<?>> data) {
        if (data != null && instance instanceof MILista) {
            MILista<?> list = (MILista<?>) instance;
            list.clear();
            for (SelectOption o : data) {
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