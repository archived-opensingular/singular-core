package br.net.mirante.singular.form.wicket.mapper.selection;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.stream.Collectors;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableType;

public class WicketSelectionUtils {
    @SuppressWarnings("rawtypes")
    public static List<SelectOption> createOptions(IModel<? extends MInstancia> model, MTipo<?> type) {
        if(type instanceof MSelectionableType ){
            MOptionsProvider provider = ((MSelectionableType) type).getProviderOpcoes();
            if(provider != null){
                return createSelectOptions(model, provider);
            }
        }
        return newArrayList();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List<SelectOption> createSelectOptions(IModel<? extends MInstancia> model, MOptionsProvider provider) {
        List<SelectOption> opcoesValue;
        MILista<MInstancia> rawOptions = (MILista<MInstancia>) provider.listAvailableOptions(model.getObject());
        opcoesValue = rawOptions.getValores().stream()
                .map((o) -> {
                    MSelectionableInstance x = (MSelectionableInstance) o;
                    return newSelectionOption(x);
                }
                ).collect(Collectors.toList());
        return opcoesValue;
    }

    private static SelectOption<?> newSelectionOption(MSelectionableInstance selectionableInstance){
        String description = selectionableInstance.getSelectLabel();
        Object value = selectionableInstance.getSelectValue();
        if (description == null){
            description = String.valueOf(value);
        }
        return new SelectOption<>(description, value, (MInstancia) selectionableInstance);
    }
}
