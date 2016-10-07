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

package org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form;

import org.opensingular.form.exemplos.SelectBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeEstado extends STypeComposite<SIComposite> {
    
    private static final String FIELD_NOME = "nome";
    private static final String FIELD_SIGLA = "sigla";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        asAtr().label("UF");
        
        STypeString sigla = addFieldString(FIELD_SIGLA, true);
        STypeString nome = addFieldString(FIELD_NOME, true);
        
        selection()
            .id(sigla).display(nome)
            .simpleProvider(builder -> SelectBuilder.buildEstados().stream().forEach(entry -> builder.add().set(sigla, entry.getSigla()).set(nome, entry.getNome())));
    }
    
    public STypeString getFieldSigla(){
        return (STypeString) getField(FIELD_SIGLA);
    }

    public STypeString getFieldNome(){
        return (STypeString) getField(FIELD_NOME);
    }
}
