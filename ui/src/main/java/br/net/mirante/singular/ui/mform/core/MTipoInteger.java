package br.net.mirante.singular.ui.mform.core;

import org.apache.commons.lang.StringUtils;

import br.net.mirante.singular.ui.mform.MFormTipo;
import br.net.mirante.singular.ui.mform.MTipoSimples;

@MFormTipo(nome = "Integer", pacote = MPacoteCore.class)
public class MTipoInteger extends MTipoSimples<MIInteger, Integer> {

    public MTipoInteger() {
        super(MIInteger.class, Integer.class);
    }

    protected MTipoInteger(Class<? extends MIInteger> classeInstancia) {
        super(classeInstancia, Integer.class);
    }

    @Override
    protected Integer converterNaoNativoNaoString(Object valor) {
        if (valor instanceof Number) {
            long longValue = ((Number) valor).longValue();
            if (longValue > Integer.MAX_VALUE) {
                throw createErroConversao(valor, Integer.class, " Valor muito grande.", null);
            }
            if (longValue < Integer.MIN_VALUE) {
                throw createErroConversao(valor, Integer.class, " Valor muito pequeno.", null);
            }
            return ((Number) valor).intValue();
        }
        throw createErroConversao(valor);
    }

    @Override
    public Integer fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            throw createErroConversao(valor, Integer.class, null, e);
        }
    }
}
