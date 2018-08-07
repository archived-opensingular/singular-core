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

package org.opensingular.internal.lib.commons.injection;

/**
 * Indica que o provedor de beans para injeção ainda não foi configurado e não é possível atender uma solicitação de
 * injeção específica.
 *
 * @author Daniel C. Bordin on 21/05/2017.
 */
public class SingularInjectionNotConfiguredException extends SingularInjectionException {

    public SingularInjectionNotConfiguredException(FieldInjectionInfo fieldInfo, Object target) {
        super(fieldInfo, target, "Foi encontrada essa solicitação injeção, mas o provedor de beans (provavelmente do " +
                "Spring) ainda não foi configurado (ver " + "ApplicationContextProvider.get())", null);

    }
}
