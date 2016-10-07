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

package org.opensingular.form;

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
