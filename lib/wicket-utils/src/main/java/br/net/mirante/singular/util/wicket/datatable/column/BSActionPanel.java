package br.net.mirante.singular.util.wicket.datatable.column;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.datatable.IBSAction;
import br.net.mirante.singular.util.wicket.lambda.IFunction;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.resource.IconeView;

public class BSActionPanel<T> extends Panel {

    public static final String LINK_ID = "link";
    public static final String ICONE_ID = "icone";

    private final RepeatingView actions = new RepeatingView("actions");

    public BSActionPanel(String id) {
        super(id);
        add(actions);
        add($b.classAppender("pull-right"));
    }

    public BSActionPanel<T> appendAction(IModel<?> labelModel, IModel<Icone> iconeModel, MarkupContainer link) {
        actions
            .add(new WebMarkupContainer(actions.newChildId())
                .add(link));

        if (link.get(ICONE_ID) == null)
            link.add(new IconeView(ICONE_ID, iconeModel));

        if (labelModel != null)
            link.add($b.attr("title", labelModel));
        return this;
    }

    public BSActionPanel<T> appendAction(IModel<?> labelModel, IModel<Icone> iconeModel, IFunction<String, MarkupContainer> linkFactory) {
        return appendAction(labelModel, iconeModel, linkFactory.apply(LINK_ID));
    }

    public BSActionPanel<T> appendAction(IModel<?> labelModel, IModel<Icone> iconeModel, IBSAction<T> action, IModel<T> model) {
        return appendAction(labelModel, iconeModel, childId -> {
            return new ActionAjaxLink<T>(childId, model) {
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
                }
            };
        });
    }

    protected void onConfigure() {
        super.onConfigure();
        for (Component comp : actions) {
            MarkupContainer container = (MarkupContainer) ((WebMarkupContainer) comp).get(LINK_ID);

            if (container.get(ICONE_ID) == null) {
                container.add(new IconeView(ICONE_ID));
            }
        }
    }
}
