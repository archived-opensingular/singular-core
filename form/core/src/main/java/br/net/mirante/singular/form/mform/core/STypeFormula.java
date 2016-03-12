package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;

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
        tipo.withSelectionOf(TipoScript.class);
    }

    public static enum TipoScript {
        JS;
    }
}
