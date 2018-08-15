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

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GenerationModifierFilter extends GenerationModifier {
    
    private static final long serialVersionUID = 1L;
    
    private transient Map<Column, Predicate<InfoCell>> predicates = Maps.newHashMap();

    public GenerationModifierFilter(TableTool table) {
        super(table);
    }

    public GenerationModifierFilter addColumn(Column column, Predicate<InfoCell> filter) {
        predicates.put(column, filter);
        return this;
    }

    @Override
    public DataReader apply(DataReader original) {
        List<LineData> lines = original.preLoadDataAndCells(getTable()).stream()
            .filter(this::filterLine)
            .collect(Collectors.toList());
        
        return super.apply(new DataReaderFixed(original, lines));
    }
    
    private boolean filterLine(LineData line) {
        return predicates.entrySet().stream().allMatch(pair -> pair.getValue().test(line.getInfoCell(pair.getKey())));
    }
}
