package org.opensingular.form.wicket.mapper.decorator;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.form.wicket.panel.ICloseModalEvent;
import org.opensingular.form.wicket.panel.IOpenModalEvent;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;

import com.google.common.collect.ImmutableList;

/**
 * Implementação de <code>SInstanceAction.Delegate</code> integrada com a infraestrutura Wicket.
 */
public class WicketSIconActionDelegate implements SInstanceAction.Delegate {

    private ISupplier<SInstance> instanceRef;
    private List<?>              contextList;

    public WicketSIconActionDelegate(ISupplier<SInstance> instanceRef, List<?> contextList) {
        this.instanceRef = instanceRef;
        this.contextList = contextList;
    }

    /*
     * 
     */
    @Override
    public ISupplier<SInstance> getInstanceRef() {
        return instanceRef;
    }

    @Override
    public void openForm(String title, ISupplier<SInstance> formInstance, List<SInstanceAction> actions) {
        IModel<SInstance> formInstanceModel = new SuppliedInstanceModel(formInstance);
        getInternalContext(Component.class).ifPresent(comp -> {
            comp.send(comp, Broadcast.BUBBLE, new OpenModelEventImpl(
                title,
                getInternalContext(AjaxRequestTarget.class).get(),
                instanceRef,
                formInstanceModel,
                actions));
        });
    }

    @Override
    public void closeForm(SInstance formInstance) {
        getInternalContext(Component.class).ifPresent(comp -> {
            comp.send(comp, Broadcast.BUBBLE, ICloseModalEvent.of(
                getInternalContext(AjaxRequestTarget.class).orElse(null),
                it -> Objects.equals(it.getDefaultModelObject(), formInstance)));
        });
    }

    @Override
    public void showMessage(String title, Serializable message, String forcedFormat) {
        Serializable serializableMessage = ObjectUtils.defaultIfNull(message, "");
        switch (defaultIfBlank(forcedFormat, "").toLowerCase()) {

            case "markdown":
            case "commonmark":
                Parser parser = Parser.builder().build();
                Node node = parser.parse(serializableMessage.toString());
                String html = HtmlRenderer.builder().build().render(node);
                showMessage(title, html, "html");
                return;

            case "text":
            case "plaintext":
            case "plain text":
                showMessage(title, "<p>" + serializableMessage.toString() + "</p>", "html");
                return;

            case "html":
                getInternalContext(Component.class).ifPresent(comp -> {
                    comp.send(comp, Broadcast.BUBBLE, new IOpenModalEvent() {
                        @Override
                        public String getModalTitle() {
                            return title;
                        }
                        @Override
                        public AjaxRequestTarget getTarget() {
                            return getInternalContext(AjaxRequestTarget.class).orElse(null);
                        }
                        @Override
                        public Component getBodyContent(String id) {
                            return new TemplatePanel(id, serializableMessage.toString());
                        }
                        @Override
                        public Iterator<ButtonDef> getFooterButtons(IConsumer<AjaxRequestTarget> closeCallback) {
                            return Arrays.asList(new ButtonDef(
                                ButtonStyle.LINK,
                                Model.of("Fechar"),
                                new FecharButton("fechar", closeCallback)))
                                .iterator();
                        }
                    });
                });
                return;

            default:
                showMessage(title, serializableMessage, resolveMessageFormat(serializableMessage));
        }
    }

    private static ButtonStyle resolveButtonStyle(SInstanceAction.ActionType actionType) {
        switch (actionType) {
            case PRIMARY:
                return ButtonStyle.PRIMARY;
            case LINK:
                return ButtonStyle.LINK;
            case WARNING:
                return ButtonStyle.DANGER;
            case NORMAL:
            default:
                return ButtonStyle.DEFAULT;
        }
    }

    protected static String resolveMessageFormat(Serializable msg) {
        if (msg instanceof String) {
            String s = ((String) msg).trim();
            if (s.startsWith("<"))
                return "html";
            if (s.contains("**") || s.contains("##") || s.contains("]("))
                return "markdown";
        }
        return "plaintext";
    }

