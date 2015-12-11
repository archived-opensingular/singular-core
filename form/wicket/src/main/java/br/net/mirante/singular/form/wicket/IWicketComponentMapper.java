package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

@FunctionalInterface
public interface IWicketComponentMapper extends Serializable {

    interface HintKey<T> extends Serializable {
        T getDefaultValue();
    }

    void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model, ViewMode viewMode);
}
