package br.net.mirante.singular.form.mform.core;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.basic.view.MBooleanRadioView;

@MInfoTipo(nome = "Boolean", pacote = MPacoteCore.class)
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
    
    /**
     * Configura o tipo para utilizar a view {@link MBooleanRadioView}
     */
    public MTipo<MIBoolean> withRadioView() {
        return super.withView(MBooleanRadioView::new);
    }
}
