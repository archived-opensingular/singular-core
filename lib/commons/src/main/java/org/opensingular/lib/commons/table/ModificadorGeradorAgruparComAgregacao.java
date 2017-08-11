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
class ModificadorGeradorAgruparComAgregacao extends ModificadorGerador {
    private static final long serialVersionUID = 1L;
    
    private final List<Column> colunasAgrupamento = new ArrayList<>();  // Colunas do agrupamento por ordem de inserção
    private final Map<Column, TipoAgregacaoCampo> colunaTipoAgregacao;  // Tipo de agregação por coluna
    
    public ModificadorGeradorAgruparComAgregacao(TableTool table) {
        super(table);
        colunaTipoAgregacao = montaAgregacaoDefault();
    }

    public ModificadorGeradorAgruparComAgregacao(TableTool table,
                                                 Map<Column, TipoAgregacaoCampo> configuracaoAgregacao) {
        this(table);
        configuracaoAgregacao.forEach(colunaTipoAgregacao::put);
    }

    public void addColuna(Column column) {
        this.colunasAgrupamento.add(column);
    }

    @Override
    public DadoLeitor aplicar(DadoLeitor original) {
        Comparator<DadoLinha> sortComparator = getSortComparator(); 
        List<DadoLinha> linhas = original.preCarregarDadosECelulas(getTable());
        
        LinkedListMultimap<DadoLinha, DadoLinha> mapa = LinkedListMultimap.create();
        if(!linhas.isEmpty()){
            // Usa o comparador para determinar as quebras de grupo. Como j� houve a
            // ordenaçãoo prévia, quando o valor da
            // comparaçãoo for diferente de zero, sinaliza quebra de grupo
            DadoLinha[] piloto = new DadoLinha[] { linhas.get(0) }; 
            linhas.stream().sorted(sortComparator).forEach(dado ->
                    mapa.put(piloto[0] = (sortComparator.compare(dado, piloto[0]) != 0) ? dado : piloto[0], dado));
        }
        
        List<DadoLinha> resultado = mapa.asMap().values().stream()
                                                         .map(this::preencheValores)
                                                         .collect(Collectors.toList());
        return super.aplicar(new DadoLeitorFixo(original, resultado));
    }
    
    @Override
    public List<Column> adjustTitles(List<Column> visiveis) {
        List<Column> collect = visiveis.stream().skip(1).collect(Collectors.toList());
        List<Column> colunasOrdenadas = new ArrayList<>();
        
        colunasAgrupamento.forEach(coluna -> {
            colunasOrdenadas.add(coluna);
            coluna.setSuperTitle("");
        });
        colunasOrdenadas.add(null);
        
        collect.stream().filter(coluna -> coluna == null || !colunasOrdenadas.contains(coluna))
                        .forEach(colunasOrdenadas::add);
        
        return super.adjustTitles(Lists.newArrayList(colunasOrdenadas));
    }

    
    private DadoLinha preencheValores(Collection<DadoLinha> informacaoAgrupada) {

        DadoLinha novoDadoLinha = new DadoLinha(getTable().newBlankLine());
        DadoLinha referencia = informacaoAgrupada.stream().findFirst().orElseThrow(() -> new SingularException("Não foi possivel encontrar a referencia."));
        colunasAgrupamento.forEach(coluna -> copiarValoresCelula(novoDadoLinha.getInfoCelula(coluna), referencia.getInfoCelula(coluna)));
        
        realizaAgregacao(informacaoAgrupada, novoDadoLinha);
        return novoDadoLinha;
    }
    
    private void copiarValoresCelula(InfoCelula dado, InfoCelula referencia) {
        dado.setValor(referencia.getValue());
        dado.setValorReal(referencia.getValorReal());
    }

    public void realizaAgregacao(Collection<DadoLinha> linhas, DadoLinha agregador) {
        for (Entry<Column, TipoAgregacaoCampo> entry : colunaTipoAgregacao.entrySet()) {
            TipoAgregacaoCampo tipoAgregacao = entry.getValue();
            Column column = entry.getKey();

            if (colunasAgrupamento.contains(column)) { continue; } // As colunas agrupadas não são agregadas //NOSONAR
            
            setValor(agregador.getInfoCelula(column), tipoAgregacao.calcular(recuperaDadosColuna(linhas, column)))
                .getDecorator().addStyle("cursor", "pointer")
                               .addTitle(tipoAgregacao.getNome());
        }
    }

    private static List<Object> recuperaDadosColuna(Collection<DadoLinha> linhas, Column column) {
        return linhas.stream().map(dado -> dado.getInfoCelula(column))
            .map(dado -> dado == null ? null : (Object)(dado.getValorReal() != null ? dado.getValorReal() : dado.getValue()))
            .collect(Collectors.toList());
    }
    
    private static InfoCelula setValor(InfoCelula celula, Object valor) {
        if (valor instanceof Integer || valor instanceof Long || valor instanceof Double) {
            celula.setValorReal((Comparable<?>) valor); 
        }
        celula.setValor(valor);
        return celula;
    }
    
    private Map<Column, TipoAgregacaoCampo> montaAgregacaoDefault() {
        Map<Column, TipoAgregacaoCampo> agregacaoDefault = new HashMap<>();
        getColunas().forEach(coluna -> {
            switch (coluna.getTipo()) {
            case NUMBER:
            case INTEGER:
            case MONEY:
                agregacaoDefault.put(coluna, TipoAgregacaoCampo.SOMA);
                break;
            default: break;
            }
        });
        return agregacaoDefault;
    }
    
    private Comparator<DadoLinha> montaComparador(Column column) {
        return (o1, o2) -> column.compare(o1.getInfoCelula(column), o2.getInfoCelula(column));
    }

    private Comparator<DadoLinha> getSortComparator() {
        return colunasAgrupamento.stream().map(this::montaComparador).reduce(Comparator::thenComparing).get();
    }
}
