package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.context.SingularFormContextImpl;

public class SingularFormContextWicketImpl extends SingularFormContextImpl implements SingularFormContextWicket {

    private UIBuilderWicket buildContext;

    public SingularFormContextWicketImpl(SingularFormConfigWicketImpl config) {
        super(config);
    }

    @Override
    public UIBuilderWicket getUIBuilder() {
        if (buildContext == null) {
            buildContext = new UIBuilderWicket();
        }
        return buildContext;
    }

}
