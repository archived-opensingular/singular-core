/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.emec.credenciamentoescolagoverno.form;

import org.opensingular.singular.exemplos.SelectBuilder;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.util.transformer.Value;

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
