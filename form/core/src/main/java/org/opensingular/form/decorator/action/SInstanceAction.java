package org.opensingular.form.decorator.action;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

import org.opensingular.form.SInstance;

public class SInstanceAction implements Serializable {

    private SIcon         icon;
    private String        text;
    private String        description;
    private ActionHandler actionHandler;

    public SInstanceAction() {}

    public SInstanceAction(SIcon icon, String text, String description, ActionHandler handler) {
        this.setIcon(icon);
        this.setText(text);
        this.setDescription(description);
        this.setActionHandler(handler);
    }

    public SInstanceAction(SIcon icon, String text, ActionHandler handler) {
        this.setIcon(icon);
        this.setText(text);
        this.setActionHandler(handler);
    }
    public SInstanceAction(SIcon icon, ActionHandler handler) {
        this.setIcon(icon);
        this.setActionHandler(handler);
    }
    public SInstanceAction(String text, ActionHandler handler) {
        this.setText(text);
        this.setActionHandler(handler);
    }
    public SInstanceAction(SIcon icon, String text) {
        this.setIcon(icon);
        this.setText(text);
    }
    public SInstanceAction(SIcon icon) {
        this.setIcon(icon);
    }
    public SInstanceAction(String text) {
        this.setText(text);
    }

    //@formatter:off
    public String          getText()        { return text         ; }
    public SIcon           getIcon()        { return icon         ; }
    public String          getDescription() { return description  ; }
    public ActionHandler getActionHandler() { return actionHandler; }
    public SInstanceAction setText         (String           text) { this.text          = text       ; return this; }
    public SInstanceAction setIcon         (SIcon            icon) { this.icon          = icon       ; return this; }
    public SInstanceAction setDescription  (String    description) { this.description   = description; return this; }
    public SInstanceAction setActionHandler(ActionHandler handler) { this.actionHandler = handler    ; return this; }
    //@formatter:on

    public interface ActionHandler extends Serializable {
        void onAction(SInstance instance, SInstanceAction.Delegate delegate);
    }

    public interface Delegate {

        Supplier<SInstance> getInstanceRef();

        default void showMessage(Serializable msg) {
            showMessage(msg, null);
        }
        void showMessage(Serializable msg, String forcedFormat);

        void openForm(SInstance instance, SInstanceAction... actions);
        void closeForm(SInstance instance);

        <T> Optional<T> getInternalContext(Class<T> clazz);
    }
}
