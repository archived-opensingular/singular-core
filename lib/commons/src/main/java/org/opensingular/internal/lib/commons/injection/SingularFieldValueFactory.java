/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * Factory object used by injector to generate values for fields of the object being injected.
 *
 * @author Daniel C. Bordin on 16/05/2017.
 */
public interface SingularFieldValueFactory {

    /**
     * Returns the value the field will be set to. May return null, if not found.
     *
     * @param field      field being injected
     * @param fieldOwner instance of object being injected
     */
    @Nullable
    Object getFieldValue(@Nonnull FieldInjectionInfo fieldInfo, @Nonnull Object fieldOwner);

    /**
     * Dá a chance à factory de criar um objeto espefício de metadado sobre a injeção de um campo, se a factorie tiver
     * necessidade de aproveitar o cache de informações de um campo alvo de injeção.
     */
    @Nonnull
    default FieldInjectionInfo createCachedInfo(@Nonnull Field field) {
        return new FieldInjectionInfo(field);
    }
}
