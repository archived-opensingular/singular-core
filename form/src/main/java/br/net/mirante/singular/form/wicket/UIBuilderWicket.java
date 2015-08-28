package br.net.mirante.singular.form.wicket;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MProviderOpcoes;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
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

    public static Component createForEdit(String componentId, WicketBuildContext ctx, MInstancia instancia) {

        IWicketComponentMapper mapper = getMapper(instancia);
        if (mapper == null) {
            throw createErro(instancia, "Não há mappeamento de componente Wicket para o tipo");
        }

        Component cmp = mapper.create(componentId, ctx, instancia);
        if (cmp == null) {
            throw createErro(instancia, "O mappeador de interface " + mapper.getClass().getName() + " retornou um null");
        }
        cmp.setDefaultModel(new MInstanciaModel<>(instancia));
        if (cmp instanceof LabeledWebMarkupContainer) {
            final LabeledWebMarkupContainer fc = (LabeledWebMarkupContainer) cmp;
            if (fc.getLabel() == null) {
                String label = StringUtils.trimToNull(instancia.as(AtrBasic.class).getLabel());
                if (label != null) {
                    fc.setLabel(Model.of(label));
                }
            }
        }
        return cmp;
    }

    private static IWicketComponentMapper getMapper(MInstancia instancia) {
        if (instancia.getMTipo() instanceof MTipoSimples<?, ?>) {
            MProviderOpcoes providerOpcoes = ((MTipoSimples<?, ?>) instancia.getMTipo()).getProviderOpcoes();
        }
        return MAPPERS.get(instancia.getMTipo().getClass()).get();
    }

    private static RuntimeException createErro(MInstancia instancia, String msg) {
        return new RuntimeException(
            msg + " (instancia=" + instancia.getCaminhoCompleto() + " tipo=" + instancia.getMTipo().getNome() + ")");
    }

    static class SelectMapper implements IWicketComponentMapper {
        @Override
        public FormComponent<?> create(String componentId, WicketBuildContext ctx, MInstancia instancia) {
            return new DropDownChoice<MInstancia>(componentId)
                .setChoices(new AbstractReadOnlyModel<List<? extends MInstancia>>() {
                    @Override
                    public List<? extends MInstancia> getObject() {
                        MTipoSimples<?, ?> mtsimples = (MTipoSimples<?, ?>) instancia.getMTipo();
                        MProviderOpcoes provider = mtsimples.getProviderOpcoes();
                        MILista<? extends MInstancia> milista = provider.getOpcoes();
                        return Lists.newArrayList(milista.iterator());
                    }
                })
                .setChoiceRenderer(new IChoiceRenderer<MInstancia>() {
                    @Override
                    public Object getDisplayValue(MInstancia object) {
                        return object.getDisplayString();
                    }
                    @Override
                    public String getIdValue(MInstancia object, int index) {
                        return "" + index;
                    }
                    @Override
                    public MInstancia getObject(String id, IModel<? extends List<? extends MInstancia>> choices) {
                        return choices.getObject().get(Integer.parseInt(id));
                    }
                });
        }
    }

    static class StringMapper implements IWicketComponentMapper {
        @Override
        public FormComponent<?> create(String componentId, WicketBuildContext ctx, MInstancia instancia) {
            boolean multiLinha = instancia.as(AtrBasic.class).isMultiLinha();
            if (multiLinha) {
                return null;
            } else {
                return new TextField<>(componentId, String.class);
            }
        }
    }

    static class IntegerMapper implements IWicketComponentMapper {
        @Override
        public FormComponent<?> create(String componentId, WicketBuildContext ctx, MInstancia instancia) {
            return new TextField<>(componentId, Integer.class);
        }
    }

    static class CompostoMapper implements IWicketComponentMapper {
        @Override
        public Component create(String componentId, WicketBuildContext ctx, MInstancia instancia) {
            MIComposto composto = (MIComposto) instancia;

            BSFormHorizontal layout = new BSFormHorizontal(componentId);

            Collection<MInstancia> campos = composto.getCampos();
            for (MInstancia campo : campos) {
                final String label = StringUtils.defaultString(
                    campo.getValorAtributo(MPacoteBasic.ATR_LABEL),
                    campo.getMTipo().getNomeSimples());

                layout.appendGroupLabelControlsFeedback(4, label, 8, controlsId ->
                    new BSControls(controlsId)
                        .appendInputText(createForEdit(campo.getNome(), ctx, campo)));
            }
            return layout;
        }
    }
}
