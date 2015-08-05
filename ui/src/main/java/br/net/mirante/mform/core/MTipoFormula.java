package br.net.mirante.mform.core;

import br.net.mirante.mform.MFormTipo;
import br.net.mirante.mform.MTipoComposto;
import br.net.mirante.mform.TipoBuilder;

@MFormTipo(nome = "Formula", pacote = MPacoteCore.class)
public class MTipoFormula extends MTipoComposto<MIFormula> {

    public static final String CAMPO_SCRIPT = "script";
    public static final String CAMPO_TIPO_SCRIPT = "tipoScript";

    public MTipoFormula() {
        super(MIFormula.class);
    }

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        addCampoString(CAMPO_SCRIPT);
        MTipoString tipo = addCampoString(CAMPO_TIPO_SCRIPT);
        tipo.selectionOf(TipoScript.class);
    }

    public static enum TipoScript {
        JS;
    }
}
