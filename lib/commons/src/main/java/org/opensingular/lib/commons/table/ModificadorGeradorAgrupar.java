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

import java.util.List;
import java.util.Objects;

class ModificadorGeradorAgrupar extends ModificadorGerador {

    private final Column column;

    public ModificadorGeradorAgrupar(TableTool table, Column column) {
        super(table);
        this.column = column;
        for (Column c : getColunas()) {
            if (c != column && c.getNivelDados() >= column.getNivelDados()) {
                c.setNivelDados(c.getNivelDados() + 1);
            }
        }
    }

    @Override
    public DadoLeitor aplicar(DadoLeitor dadoLeitor) {
        DadoLeitorFixo grupo = new DadoLeitorFixo(null);
        Object valorAtual = null;
        DadoLeitorFixo filhos = null;
        for (DadoLinha linha : dadoLeitor) {
            InfoCelula celula = linha.getInfoCelula(column);
            Object valorCelulaLinha = celula == null ? null : celula.getValue();
            if (!Objects.equals(valorAtual, valorCelulaLinha)) {
                filhos = new DadoLeitorFixo(dadoLeitor);
                grupo.add(new DadoLinha(linha, new DadoLeitorModificador(filhos, getProximoModificador())));
                valorAtual = valorCelulaLinha;
            }
            filhos.add(linha);
        }

        return grupo;
    }

    @Override
    public List<Column> adjustTitles(List<Column> visiveis) {
        int posColuna = visiveis.indexOf(column);
        if (posColuna != -1) {
            int posNova = posColuna;
            for (int i = posColuna - 1; i >= 0; i--) {
                Column c = visiveis.get(i);
                if (c.getNivelDados() > column.getNivelDados()) {
                    posNova = i;
                }
            }
            if (posNova != posColuna) {
                for (int i = posColuna; i > posNova; i--) {
                    visiveis.set(i, visiveis.get(i - 1));
                }
                visiveis.set(posNova, column);
            }
        }
        return super.adjustTitles(visiveis);
    }

}
