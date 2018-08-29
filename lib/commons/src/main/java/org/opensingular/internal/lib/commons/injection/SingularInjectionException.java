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

import org.opensingular.lib.commons.base.SingularException;

/**
 * Indica um erro ao tentar injetar um valor em um campo de um objeto.
 *
 * @author Daniel C. Bordin on 17/05/2017.
 */
public class SingularInjectionException extends SingularException {

    public SingularInjectionException(String msg) {
        super(msg);
    }

    public SingularInjectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SingularInjectionException(FieldInjectionInfo fieldInfo, Object target, CharSequence msg, Throwable cause) {
        super(errorMsg(fieldInfo, target, msg), cause);
        if (fieldInfo.getBeanName() != null) {
            add("beanName", fieldInfo.getBeanName());
        }
        add("targetClass", fieldInfo.getField().getDeclaringClass().getName());
        add("fieldName", fieldInfo.getFieldName());
        add("fieldClass", fieldInfo.getType().getName());
    }

    private static String errorMsg(FieldInjectionInfo fieldInfo, Object target, CharSequence msg) {
        Class<?> targetClass = target == null ? fieldInfo.getField().getDeclaringClass() : target.getClass();
        StringBuilder msg2 = new StringBuilder("Erro ao tentar injetar o valor no field [").append(
                targetClass.getSimpleName()).append('.').append(fieldInfo.getFieldName()).append(']');
        if (target != null) {
            msg2.append(" do objeto [").append(target).append(']');
        }

        if (msg != null) {
            msg2.append(": ").append(msg);
        }
        return msg2.toString();
    }
}
