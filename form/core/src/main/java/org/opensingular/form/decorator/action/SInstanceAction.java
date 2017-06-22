package org.opensingular.form.decorator.action;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.ISupplier;

/**
 * Objeto que encapsula uma ação que pode ser tomada sobre um campo do form, na interface.
 */
public class SInstanceAction implements Serializable {

    /**
     * Tipo de ação. Pode alterar a forma como a ação é exibida na tela.
     */
    public enum ActionType {
        PRIMARY, NORMAL, LINK, WARNING;
    }

    /**
     * Interface callback para o tratamento de uma ação solicitada pelo usuário.
     */
    public interface ActionHandler extends Serializable {
        void onAction(SInstanceAction action, ISupplier<SInstance> instance, SInstanceAction.Delegate delegate);
    }

    /**
     * Objeto que encapsula a interação com a interface, de forma independente da tecnologia da implementação.
     */
    public interface Delegate {
        /** Retorna um objeto do contexto interno do tipo especificado, caso seja necessária alguma customização específica da tecnologia. */
        <T> Optional<T> getInternalContext(Class<T> clazz);
        /** Retorna uma referência à instância do campo correspondente à ação que está sendo executada. */
        Supplier<SInstance> getInstanceRef();
        /** Abre um form gerado a partir da instância fornecida, com as ações possíveis. */
        void openForm(String title, ISupplier<SInstance> instanceSupplier, List<SInstanceAction> actions);
        /** Fecha um form aberto por openForm(), correspondente à instância fornecida. */
        void closeForm(SInstance instance);
        /** Exibe uma mensagem, no formato especificado (html, markdown, text). */
        void showMessage(String title, Serializable msg, String forcedFormat);
        /** Exibe uma mensagem. O formato usado é resolvido automaticamente (best effort). */
        default void showMessage(String title, Serializable msg) {
            showMessage(title, msg, null);
        }
    }

    private ActionType    type;
    private SIcon         icon;
    private String        text;
    private String        description;
    private ActionHandler actionHandler;
    private boolean       secondary = false;
    private int           position  = 0;

    /**
     * Construtor.
     */
    public SInstanceAction(ActionType type) {
        this.setType(type);
    }

    //@formatter:off
    /** Tipo da mensagem para exibição */
    public ActionType      getType()          { return type         ; }
    /** Texto da label para exibição */
    public String          getText()          { return text         ; }
    /** Ícone para exibição */
    public SIcon           getIcon()          { return icon         ; }
    /** Descrição da ação para exibição */
    public String          getDescription()   { return description  ; }
    /** Posição da ação para exibição */
    public int             getPosition()      { return position     ; }
    /** Callback de execução da ação */
    public ActionHandler   getActionHandler() { return actionHandler; }
    /** Flag para agrupamento de ações */
    public boolean         isSecondary()      { return secondary    ; }
    public SInstanceAction setType         (ActionType       type) { this.type          = type       ; return this; }
    public SInstanceAction setText         (String           text) { this.text          = text       ; return this; }
    public SInstanceAction setIcon         (SIcon            icon) { this.icon          = icon       ; return this; }
    public SInstanceAction setDescription  (String    description) { this.description   = description; return this; }
    public SInstanceAction setActionHandler(ActionHandler handler) { this.actionHandler = handler    ; return this; }
    public SInstanceAction setSecondary    (boolean     secondary) { this.secondary     = secondary  ; return this; }
    public SInstanceAction setPosition     (int          position) { this.position      = position   ; return this; }
    //@formatter:on
}
