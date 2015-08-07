package br.net.mirante.singular.ui.mform.core;

import br.net.mirante.singular.ui.mform.MIComposto;

public class MIFormula extends MIComposto {

    public void setScipt(String valor) {
        setValor(MTipoFormula.CAMPO_SCRIPT, valor);
    }

    public void setSciptJS(String valor) {
        setScipt(valor);
        setTipoScript(MTipoFormula.TipoScript.JS);
    }

    private void setTipoScript(MTipoFormula.TipoScript t) {
        setValor(MTipoFormula.CAMPO_TIPO_SCRIPT, t);
    }

    public String getTipoScript() {
        return getValorString(MTipoFormula.CAMPO_TIPO_SCRIPT);
    }

    public MTipoFormula.TipoScript getTipoScriptEnum() {
        return getValorEnum(MTipoFormula.CAMPO_TIPO_SCRIPT, MTipoFormula.TipoScript.class);
    }
}
