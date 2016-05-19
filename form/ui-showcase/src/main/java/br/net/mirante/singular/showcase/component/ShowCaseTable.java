/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.google.common.base.Throwables;

import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreBasic;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreBoolean;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreDate;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreDateTime;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreDecimal;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreInteger;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreMoney;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreString;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreTextArea;
import br.net.mirante.singular.showcase.component.form.core.CaseInputCoreYearMonth;
import br.net.mirante.singular.showcase.component.form.core.multiselect.CaseInputCoreMultiSelectCheckbox;
import br.net.mirante.singular.showcase.component.form.core.multiselect.CaseInputCoreMultiSelectCombo;
import br.net.mirante.singular.showcase.component.form.core.multiselect.CaseInputCoreMultiSelectComposite;
import br.net.mirante.singular.showcase.component.form.core.multiselect.CaseInputCoreMultiSelectDefault;
import br.net.mirante.singular.showcase.component.form.core.multiselect.CaseInputCoreMultiSelectPickList;
import br.net.mirante.singular.showcase.component.form.core.multiselect.CaseInputCoreMultiSelectProvider;
import br.net.mirante.singular.showcase.component.form.core.search.CaseInputModalSearch;
import br.net.mirante.singular.showcase.component.form.core.search.CaseLazyInputModalSearch;
import br.net.mirante.singular.showcase.component.form.core.select.CaseInputCoreSelectComboAutoComplete;
import br.net.mirante.singular.showcase.component.form.core.select.CaseInputCoreSelectComboRadio;
import br.net.mirante.singular.showcase.component.form.core.select.CaseInputCoreSelectComposite;
import br.net.mirante.singular.showcase.component.form.core.select.CaseInputCoreSelectCompositePojo;
import br.net.mirante.singular.showcase.component.form.core.select.CaseInputCoreSelectDefault;
import br.net.mirante.singular.showcase.component.form.core.select.CaseInputCoreSelectProvider;
import br.net.mirante.singular.showcase.component.form.custom.CaseCustomStringMapper;
import br.net.mirante.singular.showcase.component.form.custom.CaseCustonRangeMapper;
import br.net.mirante.singular.showcase.component.form.custom.comment.CaseAnnotation;
import br.net.mirante.singular.showcase.component.form.file.CaseFileAttachment;
import br.net.mirante.singular.showcase.component.form.file.CaseFileMultipleAttachments;
import br.net.mirante.singular.showcase.component.form.interaction.CaseInitListener;
import br.net.mirante.singular.showcase.component.form.interaction.CaseInteractionDependsOnOptions;
import br.net.mirante.singular.showcase.component.form.interaction.CaseInteractionEnabled;
import br.net.mirante.singular.showcase.component.form.interaction.CaseInteractionExists;
import br.net.mirante.singular.showcase.component.form.interaction.CaseInteractionRequired;
import br.net.mirante.singular.showcase.component.form.interaction.CaseInteractionVisible;
import br.net.mirante.singular.showcase.component.form.interaction.CaseUpdateListener;
import br.net.mirante.singular.showcase.component.form.layout.CaseComplexListByBreadcrumb;
import br.net.mirante.singular.showcase.component.form.layout.CaseFineTunningGrid;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByBreadcrumb;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByFormDefault;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByFormMinimumAndMaximum;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByMasterDetailButtons;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByMasterDetailColumns;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByMasterDetailDefault;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByMasterDetailMiniumAndMaximum;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByMasterDetailNested;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByTableDefault;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByTableMinimiumAndMaximum;
import br.net.mirante.singular.showcase.component.form.layout.CaseListByTableSimpleType;
import br.net.mirante.singular.showcase.component.form.layout.CaseRowControlGrid;
import br.net.mirante.singular.showcase.component.form.layout.CaseSimpleGrid;
import br.net.mirante.singular.showcase.component.form.layout.CaseTabs;
import br.net.mirante.singular.showcase.component.form.map.CaseGoogleMaps;
import br.net.mirante.singular.showcase.component.form.validation.CaseValidationBetweenFields;
import br.net.mirante.singular.showcase.component.form.validation.CaseValidationCustom;
import br.net.mirante.singular.showcase.component.form.validation.CaseValidationPartial;
import br.net.mirante.singular.showcase.component.form.validation.CaseValidationRequired;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class ShowCaseTable {

    private final Map<String, ShowCaseGroup> groups = new LinkedHashMap<>();

    public ShowCaseTable() {

        // @formatter:off
        group("Input", Icone.PUZZLE)
            .addCase(CaseInputCoreDate.class)
            .addCase(CaseInputCoreYearMonth.class)
            .addCase(CaseInputCoreInteger.class)
            .addCase(CaseInputCoreSelectComboRadio.class)
            .addCase(CaseInputCoreSelectDefault.class)
            .addCase(CaseInputCoreSelectComposite.class)
            .addCase(CaseInputCoreSelectCompositePojo.class)
            .addCase(CaseInputCoreSelectProvider.class)
            .addCase(CaseInputCoreSelectComboAutoComplete.class)
            .addCase(CaseInputCoreMultiSelectDefault.class)
            .addCase(CaseInputCoreMultiSelectCombo.class)
            .addCase(CaseInputCoreMultiSelectCheckbox.class)
            .addCase(CaseInputCoreMultiSelectPickList.class)
            .addCase(CaseInputCoreMultiSelectComposite.class)
            .addCase(CaseInputCoreMultiSelectProvider.class)
            .addCase(CaseInputModalSearch.class)
            .addCase(CaseLazyInputModalSearch.class)
            .addCase(CaseInputCoreBasic.class)
            .addCase(CaseInputCoreBoolean.class)
            .addCase(CaseInputCoreString.class)
            .addCase(CaseInputCoreTextArea.class)
            .addCase(CaseInputCoreDecimal.class)
            .addCase(CaseInputCoreMoney.class)
            .addCase(CaseInputCoreDateTime.class)
        ;
        group("File", Icone.FOLDER)
            .addCase(CaseFileAttachment.class)
            .addCase(CaseFileMultipleAttachments.class)
        ;
        group("Layout", Icone.GRID)
            .addCase(CaseSimpleGrid.class)
            .addCase(CaseFineTunningGrid.class)
            .addCase(CaseListByFormDefault.class)
            .addCase(CaseListByTableDefault.class)
            .addCase(CaseListByMasterDetailDefault.class)
            .addCase(CaseListByMasterDetailColumns.class)
            .addCase(CaseListByMasterDetailButtons.class)
            .addCase(CaseListByMasterDetailNested.class)
            .addCase(CaseListByBreadcrumb.class)
            .addCase(CaseComplexListByBreadcrumb.class)
            .addCase(CaseListByFormMinimumAndMaximum.class)
            .addCase(CaseListByTableMinimiumAndMaximum.class)
            .addCase(CaseListByMasterDetailMiniumAndMaximum.class)
            .addCase(CaseListByTableSimpleType.class)
            .addCase(CaseRowControlGrid.class)
            .addCase(CaseTabs.class)
        ;
        group("Validation", Icone.BAN)
            .addCase(CaseValidationRequired.class)
            .addCase(CaseValidationCustom.class)
            .addCase(CaseValidationBetweenFields.class)
            .addCase(CaseValidationPartial.class);
        group("Interaction", Icone.ROCKET)
            .addCase(CaseInteractionExists.class)
            .addCase(CaseInteractionEnabled.class)
            .addCase(CaseInteractionVisible.class)
            .addCase(CaseInteractionRequired.class)
            .addCase(CaseInteractionDependsOnOptions.class)
            .addCase(CaseInitListener.class)
            .addCase(CaseUpdateListener.class)
        ;
        group("Custom", Icone.WRENCH)
                .addCase(CaseCustomStringMapper.class)
                .addCase(CaseCustonRangeMapper.class)
                .addCase(CaseAnnotation.class)
        ;
        group("Maps", Icone.MAP)
                .addCase(CaseGoogleMaps.class)
        ;
        //@formatter:on
    }

    public ShowCaseItem findCaseItemByComponentName(String name) {
        final ShowCaseItem[] showCaseItem = new ShowCaseItem[1];
        getGroups().stream().forEach(i -> {
            Optional<ShowCaseItem> op = i.getItens().stream()
                .filter(f -> name.equalsIgnoreCase(f.getComponentName()))
                .findFirst();
            if (op.isPresent()) {
                showCaseItem[0] = op.get();
            }
        });

        return showCaseItem[0];
    }

    private ShowCaseGroup group(String groupName, Icone icon) {
        ShowCaseGroup group = groups.get(groupName);
        if (group == null) {
            group = new ShowCaseGroup(groupName, icon);
            groups.put(groupName, group);
        }
        return group;
    }

    public Collection<ShowCaseGroup> getGroups() {
        return groups.values();
    }

    public static class ShowCaseGroup implements Serializable {

        private final String groupName;
        private final Icone  icon;

        private final Map<String, ShowCaseItem> itens = new TreeMap<>();

        public ShowCaseGroup(String groupName, Icone icon) {
            this.groupName = groupName;
            this.icon = icon;
        }

        public String getGroupName() {
            return groupName;
        }

        public <T extends CaseBase> ShowCaseGroup addCase(Class<T> classCase) {
            try {
                return addCase(classCase.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        }

        private ShowCaseGroup addCase(CaseBase c) {
            ShowCaseItem item = itens.get(c.getComponentName());
            if (item == null) {
                item = new ShowCaseItem(c.getComponentName());
                itens.put(c.getComponentName(), item);
            }
            item.addCase(c);
            return this;
        }

        public Collection<ShowCaseItem> getItens() {
            return itens.values();
        }

        public Icone getIcon() {
            return icon;
        }
    }

    public static class ShowCaseItem implements Serializable {

        private final String componentName;

        private final List<CaseBase> cases = new ArrayList<>();

        public ShowCaseItem(String componentName) {
            this.componentName = componentName;
        }

        public String getComponentName() {
            return componentName;
        }

        public void addCase(CaseBase c) {
            cases.add(c);
        }

        public List<CaseBase> getCases() {
            return cases;
        }
    }
}
