package br.net.mirante.singular.util.wicket.behavior;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import br.net.mirante.singular.util.wicket.lambda.IBiConsumer;
import br.net.mirante.singular.util.wicket.lambda.ITriConsumer;
import br.net.mirante.singular.util.wicket.util.IOnAfterPopulateItemConfigurable;


@SuppressWarnings("serial")
public class DynamicContainerAjaxUpdateBehavior extends Behavior implements IAjaxUpdateConfiguration<Component> {

    private final static MetaDataKey<IAjaxUpdateConfiguration<Component>> CONFIGURATION_KEY = new MetaDataKey<IAjaxUpdateConfiguration<Component>>() {
    };

    private IBiConsumer<AjaxRequestTarget, Component> onUpdate;
    private ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError = ITriConsumer.noop();
    private IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes = IBiConsumer.noop();
    private boolean refreshTargetComponent;
    private Component targetComponent;

    public DynamicContainerAjaxUpdateBehavior(IBiConsumer<AjaxRequestTarget, Component> onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    public void bind(Component component) {
        if (!(component instanceof IOnAfterPopulateItemConfigurable)) {
            throw new AssertionError("Unexpected type: " + component.getClass().getName());
        }

        this.targetComponent = component;
        IOnAfterPopulateItemConfigurable rootContainer = (IOnAfterPopulateItemConfigurable) component;
        rootContainer.setOnAfterPopulateItem(item ->
            onConfigureInternal(item, (Component & IOnAfterPopulateItemConfigurable) rootContainer)
        );
    }

    @Override
    public void onConfigure(Component component) {
        if (!(component instanceof IOnAfterPopulateItemConfigurable)) {
            throw new AssertionError("Unexpected type: " + component.getClass().getName());
        }

        IOnAfterPopulateItemConfigurable rootContainer = (IOnAfterPopulateItemConfigurable) component;

        rootContainer.setOnAfterPopulateItem(item -> {
            onConfigureInternal(item, (Component & IOnAfterPopulateItemConfigurable) rootContainer);
        });
    }

    protected <C extends Component & IOnAfterPopulateItemConfigurable> void onConfigureInternal(Component component, C rootContainer) {
        IVisitor<Component, Void> visitor = (Component child, IVisit<Void> visit) -> {
            if (child instanceof AbstractRepeater) {
                visit.dontGoDeeper();

            } else {
                IAjaxUpdateConfiguration<Component> updateConfiguration = child.getMetaData(CONFIGURATION_KEY);
                if (updateConfiguration == null) {
                    updateConfiguration = $b.addAjaxUpdate(child, (t, c) -> {
                        onUpdate.accept(t, rootContainer);
                        if (refreshTargetComponent)
                            t.add(c);
                    });
                }

                if (updateConfiguration != null) {
                    child.setMetaData(CONFIGURATION_KEY, updateConfiguration);
                    updateConfiguration.setUpdateAjaxAttributes(updateAjaxAttributes);
                    updateConfiguration.setOnError((t, c, e) -> {
                        onError.accept(t, rootContainer, e);
                        if (refreshTargetComponent)
                            t.add(c);
                    });
                }
            }
        };
        if (component instanceof MarkupContainer) {
            ((MarkupContainer) component).visitChildren(Component.class, visitor);
        } else {
            visitor.component(component, null);
        }
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
        return targetComponent;
    }
}