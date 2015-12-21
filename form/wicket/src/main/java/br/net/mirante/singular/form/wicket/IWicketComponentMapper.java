package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public interface IWicketComponentMapper extends UIComponentMapper {

    void buildForEdit(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model);

    void buildForView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model);

    interface HintKey<T> extends Serializable {
        T getDefaultValue();
    }
}
