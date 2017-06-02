package org.opensingular.form.decorator.action;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.ISupplier;

public class SInstanceAction implements Serializable {

    public enum ActionType {
        PRIMARY, NORMAL, LINK, WARNING;
    }

    public interface ActionHandler extends Serializable {
        void onAction(ISupplier<SInstance> instance, SInstanceAction.Delegate delegate);
    }

    private ActionType    type;
    private SIcon         icon;
    private String        text;
    private String        description;
    private ActionHandler actionHandler;
    private boolean       secondary = false;
    private int           position  = 0;

    public SInstanceAction(ActionType type) {
        this.setType(type);
    }

    public static SInstanceAction defaultCancelAction(String text) {
        return new SInstanceAction(ActionType.NORMAL)
            .setText(text)
            .setActionHandler((i, d) -> d.closeForm(i.get()));
    }

    //@formatter:off
    public ActionType      getType()          { return type         ; }
    public String          getText()          { return text         ; }
    public SIcon           getIcon()          { return icon         ; }
    public String          getDescription()   { return description  ; }
    public ActionHandler   getActionHandler() { return actionHandler; }
    public boolean         isSecondary()      { return secondary    ; }
    public int             getPosition()      { return position     ; }
    public SInstanceAction setType         (ActionType       type) { this.type          = type       ; return this; }
    public SInstanceAction setText         (String           text) { this.text          = text       ; return this; }
    public SInstanceAction setIcon         (SIcon            icon) { this.icon          = icon       ; return this; }
    public SInstanceAction setDescription  (String    description) { this.description   = description; return this; }
    public SInstanceAction setActionHandler(ActionHandler handler) { this.actionHandler = handler    ; return this; }
    public SInstanceAction setSecondary    (boolean     secondary) { this.secondary     = secondary  ; return this; }
    public SInstanceAction setPosition     (int          position) { this.position      = position   ; return this; }
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