    protected AjaxRequestTarget getAjaxRequestTarget() {
        return getInternalContext(AjaxRequestTarget.class).orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getInternalContext(Class<T> clazz) {
        return contextList.stream()
            .filter(it -> clazz.isAssignableFrom(it.getClass()))
            .map(it -> (T) it)
            .findFirst();
    }

    private static final class FecharButton extends ActionAjaxButton {
        IConsumer<AjaxRequestTarget> closeCallback;
        private FecharButton(String id, IConsumer<AjaxRequestTarget> closeCallback) {
            super(id);
            this.closeCallback = closeCallback;
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            closeCallback.accept(target);
        }
    }

    private static final class OpenModelEventImpl implements IOpenModalEvent {
        private String                      title;
        private AjaxRequestTarget           target;
        private ISupplier<SInstance>        instanceSupplier;
        private IModel<? extends SInstance> formInstance;
        private List<SInstanceAction>       actions;

        public OpenModelEventImpl(String title, AjaxRequestTarget target, ISupplier<SInstance> instanceSupplier, IModel<? extends SInstance> formInstanceModel, List<SInstanceAction> actions) {
            this.title = title;
            this.target = target;
            this.instanceSupplier = instanceSupplier;
            this.formInstance = formInstanceModel;
            this.actions = actions;
        }
        //@formatter:off
        @Override public String            getModalTitle() { return this.title; }
        @Override public AjaxRequestTarget getTarget()     { return this.target; }
        //@formatter:on
        @Override
        public Component getBodyContent(String id) {
            return new TemplatePanel(id, "<div wicket:id='panel'></div>")
                .add(new SingularFormPanel("panel", new ModelGetterSupplier<SInstance>(formInstance)))
                .setDefaultModel(formInstance);
        }
        @Override
        public Iterator<ButtonDef> getFooterButtons(IConsumer<AjaxRequestTarget> closeCallback) {
            final List<ButtonDef> buttons = new ArrayList<IOpenModalEvent.ButtonDef>();
            for (int i = 0; i < actions.size(); i++) {
                final SInstanceAction action = actions.get(i);

                final ButtonStyle style = resolveButtonStyle(action.getType());
                final Model<String> label = Model.of(action.getText());
                final FooterButton button = new FooterButton("action" + i, action, instanceSupplier, formInstance);
                buttons.add(new ButtonDef(style, label, button));
            }
            return buttons
                .iterator();
        }
    }

    private static final class SuppliedInstanceModel extends LoadableDetachableModel<SInstance> {
        private ISupplier<SInstance> instanceSupplier;
        public SuppliedInstanceModel(ISupplier<SInstance> instanceSupplier) {
            this.instanceSupplier = instanceSupplier;
        }
        @Override
        protected SInstance load() {
            return instanceSupplier.get();
        }
    }

    private static final class ModelGetterSupplier<T> implements ISupplier<T> {
        private IModel<? extends T> model;
        public ModelGetterSupplier(IModel<? extends T> model) {
            this.model = model;
        }
        @Override
        public T get() {
            return model.getObject();
        }
    }

    private static final class FooterButton extends ActionAjaxButton {

        private final ISupplier<SInstance>        instanceSupplier;
        private final IModel<? extends SInstance> formInstanceModel;
        private final SInstanceAction             action;

        private FooterButton(String id, SInstanceAction action, ISupplier<SInstance> instanceSupplier, IModel<? extends SInstance> formInstanceModel) {
            super(id);
            this.action = action;
            this.instanceSupplier = instanceSupplier;
            this.formInstanceModel = formInstanceModel;
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            List<Object> childContextList = ImmutableList.of(
                target,
                form,
                formInstanceModel,
                formInstanceModel.getObject(),
                this);
            action.getActionHandler().onAction(
                action,
                new ModelGetterSupplier<SInstance>(formInstanceModel),
                new WicketSIconActionDelegate(
                    instanceSupplier,
                    childContextList));
        }
    }

}
