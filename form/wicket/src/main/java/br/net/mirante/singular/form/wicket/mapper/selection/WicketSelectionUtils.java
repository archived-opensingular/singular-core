/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.options.SSelectionableType;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class WicketSelectionUtils {
    @SuppressWarnings("rawtypes")
    public static List<SelectOption> createOptions(IModel<? extends SInstance> model, SType<?> type) {
        if (type instanceof SSelectionableType) {
            SOptionsProvider provider = ((SSelectionableType) type).getOptionsProvider();
            if (provider != null) {
                return createSelectOptions(model, provider);
            }
        }
        return newArrayList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<SelectOption> createSelectOptions(IModel<? extends SInstance> model, SOptionsProvider provider) {
        List<SelectOption> opcoesValue = new ArrayList<>();
        Map<String, String> optionsMap = model.getObject().getOptionsConfig().listSelectOptions();
        optionsMap.forEach((key, label) -> opcoesValue.add(new SelectOption(label, key)));
        return opcoesValue;
    }

}
