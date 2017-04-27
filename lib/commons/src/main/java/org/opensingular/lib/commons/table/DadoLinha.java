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
import java.util.List;

public final class DadoLinha {

    private DadoLeitor pai;
    private final Object valor;

    private DadoLeitor filhos;

    private Integer linhas;

    private InfoLinha line;

    DadoLinha(InfoLinha line) {
        this.valor = null;
        this.line = line;
    }

    public DadoLinha(DadoLeitor pai, Object valor) {
        this.pai = pai;
        this.valor = valor;
    }

    DadoLinha(DadoLinha original, DadoLeitor novosFilhos) {
        pai = null; // Nunca deve ler
        valor = original.valor;
        line = original.line;
        filhos = novosFilhos;
    }

    public Object getValor() {
        return valor;
    }

    public DadoLeitor getLeitorFilhos() {
        if (filhos == null && pai != null) {
            filhos = pai.getFilhos(this);
        }
        return filhos;
    }

    public void setLeitorPai(DadoLeitor novo) {
        pai = novo;
    }

    public List<DadoLinha> getFilhos() {
        return getLeitorFilhos().preCarregarDados();
    }

    public InfoLinha recuperarValores(LineReadContext ctx, int numNivel, boolean limparContextoEColunas, boolean salvar) {
        if (line == null) {
            if (limparContextoEColunas) {
                //ctx.limpar(numNivel);
            }
            InfoLinha result = pai.recuperarValores(ctx, this);
            if (salvar) {
                line = result;
            }
            return result;
        } else {
            return line;
        }
    }

    public InfoLinha getLine() {
        return line;
    }
    
    public InfoCelula getInfoCelula(Column c) {
        return line.get(c);
    }
    
    public <K> K getValor(int indice) {
        return getValor(indice, null);
    }
    
    public <K> K getValor(int indice, K valorDefault) {
        InfoCelula infoCelula = line.get(indice);
        if(infoCelula == null){
            return valorDefault;
        }
        K valor = infoCelula.getValue();
        return valor == null ? valorDefault : valor;
    }

    public int getLinhas() {
        if (linhas == null) {
            linhas = getFilhos().stream().mapToInt(p -> p.getLinhas()).sum();
            if (linhas.intValue() == 0) {
                linhas = 1;
            }
        }
        return linhas;
    }

    public List<DadoLinha[]> normalizarNiveis(int max) {
        List<DadoLinha[]> lista = new ArrayList<>(getLinhas());
        DadoLinha[] nova = new DadoLinha[max];
        nova[0] = this;
        lista.add(nova);
        normalizarFilhos(lista, 1, max);
        return lista;
    }

    private void normalizarFilhos(List<DadoLinha[]> lista, int profundidade, int max) {
        if (profundidade > max + 2) {
            return;
        }
        for (int i = 0; i < getFilhos().size(); i++) {
            DadoLinha filho = getFilhos().get(i);
            DadoLinha[] linhaAtual;
            if (i == 0) {
                linhaAtual = lista.get(lista.size() - 1);
            } else {
                linhaAtual = new DadoLinha[max];
                lista.add(linhaAtual);
            }
            linhaAtual[profundidade] = filho;
            filho.normalizarFilhos(lista, profundidade + 1, max);
        }
    }
}