package br.net.mirante.singular.form.wicket.model.instancia;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;

public interface IMInstanciaAwareModel<T> extends IModel<T> {
    MInstancia getMInstancia();
}
