package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectMapper;

@SuppressWarnings("serial")
public class SelectBSMapper extends SelectMapper {

    @Override
    protected boolean isBSSelect(IModel<? extends MInstancia> model) {
        return true;
    }
}
