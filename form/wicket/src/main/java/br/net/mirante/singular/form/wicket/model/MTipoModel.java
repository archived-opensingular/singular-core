package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import org.apache.wicket.model.IDetachable;

/**
 * Model
 */
public class MTipoModel
        implements IReadOnlyModel<SType<?>> {

    private transient Object rootTarget;

    public MTipoModel(Object rootTarget) {
        this.rootTarget = rootTarget;
    }

    @Override
    public SType<?> getObject() {
        return (SType<?>) rootTarget;
    }


    @Override
    public void detach() {
        if (rootTarget instanceof IDetachable) {
            ((IDetachable) rootTarget).detach();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rootTarget == null) ? 0 : rootTarget.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MTipoModel other = (MTipoModel) obj;
        if (rootTarget == null) {
            if (other.rootTarget != null)
                return false;
        } else if (!rootTarget.equals(other.rootTarget))
            return false;
        return true;
    }
}
