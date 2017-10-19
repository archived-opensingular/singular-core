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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import org.opensingular.lib.commons.base.SingularException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Agrupador com agregaçaoo. Agrupa colunas e em seguida realiza a operaçao de agregção.
 * 
 * @author joao.gomes
 */
class GenerationModifierAgruparComAgregacao extends GenerationModifier {
    private static final long serialVersionUID = 1L;
    
    private final List<Column> colunasAgrupamento = new ArrayList<>();  // Colunas do agrupamento por ordem de inserção
    private final Map<Column, ColumnAggregationType> aggregationTypeByColumn;  // Tipo de agregação por coluna
    
    public GenerationModifierAgruparComAgregacao(TableTool table) {
        super(table);
        aggregationTypeByColumn = createDefaultAggregation();
    }

    public GenerationModifierAgruparComAgregacao(TableTool table,
                                                 Map<Column, ColumnAggregationType> aggregationConfiguration) {
        this(table);
        aggregationConfiguration.forEach(aggregationTypeByColumn::put);
    }

    public void addColuna(Column column) {
        this.colunasAgrupamento.add(column);
    }

    @Override
    public DataReader apply(DataReader original) {
        Comparator<LineData> sortComparator = getSortComparator();
        List<LineData> lines = original.preLoadDataAndCells(getTable());
        
        LinkedListMultimap<LineData, LineData> map = LinkedListMultimap.create();
        if(!lines.isEmpty()){
            // Usa o comparador para determinar as quebras de grupo. Como j� houve a
            // ordenaçãoo prévia, quando o valor da
            // comparaçãoo for diferente de zero, sinaliza quebra de grupo
            LineData[] piloto = new LineData[] { lines.get(0) };
            lines.stream().sorted(sortComparator).forEach(dado ->
                    map.put(piloto[0] = (sortComparator.compare(dado, piloto[0]) != 0) ? dado : piloto[0], dado));
        }
        
        List<LineData> result = map.asMap().values().stream()
                                                         .map(this::fillValues)
                                                         .collect(Collectors.toList());
        return super.apply(new DataReaderFixed(original, result));
    }
    
    @Override
    public List<Column> adjustTitles(List<Column> visibleColumns) {
        List<Column> collect = visibleColumns.stream().skip(1).collect(Collectors.toList());
        List<Column> orderedColumns = new ArrayList<>();
        
        colunasAgrupamento.forEach(column -> {
            orderedColumns.add(column);
            column.setSuperTitle("");
        });
        orderedColumns.add(null);
        
        collect.stream().filter(column -> column == null || !orderedColumns.contains(column))
                        .forEach(orderedColumns::add);
        
        return super.adjustTitles(Lists.newArrayList(orderedColumns));
    }

    
    private LineData fillValues(Collection<LineData> informacaoAgrupada) {

        LineData newLineData = new LineData(getTable().newBlankLine());
        LineData reference = informacaoAgrupada.stream().findFirst().orElseThrow(() -> new SingularException("Não foi possivel encontrar a referencia."));
        colunasAgrupamento.forEach(column -> copyCellValues(newLineData.getInfoCell(column), reference.getInfoCell(column)));
        
        doAggregation(informacaoAgrupada, newLineData);
        return newLineData;
    }
    
    private void copyCellValues(InfoCell dado, InfoCell reference) {
        dado.setValue(reference.getValue());
        dado.setValueReal(reference.getValueReal());
    }

    public void doAggregation(Collection<LineData> lines, LineData agregador) {
        for (Entry<Column, ColumnAggregationType> entry : aggregationTypeByColumn.entrySet()) {
            ColumnAggregationType aggregationType = entry.getValue();
            Column column = entry.getKey();

            if (colunasAgrupamento.contains(column)) { continue; } // As colunas agrupadas não são agregadas //NOSONAR
            
            setValue(agregador.getInfoCell(column), aggregationType.calculate(retrieveColumnData(lines, column)))
                .getDecorator().addStyle("cursor", "pointer")
                               .addTitle(aggregationType.getName());
        }
    }

    private static List<Object> retrieveColumnData(Collection<LineData> lines, Column column) {
        return lines.stream().map(dado -> dado.getInfoCell(column))
            .map(dado -> dado == null ? null : (Object)(dado.getValueReal() != null ? dado.getValueReal() : dado.getValue()))
            .collect(Collectors.toList());
    }
    
    private static InfoCell setValue(InfoCell cell, Object value) {
        if (value instanceof Integer || value instanceof Long || value instanceof Double) {
            cell.setValueReal((Comparable<?>) value);
        }
        cell.setValue(value);
        return cell;
    }
    
    private Map<Column, ColumnAggregationType> createDefaultAggregation() {
        Map<Column, ColumnAggregationType> defaultAggregation = new HashMap<>();
        getColunas().forEach(column -> {
            switch (column.getType()) {
            case NUMBER:
            case INTEGER:
            case MONEY:
                defaultAggregation.put(column, ColumnAggregationType.SUM);
                break;
            default: break;
            }
        });
        return defaultAggregation;
    }
    
    private Comparator<LineData> createComparator(Column column) {
        return (o1, o2) -> column.compare(o1.getInfoCell(column), o2.getInfoCell(column));
    }

    private Comparator<LineData> getSortComparator() {
        return colunasAgrupamento.stream().map(this::createComparator).reduce(Comparator::thenComparing).orElse(null);
    }
}
