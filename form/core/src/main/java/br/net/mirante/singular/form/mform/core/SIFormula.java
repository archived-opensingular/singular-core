package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SIComposite;

public class SIFormula extends SIComposite {

    public void setScipt(String valor) {
        setValor(STypeFormula.CAMPO_SCRIPT, valor);
    }

    public void setSciptJS(String valor) {
        setScipt(valor);
        setTipoScript(STypeFormula.TipoScript.JS);
    }

    private void setTipoScript(STypeFormula.TipoScript t) {
        setValor(STypeFormula.CAMPO_TIPO_SCRIPT, t);
    }

    public String getTipoScript() {
        return getValorString(STypeFormula.CAMPO_TIPO_SCRIPT);
    }

    public STypeFormula.TipoScript getTipoScriptEnum() {
        return getValorEnum(STypeFormula.CAMPO_TIPO_SCRIPT, STypeFormula.TipoScript.class);
    }
}
