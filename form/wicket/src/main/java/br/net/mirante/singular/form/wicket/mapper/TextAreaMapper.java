package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MTextAreaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.behavior.CountDownBehaviour;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.StringValidator;

import java.util.Optional;

public class TextAreaMapper extends StringMapper {

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer,
                                 BSControls formGroup, IModel<? extends MInstancia> model,
                                 IModel<String> labelModel) {

        if (view instanceof MTextAreaView) {

            MTextAreaView mTextAreaView = (MTextAreaView) view;

            FormComponent<?> textArea = new TextArea<>(model.getObject().getNome(),
                    new MInstanciaValorModel<>(model)).setLabel(labelModel);
            formGroup.appendTextarea(textArea, mTextAreaView.getLinhas());

            Optional<Integer> maxSize = Optional.ofNullable(model.getObject().getValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO));

            if (maxSize.isPresent()) {
                textArea.add(StringValidator.maximumLength(maxSize.get()));
                //TODO Conforme solicitado pelo Daniel, o contador devera ser evoluido apos upgrade do Metronic
                //textArea.add(new CountDownBehaviour(maxSize.get()));
            }

            return textArea;
        }

        throw new WicketRuntimeException("TextAreaMapper deve ser utilizado com MTextAreaView");

    }
}
