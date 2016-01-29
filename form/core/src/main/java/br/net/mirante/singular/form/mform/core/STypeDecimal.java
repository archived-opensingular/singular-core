package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeSimples;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@MInfoTipo(nome = "Decimal", pacote = SPackageCore.class)
public class STypeDecimal extends STypeSimples<SIBigDecimal, BigDecimal> {

    public STypeDecimal() {
        super(SIBigDecimal.class, BigDecimal.class);
    }

    protected STypeDecimal(Class<? extends SIBigDecimal> classeInstancia) {
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

    @Override
    public BigDecimal fromStringPersistencia(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }

        try {
            return new BigDecimal(valor);

        } catch (Exception e) {
            throw createErroConversao(valor, BigDecimal.class, null, e);
        }
    }
}
