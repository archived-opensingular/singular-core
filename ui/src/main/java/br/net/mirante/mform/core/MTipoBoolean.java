package br.net.mirante.mform.core;

import org.apache.commons.lang.StringUtils;

import br.net.mirante.mform.MFormTipo;
import br.net.mirante.mform.MTipoSimples;

@MFormTipo(nome = "Boolean", pacote = MPacoteCore.class)
public class MTipoBoolean extends MTipoSimples<MIBoolean, Boolean> {

    public MTipoBoolean() {
        super(MIBoolean.class, Boolean.class);
    }

    protected MTipoBoolean(Class<? extends MIBoolean> classeInstancia) {
        super(classeInstancia, Boolean.class);
    }

    @Override
    protected Boolean converterNaoNativoNaoString(Object valor) {
        if (valor instanceof Number) {
            int v = ((Number) valor).intValue();
            if (v == 0) {
                return Boolean.FALSE;
            } else if (v == 1) {
                return Boolean.TRUE;
            }
        }
        throw createErroConversao(valor);
    }

    @Override
    public Boolean fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        } else if (valor.equalsIgnoreCase("true") || valor.equals("1")) {
            return Boolean.TRUE;
        } else if (valor.equalsIgnoreCase("false") || valor.equals("0")) {
            return Boolean.FALSE;
        }
        throw createErroConversao(valor, Boolean.class);
    }
}
