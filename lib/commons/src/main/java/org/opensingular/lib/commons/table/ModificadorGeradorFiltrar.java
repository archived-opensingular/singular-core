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

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModificadorGeradorFiltrar extends ModificadorGerador {
    
    private static final long serialVersionUID = 1L;
    
    private transient Map<Column, Predicate<InfoCelula>> predicados = Maps.newHashMap();

    public ModificadorGeradorFiltrar(TableTool table) {
        super(table);
    }

    public ModificadorGeradorFiltrar addColuna(Column column, Predicate<InfoCelula> filtro) {
        predicados.put(column, filtro);
        return this;
    }

    @Override
    public DadoLeitor aplicar(DadoLeitor original) {
        List<DadoLinha> linhas = original.preCarregarDadosECelulas(getTable()).stream()
            .filter(this::filtraLinha)
            .collect(Collectors.toList());
        
        return super.aplicar(new DadoLeitorFixo(original, linhas));
    }
    
    private boolean filtraLinha(DadoLinha linha) {
        return predicados.entrySet().stream().allMatch(pair -> pair.getValue().test(linha.getInfoCelula(pair.getKey())));
    }
}
