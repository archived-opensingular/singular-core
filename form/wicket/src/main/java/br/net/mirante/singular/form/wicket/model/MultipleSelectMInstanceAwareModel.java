package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MultipleSelectMInstanceAwareModel extends AbstractMInstanceAwareModel<List<Serializable>> {

    private static final long serialVersionUID = -4455601838581324870L;

    private final IModel<? extends SInstance>     model;
    private final List<SelectMInstanceAwareModel> selects;

    public MultipleSelectMInstanceAwareModel(IModel<? extends SInstance> model) {
        this.model = model;
        this.selects = new ArrayList<>();
        if (model.getObject() instanceof SIList) {
            final SIList list = (SIList) model.getObject();
            for (int i = 0; i < list.size(); i += 1) {
                selects.add(new SelectMInstanceAwareModel(new SInstanceItemListaModel<>(model, i)));
            }
        } else {
            throw new SingularFormException("Este model somente deve ser utilizado para tipo lista");
        }
    }

    @Override
    public SInstance getMInstancia() {
        return model.getObject();
    }

    @Override
    public List<Serializable> getObject() {
        return selects.stream().map(IModel::getObject).collect(Collectors.toList());
    }

    @Override
    public void setObject(List<Serializable> objects) {
        if (model.getObject() instanceof SIList) {
            final SIList list = (SIList) model.getObject();
            list.clear();
            selects.clear();
            for (int i = 0; i <= objects.size(); i += 1) {
                final Object o = objects.get(i);
                final SInstance newElement = list.addNew();
                model.getObject().asAtrProvider().getConverter().fillInstance(newElement, o);
                selects.add(new SelectMInstanceAwareModel(new SInstanceItemListaModel<>(model, i)));
            }
        } else {
            throw new SingularFormException("Este model somente deve ser utilizado para tipo lista");
        }
    }

}