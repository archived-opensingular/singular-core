/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.provider.Provider;
import br.net.mirante.singular.form.provider.ProviderContext;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentAbstractMapper;
import br.net.mirante.singular.form.wicket.model.SelectMInstanceAwareModel;
import br.net.mirante.singular.form.wicket.renderer.SingularChoiceRenderer;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.cycle.RequestCycle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectMapper extends ControlsFieldComponentAbstractMapper {

    private static final long serialVersionUID = 3837032981059048504L;

    @Override
    public Component appendInput() {
        final DropDownChoice<Serializable> dropDownChoice = new DropDownChoice<Serializable>(ctx.getCurrentInstance().getName(),
                new SelectMInstanceAwareModel(model),
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

            final Provider provider = model.getObject().asAtrProvider().getProvider();
            final List<Serializable> values = new ArrayList<>();

            if (provider != null) {
                final List<Serializable> result = provider.load(ProviderContext.of(model.getObject()));
                if (result != null) {
                    values.addAll(result);
                }
            }

            if (!model.getObject().isEmptyOfData()) {

                final SInstanceConverter converter = model.getObject().asAtrProvider().getConverter();
                final Serializable converted = converter.toObject(model.getObject());
                final RequestCycle requestCycle = RequestCycle.get();

                if (!values.contains(converted)) {
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
