/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;

import br.net.mirante.singular.lambda.IBiConsumer;
import br.net.mirante.singular.lambda.ITriConsumer;
import br.net.mirante.singular.util.wicket.util.WicketEventUtils;

@SuppressWarnings({ "serial" })
public class FormChoiceAjaxUpdateBehavior extends AjaxFormChoiceComponentUpdatingBehavior implements IAjaxUpdateConfiguration<Component> {

    private IBiConsumer<AjaxRequestTarget, Component> onUpdate;
    private ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError = (t, c, e) -> WicketEventUtils.sendAjaxErrorEvent(c, t);
    private IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes = IBiConsumer.noop();
    private boolean refreshTargetComponent;

    public FormChoiceAjaxUpdateBehavior(IBiConsumer<AjaxRequestTarget, Component> onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        onUpdate.accept(target, this.getComponent());
        if (refreshTargetComponent)
            target.add(getComponent());
    }
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        if (updateAjaxAttributes != null) {
            updateAjaxAttributes.accept(getComponent(), attributes);
        }
    }
    protected void onError(AjaxRequestTarget target, RuntimeException e) {
        this.onError.accept(target, getComponent(), e);
        if (refreshTargetComponent)
            target.add(getComponent());
    }
    @Override
    public IAjaxUpdateConfiguration<Component> setOnError(ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError) {
        this.onError = ITriConsumer.noopIfNull(onError);
        return this;
    }
    @Override
    public IAjaxUpdateConfiguration<Component> setUpdateAjaxAttributes(IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes) {
        this.updateAjaxAttributes = IBiConsumer.noopIfNull(updateAjaxAttributes);
        return this;
    }
    @Override
    public IAjaxUpdateConfiguration<Component> setRefreshTargetComponent(boolean refresh) {
        this.refreshTargetComponent = refresh;
        return this;
    }
    @Override
    public Component getTargetComponent() {
        return getComponent();
    }
}