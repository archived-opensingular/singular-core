package br.net.mirante.singular.form.wicket;

import org.apache.wicket.markup.html.form.FormComponent;

import br.net.mirante.singular.form.mform.MInstancia;

@FunctionalInterface
public interface IWicketComponentMapper {

    public FormComponent<?> create(MInstancia instancia, WicketBuildContext ctx);

}
