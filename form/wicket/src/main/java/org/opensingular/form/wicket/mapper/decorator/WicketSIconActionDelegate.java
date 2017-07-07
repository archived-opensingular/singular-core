package org.opensingular.form.wicket.mapper.decorator;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
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
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;

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
        SInstanceActionOpenModalEvent evt = new SInstanceActionOpenModalEvent(
            title,
            getInternalContext(AjaxRequestTarget.class).orElse(null),
            instanceRef,
            new SuppliedInstanceModel(formInstance),
            actions);
        getInternalContext(Component.class)
            .ifPresent(comp -> comp.send(comp, Broadcast.BUBBLE, evt));
    }

    @Override
    public void closeForm(SInstance formInstance) {
        ICloseModalEvent evt = ICloseModalEvent.of(
            getInternalContext(AjaxRequestTarget.class).orElse(null),
            it -> Objects.equals(it.getDefaultModelObject(), formInstance));
        getInternalContext(Component.class)
            .ifPresent(comp -> comp.send(comp, Broadcast.BUBBLE, evt));
    }

    @Override
    public void refreshFieldForInstance(SInstance instance) {
        Optional<AjaxRequestTarget> target = getInternalContext(AjaxRequestTarget.class);
        Optional<Component> comp = getInternalContext(Component.class);
        if (target.isPresent() && comp.isPresent()) {
            Optional<MarkupContainer> fieldContainer = WicketFormUtils.findChildByInstance(comp.get().getPage(), instance)
                .flatMap(WicketFormUtils::findCellContainer);
            target.get().add(fieldContainer.get());
        }
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

    static ButtonStyle resolveButtonStyle(SInstanceAction.ActionType actionType) {
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

    /*
     * AS CLASSES ABAIXO NÃO SÃO LAMBDAS PARA MANTER O CONTROLE DAS REFERÊNCIAS,
     * POIS O DELEGATE NÃO É SERIALIZÁVEL!!!
     */

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

    static final class ModelGetterSupplier<T> implements ISupplier<T> {
        private IModel<? extends T> model;
        public ModelGetterSupplier(IModel<? extends T> model) {
            this.model = model;
        }
        @Override
        public T get() {
            return model.getObject();
        }
    }

}
