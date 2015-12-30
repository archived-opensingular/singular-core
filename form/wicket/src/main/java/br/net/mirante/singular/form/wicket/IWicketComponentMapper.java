package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import java.io.Serializable;

@FunctionalInterface
public interface IWicketComponentMapper extends UIComponentMapper {

    void buildView(WicketBuildContext ctx);

    @FunctionalInterface
    interface HintKey<T> extends Serializable {
        T getDefaultValue();
    }
}
