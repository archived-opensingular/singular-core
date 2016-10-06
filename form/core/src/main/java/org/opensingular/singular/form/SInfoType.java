/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form;

import java.lang.annotation.*;

/**
 * Adiciona informações do tipo quando o mesmo é criado mediante um classe
 * deverivada de {@link SType}.
 *
 * @author Daniel C. Bordin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface SInfoType {

    /**
     * Permite informar o nome simples do tipo. Senão for informado, será
     * utilizado o nome simples da classe que define o tipo (ver
     * {@link java.lang.Class.getSimpleName()}).
     */
    public String name() default "";

    /**
     * Definie a classe que monta o pacote ao qual o tipo sendo definido está
     * associado.
     */
    public Class<? extends SPackage> spackage();
}
