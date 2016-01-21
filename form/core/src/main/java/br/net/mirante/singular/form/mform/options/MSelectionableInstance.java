package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MTipo;

/**
 * Represents an MInstancia that is of a kind of MSelectionableType.
 * It allows specific types to establish their own strategy for defining their key (id)
 * and value (visible selectLabel) of the instance.
 */
public interface MSelectionableInstance  extends MSelectionable{

    MTipo<?> getMTipo();

}
