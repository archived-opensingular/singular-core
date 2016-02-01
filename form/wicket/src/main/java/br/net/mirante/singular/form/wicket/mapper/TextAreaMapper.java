package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
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
                                 BSControls formGroup, IModel<? extends SInstance> model,
                                 IModel<String> labelModel) {

        if (view instanceof MTextAreaView) {

            MTextAreaView mTextAreaView = (MTextAreaView) view;

            final SInstance mi = model.getObject();
            FormComponent<?> textArea = new TextArea<>(mi.getNome(),new MInstanciaValorModel<>(model));
            textArea.setLabel(labelModel);
            formGroup.appendTextarea(textArea, mTextAreaView.getLinhas());

            Optional<Integer> maxSize = Optional.ofNullable(mi.getValorAtributo(SPackageBasic.ATR_TAMANHO_MAXIMO));

            if (maxSize.isPresent()) {
                textArea.add(StringValidator.maximumLength(maxSize.get()));
                textArea.add(new CountDownBehaviour());
            }

            return textArea;
        }

        throw new WicketRuntimeException("TextAreaMapper deve ser utilizado com MTextAreaView");

    }
}
