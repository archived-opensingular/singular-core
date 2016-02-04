package br.net.mirante.singular.form.wicket.mapper.selection;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class SelectModalBuscaMapper implements ControlsFieldComponentMapper {


    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup,
                                 IModel<? extends SInstance> model, IModel<String> labelModel)
    {
        if (view instanceof MSelecaoPorModalBuscaView) {
            return formGroupAppender(formGroup, bodyContainer, model, (MSelecaoPorModalBuscaView) view);
        }
        throw new RuntimeException("SelectModalBuscaMapper only works with a MSelecaoPorModalBuscaView.");
    }

    protected Component formGroupAppender(BSControls formGroup, BSContainer modalContainer,
                                          IModel<? extends SInstance> model, MSelecaoPorModalBuscaView view)
    {
        final SelectInputModalContainer panel = new SelectInputModalContainer(model.getObject().getNome() + "inputGroup",
                formGroup, modalContainer, model, view, new OutputValueModel() {
            @Override
            public SInstance getMInstancia() {
                return model.getObject();
            }

            @Override
            public String getObject() {
                return getReadOnlyFormattedText(model);
            }
        });
        return panel.build();
    }


    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if (mi != null) {
            return mi.getSelectLabel();
        }
        return StringUtils.EMPTY;
    }

    abstract class OutputValueModel implements IMInstanciaAwareModel<String> {

        @Override
        public void setObject(String object) {}

        @Override
        public void detach() {}

    }
}

