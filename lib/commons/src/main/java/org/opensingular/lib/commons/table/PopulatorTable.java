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

public class PopulatorTable {

    private final TableTool tableTool;

    private final List<InfoCelula[]> celulas = new ArrayList<>();

    private InfoCelula ultima;

    public PopulatorTable(TableTool tableTool) {
        this.tableTool = tableTool;
    }

    public PopulatorTable insertLine() {
        celulas.add(new InfoCelula[tableTool.getColumns().size()]);
        ultima = null;
        return this;
    }

    public PopulatorTable insertLine(Object... valores) {
        insertLine();
        setValores(valores);
        return this;
    }

    public InfoCelula ultima() {
        return ultima;
    }

    public InfoCelula setValor(int pos, Object valor) {
        return setValor(celulas.get(celulas.size() - 1), pos, valor);
    }

    private InfoCelula setValor(InfoCelula[] linha, int pos, Object valor) {
        if (linha[pos] == null) {
            linha[pos] = new InfoCelula(tableTool.getColumn(pos));
        }
        linha[pos].setValor(valor);
        ultima = linha[pos];
        return linha[pos];
    }

    public InfoCelula setValores(Object... valores) {
        InfoCelula[] linha = celulas.get(celulas.size() - 1);
        for (int i = 0; i < valores.length; i++) {
            setValor(linha, i, valores[i]);
        }
        return linha[valores.length - 1];
    }

    public boolean isEmpty() {
        return celulas.isEmpty();
    }

    @SuppressWarnings("serial")
    public LeitorArvore asLeitorArvore() {
        return new LeitorArvore() {

            @Override
            public Object getRaizes() {
                return celulas;
            }

            @Override
            public Object getFilhos(Object item) {
                return null;
            }

            @Override
            public void recuperarValores(LineReadContext ctx, Object current, InfoLinha line) {
                InfoCelula[] linha = (InfoCelula[]) current;
                if (linha != null) {
                    for (int i = 0; i < linha.length; i++) {
                        if (linha[i] != null) {
                            line.setCell(i, linha[i]);
                            // Seta de novo o valor pois a coluna pode fazer
                            // algum tratamento sobre o valor
                            line.get(i).setValor(linha[i].getValue());
                        }
                    }
                }
            }
        };
    }

}
