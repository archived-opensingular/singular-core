package br.net.mirante.singular.form.wicket;

import java.util.Collection;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.collect.ImmutableMap;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSFormHorizontal;

public class UIBuilderWicket {

    private static final ImmutableMap<Class<?>, Supplier<IWicketComponentMapper>> MAPPERS = ImmutableMap.<Class<?>, Supplier<IWicketComponentMapper>> builder()
                                                                                              .put(MTipoInteger.class, IntegerMapper::new)
                                                                                              .put(MTipoString.class, StringMapper::new)
                                                                                              .put(MTipoComposto.class, CompostoMapper::new)
                                                                                              .build();

    public static Component createForEdit(String componentId, WicketBuildContext ctx, IModel<? extends MInstancia> model) {

        Object obj = model.getObject();
        MInstancia instancia = (MInstancia) obj;
        IWicketComponentMapper mapper = getMapper(instancia);
        if (mapper == null) {
            throw createErro(instancia, "Não há mappeamento de componente Wicket para o tipo");
        }

        Component cmp = mapper.create(componentId, ctx, model);
        if (cmp == null) {
            throw createErro(instancia, "O mappeador de interface " + mapper.getClass().getName() + " retornou um null");
        }
        if (cmp instanceof LabeledWebMarkupContainer) {
            final LabeledWebMarkupContainer fc = (LabeledWebMarkupContainer) cmp;
            if (fc.getLabel() == null) {
                String label = StringUtils.trimToNull(instancia.as(MPacoteBasic.aspect()).getLabel());
                if (label != null) {
                    fc.setLabel(Model.of(label));
                }
            }
        }
        return cmp;
    }

    private static IWicketComponentMapper getMapper(MInstancia instancia) {
        //        if (instancia.getMTipo() instanceof MTipoSimples<?, ?>) {
        //            MProviderOpcoes providerOpcoes = ((MTipoSimples<?, ?>) instancia.getMTipo()).getProviderOpcoes();
        //        }
        return MAPPERS.get(instancia.getMTipo().getClass()).get();
    }

    private static RuntimeException createErro(MInstancia instancia, String msg) {
        return new RuntimeException(
            msg + " (instancia=" + instancia.getCaminhoCompleto() + " tipo=" + instancia.getMTipo().getNome() + ")");
    }

    static class StringMapper implements IWicketComponentMapper {
        @Override
        public FormComponent<?> create(String componentId, WicketBuildContext ctx, IModel<? extends MInstancia> model) {
            boolean multiLinha = model.getObject().as(AtrBasic.class).isMultiLinha();
            if (multiLinha) {
                return null;
            } else {
                return new TextField<>(componentId, new MInstanciaValorModel<>(model), String.class);
            }
        }
    }

    static class IntegerMapper implements IWicketComponentMapper {
        @Override
        public FormComponent<?> create(String componentId, WicketBuildContext ctx, IModel<? extends MInstancia> model) {
            return new TextField<>(componentId, new MInstanciaValorModel<>(model), Integer.class);
        }
    }

    static class CompostoMapper implements IWicketComponentMapper {
        @Override
        public Component create(String componentId, WicketBuildContext ctx, IModel<? extends MInstancia> model) {
            MInstancia instancia = model.getObject();
            MIComposto composto = (MIComposto) instancia;

            BSFormHorizontal layout = new BSFormHorizontal(componentId);

            Collection<MInstancia> campos = composto.getCampos();
            for (MInstancia campo : campos) {
                final String label = StringUtils.defaultString(
                    campo.getValorAtributo(MPacoteBasic.ATR_LABEL),
                    campo.getMTipo().getNomeSimples());

                layout.appendGroupLabelControlsFeedback(4, label, 8, controlsId ->
                    new BSControls(controlsId)
                        .appendInputText(createForEdit(campo.getNome(), ctx, new MInstanciaCampoModel<>(model, campo.getNome()))));
            }
            return layout;
        }
    }
}
