package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TipoBuilder;

@MInfoTipo(nome = "Formula", pacote = SPackageCore.class)
public class STypeFormula extends STypeComposite<SIFormula> {

    public static final String CAMPO_SCRIPT = "script";
    public static final String CAMPO_TIPO_SCRIPT = "tipoScript";

    public STypeFormula() {
        super(SIFormula.class);
    }

    @Override
    protected void onLoadType(TipoBuilder tb) {
        addCampoString(CAMPO_SCRIPT);
        STypeString tipo = addCampoString(CAMPO_TIPO_SCRIPT);
        tipo.withSelectionOf(TipoScript.class);
    }

    public static enum TipoScript {
        JS;
    }
}
