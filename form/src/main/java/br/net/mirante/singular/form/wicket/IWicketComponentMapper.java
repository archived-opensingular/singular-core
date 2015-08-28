package br.net.mirante.singular.form.wicket;

import org.apache.wicket.Component;

import br.net.mirante.singular.form.mform.MInstancia;

@FunctionalInterface
public interface IWicketComponentMapper {
    public Component create(String componentId, WicketBuildContext ctx, MInstancia instancia);
}
