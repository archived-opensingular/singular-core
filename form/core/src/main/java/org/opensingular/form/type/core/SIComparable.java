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

package org.opensingular.form.type.core;

public interface SIComparable<TIPO_NATIVO extends Comparable<TIPO_NATIVO>> {

    public TIPO_NATIVO getValue();

    public default int compareTo(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro.getValue());
    }

    public default int compareTo(TIPO_NATIVO outro) {
        return getValue().compareTo(outro);
    }

    public default boolean isAfter(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro) > 0;
    }

    public default boolean isBefore(SIComparable<TIPO_NATIVO> outro) {
        return compareTo(outro) < 0;
    }
}
