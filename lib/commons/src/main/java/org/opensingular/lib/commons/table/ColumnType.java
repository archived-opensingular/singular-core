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

package org.opensingular.lib.commons.table;

import javax.annotation.Nonnull;

/**
 * @author Daniel C. Bordin on 26/07/2017.
 */
public enum ColumnType {
    //@formatter:off
    STRING(ColumnTypeProcessor.STRING),
    DATE(ColumnTypeProcessor.DATE),
    DATEHOUR(ColumnTypeProcessor.DATE_HOUR),
    DATEHOURSHORT(ColumnTypeProcessor.DATE_HOUR_SHORT),
    INTEGER(ColumnTypeProcessor.INTEGER),
    MONEY(ColumnTypeProcessor.NUMBER),
    NUMBER(ColumnTypeProcessor.NUMBER),
    PERCENT(ColumnTypeProcessor.PERCENT),
    HOUR(ColumnTypeProcessor.HOUR),
    ACTION(ColumnTypeProcessor.ACTION),
    BOOLEAN(ColumnTypeProcessor.BOOLEAN),
    HTML(ColumnTypeProcessor.RAW),
    PERIODO(ColumnTypeProcessor.RAW),
    DAY(ColumnTypeProcessor.DAY);
    //@formatter:on

    private final ColumnTypeProcessor processor;

    ColumnType(@Nonnull ColumnTypeProcessor processor) {this.processor = processor;}

    @Nonnull
    public ColumnTypeProcessor getProcessor() {
        return processor;
    }
}
