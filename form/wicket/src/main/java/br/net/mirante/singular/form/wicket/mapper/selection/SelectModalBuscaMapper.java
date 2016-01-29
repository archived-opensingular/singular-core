package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class SelectModalBuscaMapper implements ControlsFieldComponentMapper {


    public Component appendInput(MView view, BSContainer bodyContainer,
                                 BSControls formGroup, IModel<? extends SInstance2> model,
                                 IModel<String> labelModel) {
        if (view instanceof MSelecaoPorModalBuscaView) {
            return formGroupAppender(formGroup, bodyContainer, model, (MSelecaoPorModalBuscaView) view);
        }
        throw new RuntimeException("SelectModalBuscaMapper only works with a MSelecaoPorModalBuscaView.");
    }

    protected Component formGroupAppender(BSControls formGroup, BSContainer modalContainer,
                                          IModel<? extends SInstance2> model,
                                          MSelecaoPorModalBuscaView view) {
        SelectInputModalContainer panel = new SelectInputModalContainer(
                model.getObject().getNome() + "inputGroup",
                formGroup, modalContainer, model, view,
                new Model<String>() {
                    @Override
                    public String getObject() {
                        return SelectModalBuscaMapper.this.getReadOnlyFormattedText(model);
                    }

                });
        return panel.build();
    }


    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance2> model) {
        final SInstance2 mi = model.getObject();
        if (mi != null) {
            return mi.getSelectLabel();
        }
        return StringUtils.EMPTY;
    }
}

