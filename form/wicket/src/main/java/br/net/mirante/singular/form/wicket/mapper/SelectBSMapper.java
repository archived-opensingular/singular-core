package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;

import br.net.mirante.singular.form.wicket.mapper.selection.SelectMapper;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class SelectBSMapper extends SelectMapper {

    @Override
    protected boolean isBSSelect(IModel<? extends MInstancia> model) {
        return true;
    }
}
