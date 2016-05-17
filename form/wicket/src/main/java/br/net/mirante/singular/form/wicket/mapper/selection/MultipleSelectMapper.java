/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.enums.PhraseBreak;
import br.net.mirante.singular.form.provider.Provider;
import br.net.mirante.singular.form.provider.ProviderContext;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentAbstractMapper;
import br.net.mirante.singular.form.wicket.model.MultipleSelectMInstanceAwareModel;
import br.net.mirante.singular.form.wicket.renderer.SingularChoiceRenderer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class MultipleSelectMapper extends ControlsFieldComponentAbstractMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput() {
        final List<Serializable> opcoesValue = new ArrayList<>();

        if (model.getObject().getType() instanceof STypeList) {
            final Provider provider = model.getObject().asAtrProvider().getProvider();
            if(provider != null) {
                opcoesValue.addAll(provider.load(ProviderContext.of(ctx.getCurrentInstance())));
            }
        }

        /**
         * Dangling values
         */
        if (!model.getObject().isEmptyOfData()) {
            final SIList list = (SIList) model.getObject();
            for (int i = 0; i < list.size(); i += 1) {
                SInstance                ins          = list.get(i);
                final SInstanceConverter converter    = list.asAtrProvider().getConverter();
                final Serializable       converterted = converter.toObject(ins);
                if (!opcoesValue.contains(converterted)) {
                    opcoesValue.add(i, converterted);
                }
            }
        }

        return formGroupAppender(formGroup, model, opcoesValue);

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ListMultipleChoice<?> retrieveChoices(IModel<? extends SInstance> model, final List<?> opcoesValue) {
        return new SListMultipleChoice(model.getObject().getName(), new MultipleSelectMInstanceAwareModel(model), opcoesValue, renderer());
    }

    @SuppressWarnings("rawtypes")
    protected Component formGroupAppender(BSControls formGroup,
                                          IModel<? extends SInstance> model,
                                          final List<?> opcoesValue) {
        final ListMultipleChoice<?> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setMaxRows(5), true, false);
        return choices;
    }

    @SuppressWarnings("rawtypes")
    protected IChoiceRenderer<Serializable> renderer() {
        return new SingularChoiceRenderer(model);
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final StringBuilder output = new StringBuilder();
        final SInstance     mi     = model.getObject();
        if (mi instanceof SIList) {
            final Collection children = ((SIList) mi).getChildren();
            final Iterator   iterator = children.iterator();
            boolean          first    = true;
            while (iterator.hasNext()) {
                final SInstance    val       = (SInstance) iterator.next();
                final Serializable converted = mi.asAtrProvider().getConverter().toObject(val);
                final String       label     = mi.asAtrProvider().getDisplayFunction().apply(converted);
                if (first) {
                    output.append(label);
                    first = false;
                } else {
                    //TODO implementar logica de auto detecção
                    final PhraseBreak phraseBreak = mi.asAtr().phraseBreak();
                    switch (phraseBreak) {
                        case BREAK_LINE:
                            output.append("\n");
                            break;
                        case COMMA:
                            output.append(", ");
                            break;
                    }
                    output.append(label);
                }
            }
        }
        return output.toString();
    }

}
