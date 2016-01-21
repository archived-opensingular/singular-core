package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.form.mform.options.MSelectionableType;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class WicketSelectionUtils {
    @SuppressWarnings("rawtypes")
    public static List<SelectOption> createOptions(IModel<? extends MInstancia> model, MTipo<?> type) {
        if (type instanceof MSelectionableType) {
            MOptionsProvider provider = ((MSelectionableType) type).getProviderOpcoes();
            if (provider != null) {
                return createSelectOptions(model, provider);
            }
        }
        return newArrayList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<SelectOption> createSelectOptions(IModel<? extends MInstancia> model, MOptionsProvider provider) {
        List<SelectOption> opcoesValue;
        MILista<MInstancia> rawOptions = (MILista<MInstancia>) provider.listAvailableOptions(model.getObject());
        opcoesValue = rawOptions
                .getValores()
                .stream()
                .map(o -> newSelectionOption((MSelectionableInstance)o))
                .collect(Collectors.toList());
        return opcoesValue;
    }

    private static SelectOption newSelectionOption(MSelectionableInstance selectionableInstance) {
        return new SelectOption(selectionableInstance.getSelectLabel(), (MInstancia) selectionableInstance);
    }
}
