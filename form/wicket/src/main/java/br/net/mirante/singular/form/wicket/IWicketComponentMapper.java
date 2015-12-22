package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

@FunctionalInterface
public interface IWicketComponentMapper extends UIComponentMapper {

    void buildView(WicketBuildContext ctx, IModel<? extends MInstancia> model);

    interface HintKey<T> extends Serializable {
        T getDefaultValue();
    }
}
