/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.commons.base;

import org.opensingular.lib.commons.util.Loggable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The base class of all runtime exceptions for Singular.
 */
public class SingularException extends RuntimeException implements Loggable {


    private List<InfoEntry> entries;

    /**
     * Constructs a new <code>SingularException</code> without specified
     * detail message.
     */
    protected SingularException() {
        super();
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * detail message.
     *
     * @param msg the error message
     */
    public SingularException(@Nullable String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>SingularException</code> with specified
     * cause <code>Throwable</code>.
     *
     * @param cause the exception or error that caused this exception to be
     *              thrown
     */
    protected SingularException(@Nullable Throwable cause) {
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
    protected SingularException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

    @Nonnull
    public static SingularException rethrow(@Nullable Throwable e) {
        return rethrow(null, e);
    }

    @Nonnull
    public static SingularException rethrow(@Nullable String message) {
        return rethrow(message, null);
    }

    @Nonnull
    public static SingularException rethrow(@Nullable String message, @Nullable Throwable e) {
        if (e instanceof SingularException) {
            return (SingularException) e;
        } else {
            return new SingularException(message, e);
        }
    }

    /** Verifica se já foi adicinada uma informação de detalhe com o label informado. */
    public boolean containsEntry(@Nonnull String label) {
        return entries != null && entries.stream().anyMatch(e -> Objects.equals(label, e.label));
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     *
     * @param value Valor da informação (pode ser null)
     */
    @Nonnull
    public SingularException add(@Nullable Object value) {
        return add(0, null, value);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     *
     * @param label Label da informação (pode ser null)
     * @param value Valor da informação (pode ser null)
     */
    @Nonnull
    public SingularException add(@Nullable String label, @Nullable Object value) {
        return add(0, label, value);
    }

    /**
     * Adiciona uma nova linha de informação extra na exception a ser exibida junto com a mensagem a partir doSupplier,
     * mas protegendo a geração caso o Supplier provoque uma Exception.
     */
    @Nonnull
    public SingularException add(@Nullable String label, @Nullable Supplier<?> valueSupplier) {
        Object value;
        try {
            value = valueSupplier == null ? null : valueSupplier.get();
        } catch (Exception e) {
            //Ignora a exception para não bloquear a geração da Exception atual
            getLogger().debug(null, e);
            return this;
        }
        return add(0, label, value);
    }

    /**
     * Adiciona um nova linha de informação extra na exception a ser exibida junto com a mensagem da mesma.
     *
     * @param level Nível de indentação da informação
     * @param label Label da informação (pode ser null)
     * @param value Valor da informação (pode ser null)
     */
    @Nonnull
    public SingularException add(int level, @Nullable String label, @Nullable Object value) {
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
    @Nullable
    public String getMessage() {
        if (entries == null) {
            return super.getMessage();
        }
        StringBuilder msg = new StringBuilder();
        msg.append(super.getMessage());
        int max = 0;
        for (InfoEntry entry : entries) {
            if (entry != null && entry.label != null) {
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

        public final int level;
        public final String label;
        public final String value;

        InfoEntry(int level, @Nullable String label, @Nullable String value) {
            this.level = level;
            this.label = label;
            this.value = value;
        }
    }
}
