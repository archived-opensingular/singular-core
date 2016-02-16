package br.net.mirante.singular.form.mform;


import br.net.mirante.singular.commons.base.SingularException;

public class SingularFormException extends SingularException {

    public SingularFormException() {
    }

    public SingularFormException(String msg) {
        super(msg);
    }

    /**
     * Cria o erro incluindo informações de contexto da instancia sobre a qual
     * ocorreu o erro.
     */
    public SingularFormException(String msg, SInstance instance) {
        super(createErroMsg(msg, instance, null));
    }

    /**
     * Cria o erro incluindo informações de contexto da instancia sobre a qual
     * ocorreu o erro e permite acrescentar informação adicional.
     */
    public SingularFormException(String msg, SInstance instance, String additionalMsgContext) {
        super(createErroMsg(msg, instance, additionalMsgContext));
    }

    public SingularFormException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Cria o erro incluindo informações de contexto da instancia sobre a qual
     * ocorreu o erro.
     */
    public SingularFormException(String msg, Throwable cause, SInstance instance) {
        super(createErroMsg(msg, instance, null), cause);
    }

    private static String createErroMsg(String msg, SInstance instance, String complemento) {
        if (instance != null) {
            try {
                StringBuilder sb = new StringBuilder(msg);
                sb.append(" (instancia=").append(instance.getPathFull());
                sb.append(", classeInstancia=").append(instance.getClass());
                sb.append(", tipo=").append(instance.getMTipo().getName());
                sb.append(", classeTipo=").append(instance.getMTipo().getClass().getName());
                if (complemento != null) {
                    sb.append(", ").append(complemento);
                }
                sb.append(")");
                return sb.toString();
            } catch (Exception e) {
            }
        }
        return msg;
    }
}
