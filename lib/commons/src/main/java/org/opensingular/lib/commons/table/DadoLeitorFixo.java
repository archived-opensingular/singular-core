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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class DadoLeitorFixo extends DadoLeitor {

    private final DadoLeitor original;

    private final List<DadoLinha> linhas;

    public DadoLeitorFixo(DadoLeitor original) {
        this.original = original;
        linhas = new ArrayList<>();
    }

    public DadoLeitorFixo(DadoLeitor original, List<DadoLinha> linhas) {
        this.original = original;
        this.linhas = linhas;
    }

    public void add(DadoLinha linha) {
        linhas.add(linha);
    }

    @Override
    public Iterator<DadoLinha> iterator() {
        return linhas.iterator();
    }

    @Override
    public boolean isEmpty() {
        return linhas.isEmpty();
    }

    @Override
    public List<DadoLinha> preCarregarDados() {
        return linhas;
    }

    @Override
    public DadoLeitor getFilhos(DadoLinha dadoLinha) {
        return original.getFilhos(dadoLinha);
    }

    @Override
    public InfoLinha recuperarValores(LineReadContext ctx, DadoLinha dadoLinha) {
        throw new RuntimeException("Nunca deveria ter chamado essa linha. Pois tudo est� em memoria");
    }

    @Override
    public List<DadoLinha> preCarregarDadosECelulas(TableTool tableTool) {
        return linhas;
    }
}