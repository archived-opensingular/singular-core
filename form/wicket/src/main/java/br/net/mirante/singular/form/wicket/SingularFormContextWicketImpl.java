package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.context.SingularFormContextImpl;

public class SingularFormContextWicketImpl extends SingularFormContextImpl<UIBuilderWicket, IWicketComponentMapper> implements SingularFormContextWicket {

    private UIBuilderWicket buildContext = new UIBuilderWicket();

    public SingularFormContextWicketImpl(SingularFormConfigWicketImpl config) {
        super(config);
    }

    @Override
    public UIBuilderWicket getUIBuilder() {
        return buildContext;
    }

}
