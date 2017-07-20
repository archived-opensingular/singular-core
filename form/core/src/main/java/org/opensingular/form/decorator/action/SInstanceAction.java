package org.opensingular.form.decorator.action;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.ref.Out;

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
    public static interface ActionHandler extends Serializable {
        void onAction(SInstanceAction action, ISupplier<SInstance> instance, SInstanceAction.Delegate delegate);
    }

    public static interface ActionsFactory extends Serializable {
        List<SInstanceAction> getActions(FormDelegate formDelegate);
    }

    public interface FormDelegate extends Serializable {
        void close();
        SInstance getFormInstance();
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
        void openForm(Out<FormDelegate> formDelegate, String title, Serializable text, ISupplier<SInstance> instanceSupplier, ActionsFactory actionsFactory);
        /** Atualiza o campo correspondente à instância */
        void refreshFieldForInstance(SInstance instanceSupplier);
        /** Exibe uma mensagem, no formato especificado (html, markdown, text). */
        default void showMessage(String title, Serializable msg, String forcedFormat, ActionsFactory actionsFactory) {
            Out<FormDelegate> formDelegate = new Out<>();
            openForm(formDelegate,
                title,
                msg,
                () -> null,
                fd -> Optional.ofNullable(actionsFactory)
                    .map(it -> it.getActions(fd))
                    .orElseGet(() -> Arrays.asList(new SInstanceAction(ActionType.LINK)
                        .setText("Fechar")
                        .setActionHandler((a, i, d) -> fd.close()))));
        }
        /** Exibe uma mensagem. O formato usado é resolvido automaticamente (best effort). */
        default void showMessage(String title, Serializable msg) {
            showMessage(title, msg, null, null);
        }
    }

    public static class Preview implements Serializable {
        private String                title;
        private String                message;
        private String                format;
        private List<SInstanceAction> actions;

        //@formatter:off
        public String                getTitle()   { return title;   }
        public String                getMessage() { return message; }
        public String                getFormat()  { return format;  }
        public List<SInstanceAction> getActions() { return actions; }
        public Preview setTitle  (String                  title) { this.title   = title  ; return this; }
        public Preview setMessage(String                message) { this.message = message; return this; }
        public Preview setFormat (String                 format) { this.format  = format ; return this; }
        public Preview setActions(List<SInstanceAction> actions) { this.actions = actions; return this; }
        //@formatter:on
    }

    private ActionType    type;
    private SIcon         icon;
    private String        text;
    private String        description;
    private int           position  = 0;
    private boolean       secondary = false;
    private ActionHandler actionHandler;
    private Preview       preview;

    /**
     * Construtor.
     */
    public SInstanceAction(ActionType type) {
        this.setType(type);
    }

    public void onAction(SInstanceAction action, ISupplier<SInstance> instance, SInstanceAction.Delegate delegate) {
        ActionHandler handler = getActionHandler();
        if (handler != null)
            handler.onAction(action, instance, delegate);
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
    /** Flag para agrupamento de ações */
    public boolean         isSecondary()      { return secondary    ; }
    /** Callback de execução da ação */
    public ActionHandler   getActionHandler() { return actionHandler; }
    /** Callback de geração de preview */
    public Preview         getPreview()       { return preview      ; }
    
    public SInstanceAction setType         (ActionType       type) { this.type          = type       ; return this; }
    public SInstanceAction setText         (String           text) { this.text          = text       ; return this; }
    public SInstanceAction setIcon         (SIcon            icon) { this.icon          = icon       ; return this; }
    public SInstanceAction setDescription  (String    description) { this.description   = description; return this; }
    public SInstanceAction setPosition     (int          position) { this.position      = position   ; return this; }
    public SInstanceAction setSecondary    (boolean     secondary) { this.secondary     = secondary  ; return this; }
    public SInstanceAction setActionHandler(ActionHandler handler) { this.actionHandler = handler    ; return this; }
    public SInstanceAction setPreview      (Preview       preview) { this.preview       = preview    ; return this; }
    //@formatter:on
}
