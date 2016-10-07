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
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeMunicipio extends STypeComposite<SIComposite> {
    
    private static final String FIELD_NOME = "nome";
    private static final String FIELD_UF = "uf";
    private static final String FIELD_ID = "id";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        asAtr().label("Município");
        
        addFieldInteger(FIELD_ID, true);
        addFieldString(FIELD_UF, true);
        addFieldString(FIELD_NOME, true);
        
    }
    
    public STypeInteger getFieldId(){
        return (STypeInteger) getField(FIELD_ID);
    }

    public STypeString getFieldUF(){
        return (STypeString) getField(FIELD_UF);
    }

    public STypeString getFieldNome(){
        return (STypeString) getField(FIELD_NOME);
    }
    
    public STypeMunicipio selectionByUF(STypeEstado uf){
        selection()
            .id(getFieldId()).display(getFieldNome())
            .simpleProvider(builder -> SelectBuilder.buildMunicipiosFiltrado(Value.of(builder.getCurrentInstance(), uf.getFieldSigla()))
                .stream().forEach(entry -> builder.add()
                        .set(getFieldId(), entry.getId())
                        .set(getFieldUF(), entry.getUF())
                        .set(getFieldNome(), entry.getNome())));
        
        asAtr().dependsOn(uf);
        
        return this;
    }
}
