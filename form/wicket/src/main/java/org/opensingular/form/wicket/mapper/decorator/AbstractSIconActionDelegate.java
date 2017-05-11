package org.opensingular.form.wicket.mapper.decorator;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.form.decorator.action.SInstanceAction.Delegate;
import org.opensingular.form.wicket.panel.IShowModalEvent;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.util.JavaScriptUtils;

public class AbstractSIconActionDelegate implements Delegate {
    private Supplier<SInstance> instanceRef;
    private Supplier<List<?>>   contextList;

    public AbstractSIconActionDelegate(Supplier<SInstance> instanceRef, Supplier<List<?>> contextList) {
        this.instanceRef = instanceRef;
        this.contextList = contextList;
    }

    @Override
    public Supplier<SInstance> getInstanceRef() {
        return instanceRef;
    }

    @Override
    public void openForm(SInstance instance, SInstanceAction... actions) {}

    @Override
    public void closeForm(SInstance instance) {}

    @Override
    public void showMessage(Serializable msg, String forcedFormat) {
        switch (defaultIfBlank(forcedFormat, "").toLowerCase()) {

            case "markdown":
            case "commonmark":
                Parser parser = Parser.builder().build();
                Node node = parser.parse(msg.toString());
                String html = HtmlRenderer.builder().build().render(node);
                showMessage(html, "html");
                return;

            case "text":
            case "plaintext":
                showMessage("<p>" + msg.toString() + "</p>", "html");
                return;

            case "html":
                Component comp = getInternalContext(Component.class).get();
                comp.send(comp, Broadcast.BUBBLE, new IShowModalEvent() {
                    @Override
                    public AjaxRequestTarget getTarget() {
                        return getInternalContext(AjaxRequestTarget.class).get();
                    }
                    @Override
                    public Component getModalContent(String id) {
                        return new TemplatePanel(id, msg.toString());
                    }
                });
                getAjaxRequestTarget().appendJavaScript(""
                    + "alert('" + JavaScriptUtils.javaScriptEscape(msg.toString()) + "');"
                    + "");
                return;

            default:
                showMessage(msg, resolveMessageFormat(msg));
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
        return getInternalContext(AjaxRequestTarget.class).get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getInternalContext(Class<T> clazz) {
        return contextList.get().stream()
            .filter(it -> clazz.isAssignableFrom(it.getClass()))
            .map(it -> (T) it)
            .findFirst();
    }
}
