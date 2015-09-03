package br.net.mirante.singular.form.wicket;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;

@FunctionalInterface
public interface IWicketComponentMapper {
    void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model);
}
//{ public Component create(String componentId, WicketBuildContext<?> ctx, IModel<? extends MInstancia> model);}
