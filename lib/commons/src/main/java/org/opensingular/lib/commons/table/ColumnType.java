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

package org.opensingular.lib.commons.table;

import javax.annotation.Nonnull;

/**
 * @author Daniel C. Bordin on 26/07/2017.
 */
public enum ColumnType {
    //@formatter:off
    String(ColumnTypeProcessor.STRING),
    Date(ColumnTypeProcessor.DATE),
    DateHour(ColumnTypeProcessor.DATE_HOUR),
    DateHourShort(ColumnTypeProcessor.DATE_HOUR_SHORT),
    Integer(ColumnTypeProcessor.INTEGER),
    Money(ColumnTypeProcessor.NUMBER),
    Number(ColumnTypeProcessor.NUMBER),
    Percent(ColumnTypeProcessor.PERCENT),
    Hour(ColumnTypeProcessor.HOUR),
    Action(ColumnTypeProcessor.ACTION),
    Boolean(ColumnTypeProcessor.BOOLEAN),
    Html(ColumnTypeProcessor.RAW),
    Periodo(ColumnTypeProcessor.RAW),
    Day(ColumnTypeProcessor.DAY);
    //@formatter:on

    private final ColumnTypeProcessor processor;

    ColumnType(@Nonnull ColumnTypeProcessor processor) {this.processor = processor;}

    @Nonnull
    public ColumnTypeProcessor getProcessor() {
        return processor;
    }
}
