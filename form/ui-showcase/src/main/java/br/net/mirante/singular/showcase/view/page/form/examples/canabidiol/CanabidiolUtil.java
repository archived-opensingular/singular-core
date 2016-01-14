package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;

public interface CanabidiolUtil {


    default boolean hasValue(MInstancia current, MTipo target) {
        return getValue(current, target) != null;
    }

    default MInstancia getInstance(MInstancia current, MTipo target) {
        return (MInstancia) current.findNearest(target).orElse(null);
    }

    default <T> T getValue(MInstancia current, MTipo target) {
        MInstancia targetInstance = getInstance(current, target);
        if (targetInstance == null) {
            return null;
        } else if (((MSelectionableInstance) targetInstance).getSelectValue() != null) {
            return (T) ((MSelectionableInstance) targetInstance).getSelectValue();
        } else if (targetInstance instanceof MIComposto) {
            return null;
        } else {
            return (T) targetInstance.getValor();
        }
    }
}
