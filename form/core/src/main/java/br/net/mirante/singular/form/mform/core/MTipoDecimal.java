package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@MInfoTipo(nome = "Decimal", pacote = MPacoteCore.class)
public class MTipoDecimal extends MTipoSimples<MIBigDecimal, BigDecimal> {

    public MTipoDecimal() {
        super(MIBigDecimal.class, BigDecimal.class);
    }

    protected MTipoDecimal(Class<? extends MIBigDecimal> classeInstancia) {
        super(classeInstancia, BigDecimal.class);
    }

    @Override
    protected BigDecimal converterNaoNativoNaoString(Object valor) {
        if (valor instanceof Number) {
            return new BigDecimal(valor.toString());
        }
        throw createErroConversao(valor);
    }

    @Override
    public BigDecimal fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }

        try {
            return new BigDecimal(valor.replaceAll("\\.", "").replaceAll(",", "."));

        } catch (Exception e) {
            throw createErroConversao(valor, BigDecimal.class, null, e);
        }
    }
}
