package br.net.mirante.singular.form.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;

@FunctionalInterface
public interface IWicketComponentMapper {
    public Component create(String componentId, WicketBuildContext ctx, IModel<? extends MInstancia> instancia);
}
