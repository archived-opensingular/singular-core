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

public abstract class SInstanceActionsPanel extends TemplatePanel {

    private static String template(TemplatePanel c) {
        SInstanceActionsPanel p = (SInstanceActionsPanel) c;
        List<SInstanceAction> actions = p.actionsSupplier.get();

        return ""
            + "<div class='btn-group btn-group-md'>"
            + "  <div wicket:id='actions'>"
            + "  <button type='button' class='btn btn-link btn-md md-skip' wicket:id='link' style='padding:0;'>"
            + "    <i wicket:id='icon'></i>"
            + "    <span wicket:id='text'></span>"
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

        add(new RefreshingView<SInstanceAction>("actions") {
            @Override
            protected void populateItem(Item<SInstanceAction> item) {
                IModel<SInstanceAction> itemModel = item.getModel();
                item.setRenderBodyOnly(true);

                SInstanceAction action = itemModel.getObject();
                ActionAjaxLink<SInstanceAction> link = new ActionAjaxLink<SInstanceAction>("link", itemModel) {
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
                link.add(iconContainer
                    .add($b.classAppender($m.map(itemModel, it -> it.getIcon().getCssClass())))
                    //.add($b.classAppender("btn-icon-only", $m.get(() -> isBlank(action.getText()))))
                    .add($b.visibleIf($m.map(itemModel, it -> it.getIcon() != null))));

                link.add(new Label("text", $m.map(itemModel, it -> it.getText()))
                    .add($b.visibleIf($m.map(itemModel, it -> isNotBlank(it.getText())))));

                link.add($b.attr("title", action.getDescription(), $m.map(itemModel, it -> isNotBlank(it.getDescription()))));

                //                link.add(new AjaxEventBehavior("click") {
                //                    @Override
                //                    protected void onEvent(AjaxRequestTarget target) {
                //                        List<?> contextList = createInternalContextList(target);
                //
                //                        SInstanceAction.Delegate delegate = new AbstractSIconActionDelegate(instanceModel::getObject, contextList);
                //                        itemModel.getObject().getActionHandler().onAction(instanceModel::getObject, delegate);
                //                    }
                //                });

                item.add(link
                    .add($b.classAppender("")));
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
