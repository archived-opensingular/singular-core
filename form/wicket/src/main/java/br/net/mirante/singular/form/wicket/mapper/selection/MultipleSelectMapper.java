/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.form.SIList;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.converter.SInstanceConverter;
import org.opensingular.singular.form.enums.PhraseBreak;
import org.opensingular.singular.form.provider.Provider;
import org.opensingular.singular.form.provider.ProviderContext;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import br.net.mirante.singular.form.wicket.model.MultipleSelectSInstanceAwareModel;
import br.net.mirante.singular.form.wicket.renderer.SingularChoiceRenderer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class MultipleSelectMapper extends AbstractControlsFieldComponentMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        final List<Serializable> opcoesValue = new ArrayList<>();

        if (model.getObject().getType() instanceof STypeList) {
            final Provider provider = model.getObject().asAtrProvider().getProvider();
            if (provider != null) {
                opcoesValue.addAll(provider.load(ProviderContext.of(ctx.getCurrentInstance())));
            }
        }

        /**
         * Dangling values
         */
        if (!model.getObject().isEmptyOfData()) {
            final SIList list = (SIList) model.getObject();
            for (int i = 0; i < list.size(); i += 1) {
                SInstance ins = list.get(i);
                final SInstanceConverter converter = list.asAtrProvider().getConverter();
                final Serializable converterted = converter.toObject(ins);
                if (!opcoesValue.contains(converterted)) {
                    opcoesValue.add(i, converterted);
                }
            }
        }

        return formGroupAppender(formGroup, model, opcoesValue);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ListMultipleChoice<?> retrieveChoices(IModel<? extends SInstance> model, final List<?> opcoesValue) {
        return new SListMultipleChoice(model.getObject().getName(), new MultipleSelectSInstanceAwareModel(model), opcoesValue, renderer(model));
    }

    protected Component formGroupAppender(BSControls formGroup,
                                          IModel<? extends SInstance> model,
                                          final List<?> opcoesValue) {
        final ListMultipleChoice<?> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setMaxRows(5), true, false);
        return choices;
    }

    protected IChoiceRenderer<Serializable> renderer(IModel<? extends SInstance> model) {
        return new SingularChoiceRenderer(model);
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final StringBuilder output = new StringBuilder();
        final SInstance mi = model.getObject();
        if (mi instanceof SIList) {
            final Collection children = ((SIList) mi).getChildren();
            final Iterator iterator = children.iterator();
            boolean first = true;
            while (iterator.hasNext()) {
                final SInstance val = (SInstance) iterator.next();
                final Serializable converted = mi.asAtrProvider().getConverter().toObject(val);
                final String label = mi.asAtrProvider().getDisplayFunction().apply(converted);
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
