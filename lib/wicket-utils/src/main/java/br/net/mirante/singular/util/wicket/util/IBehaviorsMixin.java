/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.util;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.net.mirante.singular.util.wicket.jquery.JQuery;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.net.mirante.singular.commons.lambda.IBiConsumer;
import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.commons.lambda.ISupplier;
import br.net.mirante.singular.util.wicket.behavior.FormChoiceAjaxUpdateBehavior;
import br.net.mirante.singular.util.wicket.behavior.FormComponentAjaxUpdateBehavior;
import br.net.mirante.singular.util.wicket.behavior.IAjaxUpdateConfiguration;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;

@SuppressWarnings("serial")
public interface IBehaviorsMixin extends Serializable {

    default AttributeAppender attrAppender(String attribute, Serializable valueOrModel, String separator) {
        return attrAppender(attribute, valueOrModel, separator, Model.of(true));
    }
    default AttributeAppender attrAppender(String attribute, Serializable valueOrModel, String separator, IModel<Boolean> enabledModel) {
        return new AttributeAppender(attribute,
            (valueOrModel instanceof IModel<?>) ? (IModel<?>) valueOrModel : Model.of(valueOrModel),
            separator) {
            @Override
            public boolean isEnabled(Component component) {
                return Boolean.TRUE.equals(enabledModel.getObject());
            }
        };
    }

    default AttributeModifier attrRemover(String attribute, Serializable patternToRemove, boolean isolateWord) {
        return new AttributeModifier(attribute, patternToRemove) {
            @Override
            protected String newValue(String currentValue, String replacementValue) {
                String regex = (isolateWord) ? "\\b" + replacementValue + "\\b" : replacementValue;
                Matcher m = Pattern.compile(regex).matcher(currentValue);
                boolean result = m.find();
                if (result) {
                    StringBuffer sb = new StringBuffer();
                    do {
                        m.appendReplacement(sb, "");
                        result = m.find();
                    } while (result);
                    m.appendTail(sb);
                    return sb.toString();
                }
                return super.newValue(currentValue, replacementValue);
            }
        };
    }

    default AttributeModifier attr(String attribute, Serializable valueOrModel) {
        return attr(attribute, valueOrModel, Model.of(true));
    }
    default AttributeModifier attr(String attribute, Serializable valueOrModel, IModel<Boolean> enabledModel) {
        return new AttributeModifier(attribute,
            (valueOrModel instanceof IModel<?>) ? (IModel<?>) valueOrModel : Model.of(valueOrModel)) {
            @Override
            public boolean isEnabled(Component component) {
                return enabledModel.getObject();
            }
        };
    }

    default AttributeAppender classAppender(Serializable valueOrModel) {
        return classAppender(valueOrModel, Model.of(true));
    }

    default AttributeAppender classAppender(Serializable valueOrModel, IModel<Boolean> enabledModel) {
        return attrAppender("class", valueOrModel, " ", enabledModel);
    }

    default Behavior renderBodyOnly(IModel<Boolean> renderBodyOnly) {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setRenderBodyOnly(renderBodyOnly.getObject());
            }
        };
    }

    default Behavior visibleIf(ISupplier<Boolean> model) {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(model.get());
            }
        };
    }

    default Behavior visibleIf(IModel<Boolean> model) {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(model.getObject());
            }
        };
    }

    default Behavior visibleIfAlso(Component otherComponent) {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(otherComponent.isVisibleInHierarchy());
            }
        };
    }

    default Behavior enabledIf(IModel<Boolean> model) {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setEnabled(model.getObject());
            }
        };
    }

    default Behavior onConfigure(IConsumer<Component> onConfigure) {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                IConsumer.noopIfNull(onConfigure).accept(component);
            }
        };
    }

    default Behavior onComponentTag(IBiConsumer<Component, ComponentTag> onComponentTag) {
        return new Behavior() {
            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                IBiConsumer.noopIfNull(onComponentTag).accept(component, tag);
            }
        };
    }

    default <C extends Component> IAjaxUpdateConfiguration<C> addAjaxUpdate(C component) {
        return addAjaxUpdate(component, null);
    }

    @SuppressWarnings("unchecked")
    default <C extends Component> IAjaxUpdateConfiguration<C> addAjaxUpdate(C component, IBiConsumer<AjaxRequestTarget, Component> onUpdate) {

        final Behavior behavior;

        if (component instanceof RadioChoice<?> || component instanceof CheckBoxMultipleChoice<?> || component instanceof RadioGroup<?> || component instanceof CheckGroup<?>) {
            behavior = new FormChoiceAjaxUpdateBehavior(onUpdate);
            component.add(behavior);

            //        } else if (component instanceof TypeaheadField<?>) {
            //            TypeaheadField<?> tf = (TypeaheadField<?>) component;
            //            behavior = new TypeaheadAjaxUpdateBehavior(tf, onUpdate);
            //            tf.addToValueField(behavior);

            //        } else if (component instanceof MontrealSwitcher) {
            //            behavior = new FormComponentAjaxUpdateBehavior("montrealswitcher.change", onUpdate);
            //            component.add(behavior);

            //        } else if (component instanceof LocalDateField) {
            //            behavior = new FormComponentAjaxUpdateBehavior("changedate", onUpdate);
            //            component.add(behavior);

            //        } else if (component instanceof TextField && component.getParent() instanceof LocalDateRangePanel) {
            //            behavior = new FormComponentAjaxUpdateBehavior(LocalDateRangePanel.JS_EVENT_CHANGEDATE, onUpdate);
            //            component.add(behavior);

        } else if (component instanceof FormComponent<?>) {
            behavior = new FormComponentAjaxUpdateBehavior("change", onUpdate);
            component.add(behavior);

            //        } else if (component instanceof IOnAfterPopulateItemConfigurable) {
            //            behavior = new DynamicContainerAjaxUpdateBehavior(onUpdate);
            //            component.add(behavior);

        } else {
            return null;
        }

        return (IAjaxUpdateConfiguration<C>) behavior;
    }

    default Behavior on(String event, IFunction<Component, CharSequence> scriptFunction) {
        return onReadyScript(comp -> String.format("Wicket.Event.add('%s', '%s', function(event) { %s; });",
            comp.getMarkupId(), event, scriptFunction.apply(comp)));
    }

    default Behavior onReadyScript(ISupplier<CharSequence> scriptSupplier) {
        return onReadyScript(comp -> scriptSupplier.get());
    }

    default Behavior onReadyScript(IFunction<Component, CharSequence> scriptFunction) {
        return onReadyScript(scriptFunction,
            comp -> comp.isVisibleInHierarchy() && comp.isEnabledInHierarchy());
    }

    default Behavior onReadyScript(IFunction<Component, CharSequence> scriptFunction, IFunction<Component, Boolean> isEnabled) {
        return new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                response.render(OnDomReadyHeaderItem.forScript(scriptFunction.apply(component)));
            }
            @Override
            public boolean isEnabled(Component component) {
                return isEnabled.apply(component);
            }
        };
    }

    default Behavior onEnterDelegate(Component target) {
        return $b.onReadyScript(c -> {
            return JQuery.on(c, "keypress", "if((e.keyCode || e.which) == 13){e.preventDefault(); " + JQuery.$(target) + ".click();}");
        });
    }

}
