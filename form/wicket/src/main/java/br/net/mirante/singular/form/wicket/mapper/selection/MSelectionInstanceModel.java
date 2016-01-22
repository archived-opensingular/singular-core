package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.options.MOptionsConfig;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import org.apache.wicket.model.IModel;

import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings({"serial", "rawtypes"})
public class MSelectionInstanceModel<T> implements IModel<T>, IMInstanciaAwareModel<T> {

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
                    .map(c -> getSimpleSelection(c, list.getOptionsConfig()))
                    .collect(Collectors.toList());
        }
        return getSimpleSelection(target, target.getOptionsConfig());

    }

    @Override
    @SuppressWarnings("unchecked")
    public void setObject(T object) {
        MInstancia target = getTarget();
        if (object instanceof SelectOption) {
            setValueAt(target, (SelectOption) object, target.getOptionsConfig());
        } else if (object instanceof Collection) {
            setListValueAt(target, (Collection) object);
        } else if (object == null) {
            setValueAt(target, null, null);
        }
    }

    @SuppressWarnings("unchecked")
    protected T getSimpleSelection(MInstancia target, MOptionsConfig provider) {
        if (target != null) {
            String key = provider.getKeyFromOptions(target);
            String label = provider.getLabelFromKey(key);
            return (T) new SelectOption(label, key);
        }
        return null;
    }

    private MInstancia getTarget() {
        return model.getObject();
    }

    private void setValueAt(MInstancia instance, SelectOption object, MOptionsConfig provider) {
        if (object == null) {
            instance.clearInstance();
        } else if (instance != null) {
            object.copyValueToInstance(instance, provider);
        }
    }

    private void setListValueAt(MInstancia instance,
                                Collection<SelectOption> data) {
        if (data != null && instance instanceof MILista) {
            MILista<?> list = (MILista<?>) instance;
            list.clear();
            for (SelectOption o : data) {
                MInstancia element = list.addNovo();
                setValueAt(element, o, list.getOptionsConfig());
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