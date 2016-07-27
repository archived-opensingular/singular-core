/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.cycle.RequestCycle;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.provider.AtrProvider;
import br.net.mirante.singular.form.provider.Provider;
import br.net.mirante.singular.form.provider.ProviderContext;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import br.net.mirante.singular.form.wicket.model.SelectSInstanceAwareModel;
import br.net.mirante.singular.form.wicket.renderer.SingularChoiceRenderer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class SelectMapper extends AbstractControlsFieldComponentMapper {

    private static final long serialVersionUID = 3837032981059048504L;

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        
        final DropDownChoice<Serializable> dropDownChoice = new DropDownChoice<Serializable>(ctx.getCurrentInstance().getName(),
                new SelectSInstanceAwareModel(model),
                new DefaultOptionsProviderLoadableDetachableModel(model),
                new SingularChoiceRenderer(model)) {
            @Override
            protected String getNullValidDisplayValue() {
                return "Selecione";
            }

            @Override
            protected String getNullKeyDisplayValue() {
                return null;
            }

            @Override
            public boolean isNullValid() {
                return true;
            }
        };
        formGroup.appendSelect(dropDownChoice);
        return dropDownChoice;
    }

    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if (mi != null && mi.getValue() != null) {
            Serializable instanceObject = mi.getType().asAtrProvider().getConverter().toObject(mi);
            if (instanceObject != null) {
                return mi.getType().asAtrProvider().getDisplayFunction().apply(instanceObject);
            }
        }
        return StringUtils.EMPTY;
    }


    public static class DefaultOptionsProviderLoadableDetachableModel extends LoadableDetachableModel<List<Serializable>> {

        private static final long serialVersionUID = -3852358882003412437L;

        private final IModel<? extends SInstance> model;

        public DefaultOptionsProviderLoadableDetachableModel(IModel<? extends SInstance> model) {
            this.model = model;
        }

        @Override
        protected List<Serializable> load() {

            final AtrProvider        atrProvider = model.getObject().asAtrProvider();
            final Provider           provider    = atrProvider.getProvider();
            final List<Serializable> values      = new ArrayList<>();

            if (provider != null) {
                final List<Serializable> result = provider.load(ProviderContext.of(model.getObject()));
                if (result != null) {
                    values.addAll(result);
                }
            }

            if (!model.getObject().isEmptyOfData()) {

                final SInstanceConverter        converter    = atrProvider.getConverter();
                final Serializable              converted    = converter.toObject(model.getObject());
                final RequestCycle              requestCycle = RequestCycle.get();
                final List<Object>              ids          = new ArrayList<>();
                final IFunction<Object, Object> idFunction   = atrProvider.getIdFunction();

                /**
                 * Collect All Ids
                 */
                values.forEach(v -> ids.add(idFunction.apply(v)));

                if (!ids.contains(idFunction.apply(converted))) {

                    /**
                     * Se for requisição Ajax, limpa o campo caso o valor não for encontrado,
                     * caso contrario mantem o valor.
                     */

                    if (requestCycle != null && requestCycle.find(AjaxRequestTarget.class) != null) {
                        model.getObject().clearInstance();
                    } else {
                        values.add(0, converted);
                    }
                }
            }

            return values;
        }
    }

}
