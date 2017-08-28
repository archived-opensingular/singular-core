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

import com.google.common.base.Predicates;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Respons�vel por adicionar uma linha no fim da tabela contendo as informa��es agregadas da coluna conforme configurado
 */
public class ModificadorGeradorAgregar extends ModificadorGerador {

    private final Map<Column, TipoAgregacaoCampo> colunaTipoAgregacao = new HashMap<>();
    private final Map<Column, Object> colunaCalculoExterno = new HashMap<>();

    ModificadorGeradorAgregar(TableTool tableTool) {
        super(tableTool);
    }

    void addColuna(Column column, TipoAgregacaoCampo tipoAgregacao) {
        colunaTipoAgregacao.put(column, tipoAgregacao);
    }

    void setValorCalculoExterno(Column column, Object valor) {
        colunaCalculoExterno.put(column, valor);
    }
    
    @Override
    public DadoLeitor aplicar(DadoLeitor original) {
        List<DadoLinha> linhas = original.preCarregarDadosECelulas(getTable());

        DadoLinha agregador = new DadoLinha(getTable().newBlankLine());
        if(!colunaTipoAgregacao.containsKey(getColunas().get(0))){
            agregador.getInfoCelula(getColunas().get(0))
                .setValor("Resultado").getDecorator().addTitle("Agregado").setBold(true);
        }
        realizaAgregacao(linhas, agregador);
        linhas.add(agregador);
        
        return super.aplicar(new DadoLeitorFixo(original, linhas));
    }

    void realizaAgregacao(Collection<DadoLinha> linhas, DadoLinha agregador) {
        for (Entry<Column, TipoAgregacaoCampo> entry : colunaTipoAgregacao.entrySet()) {
            TipoAgregacaoCampo tipoAgregacao = entry.getValue();
            Column column = entry.getKey();
            Object valor = colunaCalculoExterno.get(column);
            InfoCelula infoCelula = agregador.getInfoCelula(column);
            if(valor != null || tipoAgregacao.isCalcular()){
                setValor(infoCelula, valor)
                    .getDecorator().addStyle("cursor", "pointer")
                    .addTitle(tipoAgregacao.getNome()).setBold(true);
            } else {
                List<Object> dadosColuna = linhas.stream().map(dado -> dado.getInfoCelula(column))
                    .filter(Predicates.notNull())
                    .map(dado -> dado.getValorReal() != null ? dado.getValorReal() : dado.getValue())
                    .collect(Collectors.toList());
                
                setValor(infoCelula, tipoAgregacao.calcular(dadosColuna))
                    .getDecorator().addStyle("cursor", "pointer")
                    .addTitle(tipoAgregacao.getNome()).setBold(true);
            }
            if(tipoAgregacao.isCount() || tipoAgregacao.isCountDistinct()){
                infoCelula.getDecorator().addStyle("text-align", "center");
            }
        }
    }
    
    private InfoCelula setValor(InfoCelula celula, Object val) {
        Object valor = val;
        if (valor instanceof Double) {
            valor = ConversorToolkit.printNumber((Double)valor);
        } else if (valor instanceof Number) {
            valor = valor.toString();
        }
        celula.setValor(valor);
        return celula;
    }
}
