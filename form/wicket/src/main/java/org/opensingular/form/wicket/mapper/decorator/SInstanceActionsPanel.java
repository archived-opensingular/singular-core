package org.opensingular.form.wicket.mapper.decorator;

import static org.apache.commons.lang3.StringUtils.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.jquery.JQuery;

/**
 * Componente que encapsula o layout de ícones para ações sobre campos.
 */
public class SInstanceActionsPanel extends TemplatePanel {

    private static String template(TemplatePanel c) {
        //SInstanceActionsPanel p = (SInstanceActionsPanel) c;
        //List<SInstanceAction> actions = p.actionsSupplier.get();

        return ""
            + "\n<div class='btn-group btn-group-md' style='margin-left:-1px !important;'>"
            + "\n  <div wicket:id='actions'>"
            + "\n  <a wicket:id='button'"
            + "\n      class='btn btn-link btn-md md-skip' style='padding:0px;'"
            + "\n      data-toggle='tooltip' data-placement='top' data-animation='false' data-trigger='hover'>"
            + "\n    <i wicket:id='icon'></i>"
            + "\n  </a>"
            + "\n  </div>"
            + "\n</div>";
    }

    public SInstanceActionsPanel(
        String id,
        IModel<? extends SInstance> instanceModel,
        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider,
        ISupplier<? extends List<SInstanceAction>> actionsSupplier) {
        super(id, instanceModel, SInstanceActionsPanel::template);

        add($b.classAppender("decorator-actions"));
        add($b.onReadyScript(c -> JQuery.$(c) + ".find('[data-toggle=\"tooltip\"]').tooltip();"));

        add(new RefreshingView<SInstanceAction>("actions") {
            @Override
            protected void populateItem(Item<SInstanceAction> item) {
                IModel<SInstanceAction> itemModel = item.getModel();
                item.setRenderBodyOnly(true);

                SInstanceAction action = itemModel.getObject();
                ActionAjaxLink<SInstanceAction> button = new ActionAjaxLink<SInstanceAction>("button", itemModel) {
                    @Override
                    protected void onAction(AjaxRequestTarget target) {
                        final List<?> contextList = internalContextListProvider.apply(target);

                        final SInstanceAction.Delegate delegate = new WicketSIconActionDelegate(instanceModel::getObject, contextList);

                        SInstanceAction instanceAction = this.getModelObject();
                        instanceAction.getActionHandler()
                            .onAction(instanceModel::getObject, delegate);
                    }
                };

                Label iconContainer = new Label("icon");
                button.add(iconContainer
                    .add($b.classAppender($m.map(itemModel, it -> it.getIcon().getCssClass())))
                    .add($b.visibleIf($m.map(itemModel, it -> it.getIcon() != null))));

                button
                    .add($b.attr("title", action.getText()))
                    .add($b.attr("data-toggle", "tooltip"))
                    .add($b.attr("title", action.getDescription(), $m.map(itemModel, it -> isNotBlank(it.getText()))));

                item.add(button);
            }
            @Override
            protected Iterator<IModel<SInstanceAction>> getItemModels() {
                return actionsSupplier.get().stream()
                    .sorted(Comparator.comparing(it -> it.getPosition()))
                    .map(it -> (IModel<SInstanceAction>) Model.of(it))
                    .iterator();
            }
        });
    }
}
