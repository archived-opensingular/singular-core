/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.datatable.column;

import static org.opensingular.lib.wicket.util.util.WicketUtils.*;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.commons.lambda.IBiFunction;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.resource.IconeView;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.datatable.IBSAction;
import org.opensingular.lib.wicket.util.resource.Icone;

public class BSActionPanel<T> extends Panel {

    public static final String  LINK_ID  = "link";
    public static final String  ICONE_ID = "icone";
    public static final String  LABEL_ID = "label";

    private final RepeatingView actions  = new RepeatingView("actions");

    public BSActionPanel(String id, IModel<T> rowModel) {
        super(id, rowModel);
        add(actions);
    }

    public BSActionPanel<T> appendAction(IModel<?> labelModel, IModel<Icone> iconeModel, IBiFunction<String, IModel<T>, MarkupContainer> linkFactory) {
        return appendAction(new ActionConfig<T>().labelModel(labelModel).iconeModel(iconeModel)
            .stripeModel(null)
            .linkFactory(linkFactory)
            .styleClasses($m.ofValue("black")).withText(true));
    }

    public BSActionPanel<T> appendAction(ActionConfig<T> actionConfig) {

        MarkupContainer link = actionConfig.linkFactory.apply(LINK_ID, getModel());

        actions.add(new WebMarkupContainer(actions.newChildId())
            .add(link.add($b.attrAppender("class", actionConfig.styleClasses, " "))));

        if (actionConfig.stripeModel != null) {
            link.add($b.attrAppender("class", actionConfig.stripeModel, " "));
        }

        if (link.get(ICONE_ID) == null) {
            link.add(new IconeView(ICONE_ID, actionConfig.iconeModel, actionConfig.iconeStyle, actionConfig.iconeClass));
        }

        if (actionConfig.labelModel != null) {
            if (actionConfig.withText) {
                link.add(new Label(LABEL_ID, actionConfig.labelModel));
            } else {
                link.add($b.attr("title", actionConfig.labelModel));
                link.add(new WebMarkupContainer(LABEL_ID));
            }
        }

        link.add($b.attr("data-toggle", "tooltip"));
        link.add($b.attr("data-placement", "bottom"));

        if (actionConfig.titleFunction != null) {
            link.add($b.attr("title", actionConfig.titleFunction.apply(getModel())));
        } else {
            link.add($b.attr("title", actionConfig.labelModel));
        }

        return this;
    }

    public BSActionPanel<T> appendAction(ActionConfig<T> actionConfig, IBiFunction<String, IModel<T>, MarkupContainer> linkFactory) {
        return appendAction(actionConfig.linkFactory(linkFactory));
    }

    public BSActionPanel<T> appendAction(ActionConfig<T> config, IBSAction<T> action) {
        if (config.linkFactory == null) {
            return appendAction(config, (String childId, IModel<T> rowModel) -> new ActionAjaxLink<T>(childId, rowModel) {
                @Override
                public void onInitialize() {
                    super.onInitialize();
                    this.add($b.attrAppender("style", config.style, " "));
                }
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    this.setVisible(action.isVisible(this.getModel()));
                    this.setEnabled(action.isEnabled(this.getModel()));
                }
                @Override
                public void onAction(AjaxRequestTarget target) {
                    action.execute(target, this.getModel(), this);
                }
                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                    super.updateAjaxAttributes(attributes);
                    action.updateAjaxAttributes(attributes);
                }
            });
        } else {
            return appendAction(config, config.linkFactory);
        }
    }

    protected void onConfigure() {
        super.onConfigure();
        for (Component comp : actions) {
            MarkupContainer container = (MarkupContainer) comp.get(LINK_ID);

            if (container.get(ICONE_ID) == null) {
                container.add(new IconeView(ICONE_ID));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    @SuppressWarnings("unchecked")
    public T getModelObject() {
        return (T) getDefaultModelObject();
    }

    public static class ActionConfig<T> implements Serializable {

        protected IModel<?>                                       labelModel   = $m.ofValue("");
        protected IModel<Icone>                                   iconeModel;
        protected IModel<String>                                  iconeStyle;
        protected IModel<String>                                  iconeClass;
        protected IModel<String>                                  stripeModel;
        protected IModel<String>                                  styleClasses = $m.ofValue("btn default btn-xs black");
        protected IModel<String>                                  style;
        protected IFunction<IModel<T>, String>                    titleFunction;
        protected boolean                                         withText     = false;
        protected IBiFunction<String, IModel<T>, MarkupContainer> linkFactory;
        protected IFunction<IModel<T>, Boolean>                   visibleFor   = m -> Boolean.TRUE;

        public ActionConfig<T> labelModel(IModel<?> labelModel) {
            this.labelModel = labelModel;
            return this;
        }

        public ActionConfig<T> iconeModel(IModel<Icone> iconeModel) {
            return iconeModel(iconeModel, null, null);
        }

        public ActionConfig<T> iconeModel(IModel<Icone> iconeModel, IModel<String> iconeStyle) {
            return iconeModel(iconeModel, iconeStyle, null);
        }

        public ActionConfig<T> iconeModel(IModel<Icone> iconeModel, IModel<String> iconeStyle, IModel<String> iconeClass) {
            this.iconeModel = iconeModel;
            this.iconeStyle = iconeStyle;
            this.iconeClass = iconeClass;
            return this;
        }

        public ActionConfig<T> stripeModel(IModel<String> stripeModel) {
            this.stripeModel = stripeModel;
            return this;
        }

        public ActionConfig<T> styleClasses(IModel<String> buttonModel) {
            this.styleClasses = buttonModel;
            return this;
        }

        public ActionConfig<T> withText(boolean withText) {
            this.withText = withText;
            return this;
        }

        public ActionConfig<T> style(IModel<String> style) {
            this.style = style;
            return this;
        }

        public ActionConfig<T> linkFactory(IBiFunction<String, IModel<T>, MarkupContainer> linkFactory) {
            this.linkFactory = linkFactory;
            return this;
        }

        public ActionConfig<T> titleFunction(IFunction<IModel<T>, String> titleFunction) {
            this.titleFunction = titleFunction;
            return this;
        }

        public ActionConfig<T> visibleFor(IFunction<IModel<T>, Boolean> visibleFor) {
            this.visibleFor = visibleFor;
            return this;
        }

        public boolean showActionItemFor(IModel<T> rowModel) {
            return visibleFor.apply(rowModel);
        }

        public ActionConfig<T> configure(IConsumer<ActionConfig<T>> configurer) {
            if (configurer != null)
                configurer.accept(this);
            return this;
        }
    }

}
