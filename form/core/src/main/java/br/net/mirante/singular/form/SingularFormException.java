/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;


import br.net.mirante.singular.commons.base.SingularException;

public class SingularFormException extends SingularException {

    public SingularFormException() {
    }

    public SingularFormException(String msg) {
        super(msg);
    }

    /**
     * Cria um erro incluindo informações de contexto para o objeto informado. Se o objeto não for um SType ou
     * SInstance, então simplesmente dá um toString() no mesmo.
     */
    public SingularFormException(String msg, Object target) {
        super(createErroMsg(msg, target, null));
    }

    /**
     * Cria o erro incluindo informações de contexto da instancia sobre a qual
     * ocorreu o erro.
     */
    public SingularFormException(String msg, SInstance instance) {
        super(createErroMsg(msg, instance, null));
    }

    /**
     * Cria o erro incluindo informações de contexto do tipo sobre o qual ocorreu o erro.
     */
    public SingularFormException(String msg, SType<?> type) {
        super(createErroMsg(msg, type, null));
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

    private static String createErroMsg(String msg, Object target, String complement) {
        if (target instanceof SType) {
            return createErroMsg(msg, (SType<?>) target, complement);
        } else if (target instanceof SInstance) {
            return createErroMsg(msg, (SInstance) target, complement);
        } else if (target != null) {
            try {
                StringBuilder sb = new StringBuilder(msg).append('\n');
                sb.append(", target=").append(target);
                if (complement != null) {
                    sb.append("\n, ").append(complement);
                }
                return sb.toString();
            } catch (Exception e) {
            }
        }
        return msg;
    }

    private static String createErroMsg(String msg, SType<?> type, String complement) {
        if (type != null) {
            try {
                StringBuilder sb = new StringBuilder(msg).append('\n');
                sb.append(", tipo=").append(type);
                if (complement != null) {
                    sb.append("\n, ").append(complement);
                }
                return sb.toString();
            } catch (Exception e) {
            }
        }
        return msg;
    }

    private static String createErroMsg(String msg, SInstance instance, String complemento) {
        if (instance != null) {
            try {
                StringBuilder sb = new StringBuilder(msg);
                sb.append(" (instancia=").append(instance.getPathFull());
                sb.append(", classeInstancia=").append(instance.getClass());
                sb.append(", tipo=").append(instance.getType().getName());
                sb.append(", classeTipo=").append(instance.getType().getClass().getName());
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
