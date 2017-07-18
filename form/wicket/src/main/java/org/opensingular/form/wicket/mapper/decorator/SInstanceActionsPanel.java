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
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.jquery.JQuery;

/**
 * Componente que encapsula o layout de ícones para ações sobre campos.
 */
public class SInstanceActionsPanel extends TemplatePanel {

    public enum Mode {
        BAR,
        MENU,
        ;
        boolean isMenu() {
            return this == MENU;
        }
    }

    private static String template(SInstanceActionsPanel c) {

        switch (c.mode) {
            case MENU:
                return ""
                    + "\n<div class='md-skip btn-group" + (c.large ? " btn-group-lg actions-lg" : "") + "'>"
                    + "\n  <button type='button' class='md-skip btn btn-link dropdown-toggle' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
                    + "\n    <i class='fa fa-ellipsis-h'></i>"
                    + "\n  </button>"
                    + "\n  <ul class='dropdown-menu dropdown-menu-right pull-right'>"
                    + "\n    <li class='text-right' wicket:id='actions'>"
                    + "\n      <a wicket:id='link' style='text-align:right'><span wicket:id='label'></span> <i wicket:id='icon'></i></a></li>"
                    + "\n  </ul>"
                    + "\n</div>";
            case BAR:
            default:
                return ""
                    + "\n<div class='md-skip btn-group" + (c.large ? " btn-group-lg actions-lg" : "") + "' style='margin-left:-1px !important;'>"
                    + "\n  <div wicket:id='actions'>"
                    + "\n    <a wicket:id='link'"
                    + "\n        class='md-skip btn btn-link' style='padding:0px;'"
                    + "\n        data-toggle='tooltip' data-placement='top' data-animation='false' data-trigger='hover'>"
                    + "\n      <span wicket:id='label'></span> <i wicket:id='icon'></i>"
                    + "\n    </a>"
                    + "\n  </div>"
                    + "\n</div>";
        }
    }

    private final Mode                                       mode;
    private final ISupplier<? extends List<SInstanceAction>> actionsSupplier;
    private boolean                                          large = false;

    public SInstanceActionsPanel(
        String id,
        IModel<? extends SInstance> instanceModel,
        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider,
        Mode mode,
        ISupplier<? extends List<SInstanceAction>> actionsSupplier) {
        super(id, instanceModel, c -> template((SInstanceActionsPanel) c));
        this.mode = mode;
        this.actionsSupplier = actionsSupplier;

        add($b.classAppender("decorator-actions"));

        if (!mode.isMenu())
            add($b.onReadyScript(c -> JQuery.$(c) + ".find('[data-toggle=\"tooltip\"]').tooltip();"));

        add(new ActionsView("actions", mode, instanceModel, internalContextListProvider, actionsSupplier));
    }

    public SInstanceActionsPanel setLarge(boolean large) {
        this.large = large;
        return this;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(!actionsSupplier.get().isEmpty());
    }

    public static <C extends BSContainer<C>> C addFilteredPanelsTo(
        C container,
        SInstanceActionsProviders instanceActionsProviders,
        IModel<? extends SInstance> model,
        boolean large,
        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider) {

        ISupplier<? extends List<SInstanceAction>> filterLeft = () -> instanceActionsProviders.actionList(model, it -> !it.isSecondary() && it.getPosition() < 0);
        ISupplier<? extends List<SInstanceAction>> filterRight = () -> instanceActionsProviders.actionList(model, it -> !it.isSecondary() && it.getPosition() >= 0);
        ISupplier<? extends List<SInstanceAction>> filterSecondary = () -> instanceActionsProviders.actionList(model, it -> it.isSecondary());
        container
            .appendTag("div", new SInstanceActionsPanel("actionsLeft", model, internalContextListProvider, SInstanceActionsPanel.Mode.BAR, filterLeft)
                .setLarge(large)
                .add($b.classAppender("align-left")))
            .appendTag("div", new SInstanceActionsPanel("actionsSecondary", model, internalContextListProvider, SInstanceActionsPanel.Mode.MENU, filterSecondary)
                .setLarge(large)
                .add($b.classAppender("align-right")))
            .appendTag("div", new SInstanceActionsPanel("actionsRight", model, internalContextListProvider, SInstanceActionsPanel.Mode.BAR, filterRight)
                .setLarge(large)
                .add($b.classAppender("align-right")));
        return container;
    }

    private static final class ActionsView extends RefreshingView<SInstanceAction> {
        private final Mode                                       mode;
        private final IFunction<AjaxRequestTarget, List<?>>      internalContextListProvider;
        private final IModel<? extends SInstance>                instanceModel;
        private final ISupplier<? extends List<SInstanceAction>> actionsSupplier;
        private ActionsView(String id,
            Mode mode,
            IModel<? extends SInstance> instanceModel,
            IFunction<AjaxRequestTarget, List<?>> internalContextListProvider,
            ISupplier<? extends List<SInstanceAction>> actionsSupplier) {
            super(id);
            this.mode = mode;
            this.instanceModel = instanceModel;
            this.internalContextListProvider = internalContextListProvider;
            this.actionsSupplier = actionsSupplier;
        }
        @Override
        protected void populateItem(Item<SInstanceAction> item) {
            IModel<SInstanceAction> itemModel = item.getModel();
            item.setRenderBodyOnly(!mode.isMenu());
            SInstanceAction action = itemModel.getObject();
            ActionAjaxLink<SInstanceAction> link = new ActionAjaxLink<SInstanceAction>("link", itemModel) {
                @Override
                protected void onAction(AjaxRequestTarget target) {
                    final List<?> contextList = internalContextListProvider.apply(target);

                    final SInstanceAction.Delegate delegate = new WicketSIconActionDelegate(instanceModel, contextList);

                    SInstanceAction instanceAction = this.getModelObject();
                    instanceAction.getActionHandler()
                        .onAction(instanceAction, instanceModel::getObject, delegate);
                }
            };

            Label label = new Label("label", $m.get(() -> action.getText()));
            link.add(label
                .add($b.visibleIf(() -> mode.isMenu())));

            Label iconTag = new Label("icon");
            link
                .add(iconTag
                    .add($b.classAppender($m.map(itemModel, it -> it.getIcon().getIconCssClassesString())))
                    .add($b.styleAppender($m.map(itemModel, it -> it.getIcon().getIconCssStyles()))))
                .add($b.classAppender($m.map(itemModel, it -> it.getIcon().getContainerCssClassesString())))
                .add($b.styleAppender($m.map(itemModel, it -> it.getIcon().getContainerCssStyles())))
                .add($b.visibleIf($m.map(itemModel, it -> it.getIcon() != null)));

            link
                .add($b.attr("title", action.getText()))
                .add($b.attr("data-toggle", "tooltip"))
                .add($b.attr("title", action.getDescription(), $m.map(itemModel, it -> isNotBlank(it.getText()))));

            item.add(link);
        }
        @Override
        protected Iterator<IModel<SInstanceAction>> getItemModels() {
            return actionsSupplier.get().stream()
                .sorted(Comparator.comparing(it -> it.getPosition()))
                .map(it -> (IModel<SInstanceAction>) Model.of(it))
                .iterator();
        }
    }
}
