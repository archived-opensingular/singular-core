package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.context.SingularFormContext;

public interface SingularFormContextWicket extends SingularFormContext<IWicketComponentMapper> {

    public UIBuilderWicket getUIBuilder();
}
