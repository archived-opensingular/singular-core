package br.net.mirante.singular.form.wicket;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class UIBuilderWicket {

    public static FormComponent<?> createForEdit(MInstancia instancia) {

        IWicketComponentMapper mapper = instancia.getValorAtributo(MPacoteWicket.ATR_MAPPER);
        if (mapper == null) {
            throw createErro(instancia, "Não há mappeamento de componente Wicket para o tipo");
        }

        WicketBuildContext ctx = new WicketBuildContext();
        FormComponent<?> cmp = mapper.create(instancia, ctx);
        if (cmp == null) {
            throw createErro(instancia, "O mappeador de interface " + mapper.getClass().getName() + " retornou um null");
        }
        if (cmp.getLabel() == null) {
            String label = StringUtils.trimToNull(instancia.as(AtrBasic.class).getLabel());
            if (label != null) {
                cmp.setLabel(Model.of(label));
            }
        }
        return cmp;
    }

    private static RuntimeException createErro(MInstancia instancia, String msg) {
        return new RuntimeException(
                msg + " (instancia=" + instancia.getCaminhoCompleto() + " tipo=" + instancia.getMTipo().getNome() + ")");
    }

    static class StringMapper implements IWicketComponentMapper {
        @Override
        public FormComponent<?> create(MInstancia instancia, WicketBuildContext ctx) {
            boolean multiLinha = instancia.as(AtrBasic.class).isMultiLinha();
            if (multiLinha) {
                return null;
            } else {
                return null;
            }
        }
    }

    static class IntegerMapper implements IWicketComponentMapper {
        @Override
        public FormComponent<?> create(MInstancia instancia, WicketBuildContext ctx) {
            return null;
        }
    }
}
