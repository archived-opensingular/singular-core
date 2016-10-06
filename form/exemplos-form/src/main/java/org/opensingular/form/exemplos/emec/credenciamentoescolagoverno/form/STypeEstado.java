/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
