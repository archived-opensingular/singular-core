/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(name = "Formula", spackage = SPackageCore.class)
public class STypeFormula extends STypeComposite<SIFormula> {

    public static final String CAMPO_SCRIPT = "script";
    public static final String CAMPO_TIPO_SCRIPT = "tipoScript";

    public STypeFormula() {
        super(SIFormula.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addFieldString(CAMPO_SCRIPT);
        STypeString tipo = addFieldString(CAMPO_TIPO_SCRIPT);
        tipo.selectionOfEnum(TipoScript.class);
    }

    public static enum TipoScript {
        JS;
    }
}