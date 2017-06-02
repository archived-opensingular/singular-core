package org.opensingular.form.wicket.mapper.decorator;

import static org.apache.commons.lang3.StringUtils.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

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
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.jquery.JQuery;

public abstract class SInstanceActionsPanel extends TemplatePanel {

    private static String template(TemplatePanel c) {
        SInstanceActionsPanel p = (SInstanceActionsPanel) c;
        List<SInstanceAction> actions = p.actionsSupplier.get();

        return ""
            + "<div class='btn-group btn-group-md'>"
            + "  <div wicket:id='actions'>"
            + "  <button wicket:id='button' type='button'"
            + "      class='btn btn-link btn-md md-skip' style='padding:0px;'"
            + "      data-toggle='tooltip' data-placement='top'>"
            + "    <i wicket:id='icon'></i>"
            + "  </button>"
            + "  </div>"
            + "</div>";
    }

    private final ISupplier<? extends List<SInstanceAction>> actionsSupplier;

    public SInstanceActionsPanel(
        String id,
        IModel<? extends SInstance> instanceModel,
        ISupplier<? extends List<SInstanceAction>> actionsSupplier) {
        super(id, instanceModel, SInstanceActionsPanel::template);
        this.actionsSupplier = actionsSupplier;

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
                        final List<?> contextList = createInternalContextList(target);

                        final SInstanceAction.Delegate delegate =
                            new AbstractSIconActionDelegate(instanceModel::getObject, contextList);

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
                    .map(it -> (IModel<SInstanceAction>) Model.of(it))
                    .iterator();
            }
        });
    }

    protected abstract List<?> createInternalContextList(AjaxRequestTarget target);
    //
    //    public static void buildActions(
    //        final WicketBuildContext ctx,
    //        final IModel<? extends SInstance> model,
    //        final ISInstanceActionCapable actionCapable,
    //        final BSContainer<?> actionsContainer,
    //        final Iterator<SInstanceAction> actionsIterator) {
    //
    //        while (actionsIterator.hasNext()) {
    //            SInstanceAction action = actionsIterator.next();
    //            SIcon icon = action.getIcon();
    //            String text = action.getText();
    //            String desc = action.getDescription();
    //
    //            BSContainer<?> link = new BSContainer<>("btn");
    //            if (icon != null)
    //                link.appendTag("i", new WebMarkupContainer("icon").add($b.classAppender(icon.getCssClass())))
    //                    .add($b.classAppender("btn-icon-only", $m.get(() -> isBlank(text))));
    //
    //            if (isNotBlank(text))
    //                link.appendTag("span", new Label("text", text));
    //
    //            if (isNotBlank(desc))
    //                link.add($b.attr("title", desc));
    //
    //            link.add(new AjaxEventBehavior("click") {
    //                @Override
    //                protected void onEvent(AjaxRequestTarget target) {
    //                    List<?> contextList = Arrays.asList(
    //                        actionCapable,
    //                        target,
    //                        model,
    //                        model.getObject(),
    //                        ctx,
    //                        ctx.getContainer());
    //
    //                    SInstanceAction.Delegate delegate = new AbstractSIconActionDelegate(model::getObject, contextList);
    //                    action.getActionHandler().onAction(model::getObject, delegate);
    //                }
    //            });
    //
    //            actionsContainer.appendTag("a", true, "class='btn btn-circle btn-default'", link);
    //        }
    //    }
}
