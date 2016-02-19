package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.core.SIBoolean;
import br.net.mirante.singular.form.mform.options.MOptionsConfig;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import org.apache.wicket.model.IModel;

import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings({"serial", "rawtypes"})
public class MSelectionInstanceModel<T> implements IModel<T>, IMInstanciaAwareModel<T> {

    private IModel<? extends SInstance> model;

    public MSelectionInstanceModel(IModel<? extends SInstance> instanciaModel) {
        this.model = instanciaModel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        SInstance target = getTarget();
        if (getTarget() instanceof SList) {
            SList list = (SList) getTarget();
            return (T) list.getAllChildren()
                    .stream()
                    .map(c -> getSimpleSelection(c, list.getOptionsConfig()))
                    .collect(Collectors.toList());
        }
        return getSimpleSelection(target, target.getOptionsConfig());

    }

    @Override
    @SuppressWarnings("unchecked")
    public void setObject(T object) {
        SInstance target = getTarget();
        if (object instanceof SelectOption) {
            SelectOption sel = (SelectOption) object;
            setValueAt(target, (SelectOption) object, target.getOptionsConfig());
        } else if (object instanceof Collection) {
            setListValueAt(target, (Collection) object);
        } else if (object == null) {
            setValueAt(target, null, null);
        }
    }

    @SuppressWarnings("unchecked")
    protected T getSimpleSelection(SInstance target, MOptionsConfig provider) {
        if (target != null) {
//            String key = provider.getKeyFromOptions(target);
//            String label = provider.getLabelFromKey(key);
//            return (T) new SelectOption(label, key);
            SInstance v = provider.getValueFromKey(String.valueOf(target.getValue()));
            if(v!= null){
                return (T) new SelectOption(v.getSelectLabel(), v.getValue());
            }
            return (T) new SelectOption(null, null);
        }
        return null;
    }

    private SInstance getTarget() {
        return model.getObject();
    }

    private void setValueAt(SInstance instance, SelectOption object, MOptionsConfig provider) {
        if (object == null) {
            instance.clearInstance();
        } else if (instance != null) {
            object.copyValueToInstance(instance, provider);
        }
    }

    private void setListValueAt(SInstance instance,
                                Collection<SelectOption> data) {
        if (data != null && instance instanceof SList) {
            SList<?> list = (SList<?>) instance;
            list.clear();
            for (SelectOption o : data) {
                SInstance element = list.addNovo();
                setValueAt(element, o, list.getOptionsConfig());
            }
        }
    }

    @Override
    public void detach() {
        model.detach();
    }

    @Override
    public SInstance getMInstancia() {
        return getTarget();
    }

}