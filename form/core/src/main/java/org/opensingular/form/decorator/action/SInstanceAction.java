package org.opensingular.form.decorator.action;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.ISupplier;

public class SInstanceAction implements Serializable {

    public enum ActionType {
        PRIMARY, NORMAL, CANCEL, WARNING;
    }

    public interface ActionHandler extends Serializable {
        void onAction(ISupplier<SInstance> instance, SInstanceAction.Delegate delegate);
    }

    private ActionType    type;
    private SIcon         icon;
    private String        text;
    private String        description;
    private ActionHandler actionHandler;

    public SInstanceAction(ActionType type) {
        this.setType(type);
    }

    public SInstanceAction(ActionType type, SIcon icon, String text, String description, ActionHandler handler) {
        this.setType(type);
        this.setIcon(icon);
        this.setText(text);
        this.setDescription(description);
        this.setActionHandler(handler);
    }

    public SInstanceAction(ActionType type, SIcon icon, String text, ActionHandler handler) {
        this.setType(type);
        this.setIcon(icon);
        this.setText(text);
        this.setActionHandler(handler);
    }
    public SInstanceAction(ActionType type, SIcon icon, ActionHandler handler) {
        this.setType(type);
        this.setIcon(icon);
        this.setActionHandler(handler);
    }
    public SInstanceAction(ActionType type, String text, ActionHandler handler) {
        this.setType(type);
        this.setText(text);
        this.setActionHandler(handler);
    }
    public SInstanceAction(ActionType type, SIcon icon, String text) {
        this.setType(type);
        this.setIcon(icon);
        this.setText(text);
    }
    public SInstanceAction(ActionType type, SIcon icon) {
        this.setType(type);
        this.setIcon(icon);
    }
    public SInstanceAction(ActionType type, String text) {
        this.setType(type);
        this.setText(text);
    }

    public static SInstanceAction defaultCancelAction(String text) {
        return new SInstanceAction(ActionType.CANCEL, text)
            .setActionHandler((i, d) -> d.closeForm(i.get()));
    }

    //@formatter:off
    public ActionType      getType()        { return type         ; }
    public String          getText()        { return text         ; }
    public SIcon           getIcon()        { return icon         ; }
    public String          getDescription() { return description  ; }
    public ActionHandler getActionHandler() { return actionHandler; }
    public SInstanceAction setType         (ActionType       type) { this.type          = type       ; return this; }
    public SInstanceAction setText         (String           text) { this.text          = text       ; return this; }
    public SInstanceAction setIcon         (SIcon            icon) { this.icon          = icon       ; return this; }
    public SInstanceAction setDescription  (String    description) { this.description   = description; return this; }
    public SInstanceAction setActionHandler(ActionHandler handler) { this.actionHandler = handler    ; return this; }
    //@formatter:on

    public interface Delegate {

        Supplier<SInstance> getInstanceRef();

        default void showMessage(String title, Serializable msg) {
            showMessage(title, msg, null);
        }
        void showMessage(String title, Serializable msg, String forcedFormat);

        void openForm(String title, ISupplier<SInstance> instanceSupplier, List<SInstanceAction> actions);
        void closeForm(SInstance instance);

        <T> Optional<T> getInternalContext(Class<T> clazz);
    }
}
