/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.enums;

import java.util.Arrays;
import java.util.List;

public enum AcaoGestaoObras {
    EDIFICACOES("Edificações"){
        @Override
        public List<String> getChecklistItens() {
            return Arrays.asList("Projeto", "Plano de Execução", "Planta Baixa");
        }
        @Override
        public List<String> getTipologias() {
            return Arrays.asList("1.1 UBSI", "1.2 Polo Base", "1.3 CASAI", "1.4 Sede DSEI", "1.5 Custeio", "1.6 Equipamento");
        }
    },
    SANEAMENTO("Saneamento"){
        @Override
        public List<String> getChecklistItens() {
            return Arrays.asList("Projeto de Sistema de Abastecimento de Água", "Projeto de Estação de Tratamento de Água");
        }
        @Override
        public List<String> getTipologias() {
            return Arrays.asList("1.1 Água", "1.2 MSD", "1.3 Custeio", "1.4 Equipamento");
        }
    };
    private final String descricao;

    private AcaoGestaoObras(String descricao) {
        this.descricao = descricao;
    }
    
    public abstract List<String> getChecklistItens();

    public abstract List<String> getTipologias();
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return getDescricao();
    }
}
