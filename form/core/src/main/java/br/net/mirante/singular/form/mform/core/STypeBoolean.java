package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.view.MBooleanRadioView;

@MInfoTipo(nome = "Boolean", pacote = SPackageCore.class)
public class STypeBoolean extends STypeSimple<SIBoolean, Boolean> {

    public STypeBoolean() {
        super(SIBoolean.class, Boolean.class);
    }

    protected STypeBoolean(Class<? extends SIBoolean> classeInstancia) {
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
    @Override
    public STypeBoolean withRadioView() {
        withSelectionFromProvider(newBooleanProvider("Sim", "Não"));
        return (STypeBoolean) super.withView(MBooleanRadioView::new);
    }

    private MOptionsProvider newBooleanProvider(final String yesLabel, final String noLabel) {
        return new MOptionsProvider() {
            @Override
            public SList<? extends SInstance> listOptions(SInstance optionsInstance) {
                STypeBoolean type = getDictionary().getType(STypeBoolean.class);
                SList<?> r = type.novaLista();
                r.addElement(SIBoolean(type, true, yesLabel));
                r.addElement(SIBoolean(type, false, noLabel));
                return r;
            }

            private SIBoolean SIBoolean(STypeBoolean type, boolean value, String label) {
                SIBoolean e = type.novaInstancia();
                e.setValue(value);
                e.setSelectLabel(label);
                return e;
            }
        };
    }

    /**
     * Configura o tipo para utilizar a view {@link MBooleanRadioView}
     */
    public STypeBoolean withRadioView(String labelTrue, String labelFalse) {
        withSelectionFromProvider(newBooleanProvider(labelTrue, labelFalse));
        MBooleanRadioView v = new MBooleanRadioView();
        v.labelFalse(labelFalse);
        v.labelTrue(labelTrue);
        return (STypeBoolean) super.withView(v);
    }
}
