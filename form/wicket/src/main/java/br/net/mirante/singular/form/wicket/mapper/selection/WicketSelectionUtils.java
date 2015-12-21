package br.net.mirante.singular.form.wicket.mapper.selection;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.stream.Collectors;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.options.SelectionableInstance;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableType;

public class WicketSelectionUtils {
    @SuppressWarnings("rawtypes")
    public static List<SelectOption> createOptions(IModel<? extends MInstancia> model, MTipo<?> type) {
        if(type instanceof MSelectionableType ){
            MOptionsProvider provider = ((MSelectionableType) type).getProviderOpcoes();
            if(provider != null){
                if (type instanceof MTipoSimples ){
                    return createStringOptions(model, provider);
                }else{
                    return createSelectOptions(model, provider);
                }
            }
        }
        return newArrayList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<SelectOption> createStringOptions(IModel<? extends MInstancia> model, MOptionsProvider provider) {
        MInstancia instance = model.getObject();
        MILista<? extends MInstancia> options = provider.listAvailableOptions(instance);
        return options.getValor().stream()
                .map((x) -> new SelectOption(x.toString(), x))
                .collect(Collectors.toList());
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List<SelectOption> createSelectOptions(IModel<? extends MInstancia> model, MOptionsProvider provider) {
        List<SelectOption> opcoesValue;
        MILista<MIComposto> rawOptions = (MILista<MIComposto>) provider.listAvailableOptions(model.getObject());
        opcoesValue = rawOptions.getValores().stream()
                .map((o) -> {
                    SelectionableInstance x = (SelectionableInstance) o;
                    return new SelectOption<>(x.getFieldId(), x.getFieldValue(), (MInstancia) x);
                }
                ).collect(Collectors.toList());
        return opcoesValue;
    }
}
