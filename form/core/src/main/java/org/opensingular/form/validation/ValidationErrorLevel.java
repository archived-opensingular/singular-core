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

package org.opensingular.form.validation;

public enum ValidationErrorLevel {
    WARNING, ERROR;

    //@formatter:off
    public boolean eq(ValidationErrorLevel level) { return this == level; }
    public boolean ne(ValidationErrorLevel level) { return this != level; }
    public boolean gt(ValidationErrorLevel level) { return this.compareTo(level) > 0; }
    public boolean ge(ValidationErrorLevel level) { return this.compareTo(level) >= 0; }
    public boolean lt(ValidationErrorLevel level) { return this.compareTo(level) < 0; }
    public boolean le(ValidationErrorLevel level) { return this.compareTo(level) <= 0; }
    public boolean isWarning() { return this == WARNING; }
    public boolean isError()   { return this == ERROR;   }
    //@formatter:on
}
