/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
