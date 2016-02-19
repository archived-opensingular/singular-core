package br.net.mirante.singular.util.wicket.datatable.column;

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

import br.net.mirante.singular.lambda.IBiFunction;
import br.net.mirante.singular.lambda.IFunction;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.datatable.IBSAction;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.resource.IconeView;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class BSActionPanel<T> extends Panel {

    public static final String LINK_ID = "link";
    public static final String ICONE_ID = "icone";
    public static final String LABEL_ID = "label";

    private final RepeatingView actions = new RepeatingView("actions");

    public BSActionPanel(String id) {
        super(id);
        add(actions);
    }

    public BSActionPanel<T> appendAction(IModel<?> labelModel, IModel<Icone> iconeModel, MarkupContainer link) {
        return appendAction(new ActionConfig<>().labelModel(labelModel).iconeModel(iconeModel)
                .stripeModel(null).link(link).buttonModel($m.ofValue("black")).withText(true));
    }

    public BSActionPanel<T> appendAction(ActionConfig<?> actionConfig) {

        actions.add(new WebMarkupContainer(actions.newChildId())
                .add(actionConfig.link.add($b.attrAppender("class", actionConfig.buttonModel, " "))));

        if (actionConfig.stripeModel != null) {
            actionConfig.link.add($b.attrAppender("class", actionConfig.stripeModel, " "));
        }

        if (actionConfig.link.get(ICONE_ID) == null) {
            actionConfig.link.add(new IconeView(ICONE_ID, actionConfig.iconeModel));
        }

        if (actionConfig.labelModel != null) {
            if (actionConfig.withText) {
                actionConfig.link.add(new Label(LABEL_ID, actionConfig.labelModel));
            } else {
                actionConfig.link.add($b.attr("title", actionConfig.labelModel));
                actionConfig.link.add(new WebMarkupContainer(LABEL_ID));
            }
        }
        return this;
    }

    public BSActionPanel<T> appendAction(ActionConfig<T> actionConfig, IFunction<String, MarkupContainer> linkFactory) {
        return appendAction(actionConfig.link(linkFactory.apply(LINK_ID)));
    }

    public BSActionPanel<T> appendAction(ActionConfig<T> actionConfig, IBiFunction<T, String, MarkupContainer> linkFactory, IModel<T> model) {
        return appendAction(actionConfig.link(linkFactory.apply(model.getObject(), LINK_ID)));
    }

    public BSActionPanel<T> appendAction(ActionConfig<T> config, IBSAction<T> action, IModel<T> model) {
        if (config.linkFactory == null) {
            return appendAction(config, childId -> new ActionAjaxLink<T>(childId, model) {
                @Override
                public void onAction(AjaxRequestTarget target) {
                    action.execute(target, this.getModel());
                }

                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                    super.updateAjaxAttributes(attributes);
                    action.updateAjaxAttributes(attributes);
                }

                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    this.setVisible(action.isVisible(this.getModel()));
                    this.setEnabled(action.isEnabled(this.getModel()));
                    add($b.attrAppender("style", config.style, " "));
                }
            });
        } else {
            return appendAction(config, config.linkFactory, model);
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


    public static class ActionConfig<T> implements Serializable {

        protected IModel<?> labelModel = $m.ofValue("");
        protected IModel<Icone> iconeModel;
        protected IModel<String> stripeModel;
        protected MarkupContainer link;
        protected IModel<String> buttonModel = $m.ofValue("black");
        protected IModel<String> style;
        protected boolean withText = true;
        protected IBiFunction<T, String, MarkupContainer> linkFactory;

        public ActionConfig<T> labelModel(IModel<?> labelModel) {
            this.labelModel = labelModel;
            return this;
        }

        public ActionConfig<T> iconeModel(IModel<Icone> iconeModel) {
            this.iconeModel = iconeModel;
            return this;
        }

        public ActionConfig<T> stripeModel(IModel<String> stripeModel) {
            this.stripeModel = stripeModel;
            return this;
        }

        public ActionConfig<T> link(MarkupContainer link) {
            this.link = link;
            return this;
        }

        public ActionConfig<T> buttonModel(IModel<String> buttonModel) {
            this.buttonModel = buttonModel;
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

        public ActionConfig<T> linkFactory(IBiFunction<T, String, MarkupContainer> linkFactory) {
            this.linkFactory = linkFactory;
            return this;
        }
    }
}
