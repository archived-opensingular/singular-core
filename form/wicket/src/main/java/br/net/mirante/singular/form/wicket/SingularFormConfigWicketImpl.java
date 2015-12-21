package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.context.SingularFormConfigImpl;

import java.util.Map;

public class SingularFormConfigWicketImpl extends SingularFormConfigImpl<UIBuilderWicket, IWicketComponentMapper>
        implements SingularFormConfigWicket, SingularFormContextWicket {

    private UIBuilderWicket buildContext = new UIBuilderWicket();

    @Override
    public void setCustomMappers(Map<Class<? extends MTipo>, Class<IWicketComponentMapper>> customMappers) {
        if (customMappers != null) {
            for (Map.Entry<Class<? extends MTipo>, Class<IWicketComponentMapper>> entry : customMappers.entrySet()) {
                buildContext.getViewMapperRegistry().register(entry.getKey(), () -> {
                    try {
                        return entry.getValue().newInstance();
                    } catch (Exception e) {
                        throw new SingularFormException("Não é possível instanciar o mapper: " +
                                entry.getValue() +
                                ". É preciso um construtor default");
                    }
                });

            }
        }
    }

    @Override
    public UIBuilderWicket getUIBuilder() {
        return buildContext;
    }


    @Override
    public SingularFormContextWicket getContext() {
        return (SingularFormContextWicket) super.getContext();
    }
}
