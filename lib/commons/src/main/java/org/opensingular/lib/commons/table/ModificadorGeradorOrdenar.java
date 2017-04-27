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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class ModificadorGeradorOrdenar extends ModificadorGerador {

    private final List<Column> colunasOrdem = new ArrayList<>();

    private final boolean descending;

    public ModificadorGeradorOrdenar(TableTool table, Column c, boolean descending) {
        super(table);
        addColuna(c);
        this.descending = descending;
    }

    public void addColuna(Column column) {
        colunasOrdem.add(column);
    }

    @Override
    public DadoLeitor aplicar(DadoLeitor original) {
        List<DadoLinha> linhas = original.preCarregarDadosECelulas(getTable());

        Column[] ordem = colunasOrdem.toArray(new Column[colunasOrdem.size()]);
        Comparator<DadoLinha> comp = new Comparator<DadoLinha>() {
            @Override
            public int compare(DadoLinha d1, DadoLinha d2) {
                for (int i = 0; i < ordem.length; i++) {
                    InfoCelula c1 = d1.getInfoCelula(ordem[i]);
                    InfoCelula c2 = d2.getInfoCelula(ordem[i]);
                    int r = descending ? ordem[i].compare(c2, c1) : ordem[i].compare(c1, c2);
                    if (r != 0) {
                        return r;
                    }
                }
                return 0;
            }
        };

        Collections.sort(linhas, comp);

        return super.aplicar(new DadoLeitorFixo(original, linhas));
    }
}
