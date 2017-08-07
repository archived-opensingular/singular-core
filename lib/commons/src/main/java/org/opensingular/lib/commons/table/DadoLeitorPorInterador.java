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

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class DadoLeitorPorInterador extends DadoLeitor {

    private final LeitorArvore leitor;
    private final int numNivel;

    private boolean vazio = true;
    private boolean lido;
    private Iterator<?> original;

    private DadoLinha atual;

    private List<DadoLinha> dados;

    public DadoLeitorPorInterador(LeitorArvore leitor, Object lista, int numNivel) {
        this.leitor = leitor;
        this.numNivel = numNivel;

        if (lista != null) {
            original = GeradorUtil.obterIterador(lista);
            proximo();
            vazio = (atual == null);
        }
    }

    @Override
    public boolean isEmpty() {
        return vazio;
    }

    private void proximo() {
        if (original == null) {
            throw new RuntimeException("N�o existe nada para ler");
        }
        while (original.hasNext()) {
            Object v = original.next();
            if (v != null) {
                atual = new DadoLinha(this, v);
                return;
            }
        }
        atual = null;
        original = null;
    }

    @Override
    public Iterator<DadoLinha> iterator() {
        if (dados != null) {
            return dados.iterator();
        }
        if (lido) {
            throw new RuntimeException("N�o pode ser chamado iterator() duas vezes");
        }
        lido = true;

        // Retorna a lista pulando os nulos
        return new Iterator<DadoLinha>() {
            @Override
            public boolean hasNext() {
                return atual != null;
            }

            @Override
            public DadoLinha next() {
                DadoLinha anterior = atual;
                proximo();
                return anterior;
            }
        };
    }

    @Override
    public List<DadoLinha> preCarregarDados() {
        if (dados == null) {
            if (isEmpty()) {
                dados = new ArrayList<>(1);
            } else {
                dados = Lists.newArrayList(this);
            }
        }
        return dados;
    }

    @Override
    public DadoLeitorPorInterador getFilhos(DadoLinha dadoLinha) {
        return new DadoLeitorPorInterador(leitor, leitor.getFilhos(dadoLinha.getValor()), numNivel + 1);
    }

    @Override
    public InfoLinha recuperarValores(LineReadContext ctx, DadoLinha dadoLinha) {
        InfoLinha line = ctx.getTable().newBlankLine();
        leitor.recuperarValores(ctx, dadoLinha.getValor(), line);
        return line;
    }

    @Override
    public List<DadoLinha> preCarregarDadosECelulas(TableTool tableTool) {
        List<DadoLinha> lista = preCarregarDados();
        LineReadContext ctx = new LineReadContext(tableTool);
        ctx.setLevel(numNivel);
        for (DadoLinha dado : lista) {
            dado.recuperarValores(ctx, numNivel, true, true);
        }
        return lista;
    }

}
