/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.commons.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The base class of all runtime exceptions for Singular.
 */
public class SingularException extends RuntimeException {


    private List<InfoEntry> entries;

    /**
     * Constructs a new <code>SingularException</code> without specified
     * detail message.
     */
    public SingularException() {
        super();
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * detail message.
     *
     * @param msg the error message
     */
    public SingularException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * cause <code>Throwable</code>.
     *
     * @param cause the exception or error that caused this exception to be
     *              thrown
     */
    public SingularException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * detail message and cause <code>Throwable</code>.
     *
     * @param msg   the error message
     * @param cause the exception or error that caused this exception to be
     *              thrown
     */
    public SingularException(String msg, Throwable cause) {
        super(msg, cause);
    }


    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     *
     * @param value Valor da informação (pode ser null)
     */
    public SingularException add(Object value) {
        return add(0, null, value);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     *
     * @param label Label da informação (pode ser null)
     * @param value Valor da informação (pode ser null)
     */
    public SingularException add(String label, Object value) {
        return add(0, label, value);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     *
     * @param level Nível de indentação da informação
     * @param label Label da informação (pode ser null)
     * @param value Valor da informação (pode ser null)
     */
    public SingularException add(int level, String label, Object value) {
        if (label != null || value != null) {
            if (entries == null) {
                entries = new ArrayList<>();
            }
            entries.add(new InfoEntry(level, label, value == null ? null : value.toString()));
        }
        return this;
    }

    /**
     * Gera a mensagem de erro da Exception adicionando as informações adicionais (se tiverem sido incluidas).
     */
    @Override
    public String getMessage() {
        if (entries == null) {
            return super.getMessage();
        }
        StringBuilder msg = new StringBuilder();
        msg.append(super.getMessage());
        int max = 0;
        for (InfoEntry entry : entries) {
            if (entry != null) {
                max = Math.max(max, entry.label.length());
            }
        }
        for (InfoEntry entry : entries) {
            msg.append('\n');
            for (int level = 0; level <= entry.level; level++) {
                msg.append("  ");
            }
            int i = 0;
            if (entry.label != null) {
                msg.append(entry.label);
                i = entry.label.length();
            }
            for (; i < max; i++) {
                msg.append(' ');
            }
            msg.append(':').append(' ');
            msg.append(entry.value);
        }
        return msg.toString();
    }

    /**
     * Representa uma informação adicional sobre a Exception.
     */
    private static final class InfoEntry implements Serializable {

        public final int    level;
        public final String label;
        public final String value;

        public InfoEntry(int level, String label, String value) {
            this.level = level;
            this.label = label;
            this.value = value;
        }
    }
}
