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

import com.google.common.base.Predicates;
import org.opensingular.internal.lib.commons.xml.ConversorToolkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Responsvel por adicionar uma linha no fim da tabela contendo as informaes agregadas da coluna conforme configurado
 */
public class GenerationModifierAggregate extends GenerationModifier {

    private final HashMap<Column, ColumnAggregationType> aggregationTypeByColumn = new HashMap<>();
    private final HashMap<Column, Object> externalResultByColumn = new HashMap<>();

    GenerationModifierAggregate(TableTool tableTool) {
        super(tableTool);
    }

    void addColumn(Column column, ColumnAggregationType aggregationType) {
        aggregationTypeByColumn.put(column, aggregationType);
    }

    void setColumnExternalResult(Column column, Object value) {
        externalResultByColumn.put(column, value);
    }
    
    @Override
    public DataReader apply(DataReader original) {
        List<LineData> lines = original.preLoadDataAndCells(getTable());

        LineData aggregator = new LineData(getTable().newBlankLine());
        if(!aggregationTypeByColumn.containsKey(getColumns().get(0))){
            aggregator.getInfoCell(getColumns().get(0))
                .setValue("Resultado").getDecorator().addTitle("Agregado").setBold(true);
        }
        doAggregation(lines, aggregator);
        lines.add(aggregator);
        
        return super.apply(new DataReaderFixed(original, lines));
    }

    void doAggregation(Collection<LineData> lines, LineData aggregator) {
        for (Entry<Column, ColumnAggregationType> entry : aggregationTypeByColumn.entrySet()) {
            ColumnAggregationType aggregationType = entry.getValue();
            Column column = entry.getKey();
            Object value = externalResultByColumn.get(column);
            InfoCell infoCell = aggregator.getInfoCell(column);
            if(value != null || aggregationType.isCalculation()){
                setValue(infoCell, value)
                    .getDecorator().addStyle("cursor", "pointer")
                    .addTitle(aggregationType.getName()).setBold(true);
            } else {
                List<Object> columnData = lines.stream().map(lineData -> lineData.getInfoCell(column))
                    .filter(Predicates.notNull())
                    .map(lineData -> lineData.getValueReal() != null ? lineData.getValueReal() : lineData.getValue())
                    .collect(Collectors.toList());
                
                setValue(infoCell, aggregationType.calculate(columnData))
                    .getDecorator().addStyle("cursor", "pointer")
                    .addTitle(aggregationType.getName()).setBold(true);
            }
            if(aggregationType.isCount() || aggregationType.isCountDistinct()){
                infoCell.getDecorator().addStyle("text-align", "center");
            }
        }
    }
    
    private InfoCell setValue(InfoCell cell, Object value) {
        Object value2 = value;
        if (value2 instanceof Double) {
            value2 = ConversorToolkit.printNumber((Double)value2);
        } else if (value2 instanceof Number) {
            value2 = value2.toString();
        }
        cell.setValue(value2);
        return cell;
    }
}
