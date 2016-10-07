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

package org.opensingular.flow.core;

import java.io.Serializable;

//TODO renomear para algo mais representativo para o singular.
public interface MUser extends Comparable<MUser>, Serializable {

    Integer getCod();

    /**
     * Nome curto do usuário, ou seja, não um nome que identifica a pessoa
     * dentro da organização, mas que pode ser uma abreviação do nome completo
     * ou um nome de guerra. Por exemplo, "João Magalhão do Santo Silva" pode
     * ter seu nome simples como sendo "João Silva".
     */
    String getSimpleName();

    String getEmail();

    String getCodUsuario();

    default boolean is(MUser user2) {
        return (user2 != null) && getCod().equals(user2.getCod());
    }

    default boolean isNot(MUser user2) {
        return !(is(user2));
    }

    @Override
    default int compareTo(MUser p) {
        return getSimpleName().compareTo(p.getSimpleName());
    }
}
