package br.net.mirante.singular.util.wicket.behavior;

import br.net.mirante.singular.util.wicket.lambda.IBiConsumer;
import br.net.mirante.singular.util.wicket.lambda.ITriConsumer;
import br.net.mirante.singular.util.wicket.util.WicketEventUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;

@SuppressWarnings({ "serial" })
public class FormComponentAjaxUpdateBehavior extends AjaxFormComponentUpdatingBehavior implements IAjaxUpdateConfiguration {

    private IBiConsumer<AjaxRequestTarget, Component> onUpdate;
    private ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError = (t, c, e) -> WicketEventUtils.sendAjaxErrorEvent(c, t);
    private IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes = IBiConsumer.noop();
    private boolean refreshTargetComponent;

    public FormComponentAjaxUpdateBehavior(String event, IBiConsumer<AjaxRequestTarget, Component> onUpdate) {
        super(event);
        this.onUpdate = onUpdate;
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        onUpdate(target, this.getComponent());
    }

    protected void onUpdate(AjaxRequestTarget target, Component component) {
        onUpdate.accept(target, component);
        if (refreshTargetComponent)
            target.add(component);
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
    public IAjaxUpdateConfiguration setOnError(ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError) {
        this.onError = ITriConsumer.noopIfNull(onError);
        return this;
    }
    @Override
    public IAjaxUpdateConfiguration setUpdateAjaxAttributes(IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes) {
        this.updateAjaxAttributes = IBiConsumer.noopIfNull(updateAjaxAttributes);
        return this;
    }
    @Override
    public IAjaxUpdateConfiguration setRefreshTargetComponent(boolean refresh) {
        this.refreshTargetComponent = refresh;
        return this;
    }
    @Override
    public Component getTargetComponent() {
        return getComponent();
    }
}