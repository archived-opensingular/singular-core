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

import java.util.Iterator;
import java.util.List;

public final class DadoLeitorModificador extends DadoLeitor {

    private DadoLeitor original;

    private ModificadorGerador proximoModificador;

    public DadoLeitorModificador(DadoLeitor original, ModificadorGerador proximoModificador) {
        this.original = original;
        this.proximoModificador = proximoModificador;
    }

    private DadoLeitor getLeitorModificado() {
        if (proximoModificador != null) {
            original = proximoModificador.aplicar(original);
            proximoModificador = null;
        }
        return original;
    }

    @Override
    public Iterator<DadoLinha> iterator() {
        return getLeitorModificado().iterator();
    }

    @Override
    public boolean isEmpty() {
        return getLeitorModificado().isEmpty();
    }

    @Override
    public List<DadoLinha> preCarregarDados() {
        return getLeitorModificado().preCarregarDados();
    }

    @Override
    public DadoLeitor getFilhos(DadoLinha dadoLinha) {
        return getLeitorModificado().getFilhos(dadoLinha);
    }

    @Override
    public InfoLinha recuperarValores(LineReadContext ctx, DadoLinha dadoLinha) {
        return getLeitorModificado().recuperarValores(ctx, dadoLinha);
    }

    @Override
    public List<DadoLinha> preCarregarDadosECelulas(TableTool tableTool) {
        return getLeitorModificado().preCarregarDadosECelulas(tableTool);
    }
}