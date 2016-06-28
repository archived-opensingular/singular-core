/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.exemplos.SelectBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeString;

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
