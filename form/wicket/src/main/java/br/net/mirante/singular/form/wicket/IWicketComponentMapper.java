package br.net.mirante.singular.form.wicket;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;

@FunctionalInterface
public interface IWicketComponentMapper extends Serializable {

    interface HintKey<T> extends Serializable {
        T getDefaultValue();
    }

    void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model);
}
