package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;

public class SelectBSMapper extends SelectMapper {

    @Override
    protected boolean isBSSelect(IModel<? extends MInstancia> model) {
        return true;
    }
}
