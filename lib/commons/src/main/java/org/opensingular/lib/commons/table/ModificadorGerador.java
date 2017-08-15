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

import java.io.Serializable;
import java.util.List;

public abstract class ModificadorGerador implements Serializable {

    private final TableTool table;

    private ModificadorGerador proximo;

    public ModificadorGerador(TableTool table) {
        this.table = table;
    }

    protected TableTool getTable() {
        return table;
    }

    protected List<Column> getColunas() {
        return table.getColumns();
    }

    public DadoLeitor aplicar(DadoLeitor dadoLeitor) {
        if (proximo != null) {
            return proximo.aplicar(dadoLeitor);
        }
        return dadoLeitor;
    }

    public List<Column> adjustTitles(List<Column> visiveis) {
        if (proximo != null) {
            return proximo.adjustTitles(visiveis);
        }
        return visiveis;
    }

    public void addFimCadeia(ModificadorGerador novo) {
        for (ModificadorGerador atual = this; ; atual = atual.proximo) {
            if (atual.proximo == null) {
                atual.proximo = novo;
                return;
            }
        }
    }

    public ModificadorGerador getProximoModificador() {
        return proximo;
    }

}
