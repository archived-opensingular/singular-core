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

import br.net.mirante.singular.showcase.component.custom.CaseCustomStringMapper;
import br.net.mirante.singular.showcase.component.custom.CaseCustonRangeMapper;
import br.net.mirante.singular.showcase.component.file.CaseFileAttachment;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreBasic;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreBoolean;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreDate;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreDecimal;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreInteger;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreMonetario;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreString;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreTextArea;
import br.net.mirante.singular.showcase.component.input.core.CaseInputCoreYearMonth;
import br.net.mirante.singular.showcase.component.input.core.multiselect.CaseInputCoreMultiSelectCheckbox;
import br.net.mirante.singular.showcase.component.input.core.multiselect.CaseInputCoreMultiSelectCombo;
import br.net.mirante.singular.showcase.component.input.core.multiselect.CaseInputCoreMultiSelectComposite;
import br.net.mirante.singular.showcase.component.input.core.multiselect.CaseInputCoreMultiSelectDefault;
import br.net.mirante.singular.showcase.component.input.core.multiselect.CaseInputCoreMultiSelectPickList;
import br.net.mirante.singular.showcase.component.input.core.multiselect.CaseInputCoreMultiSelectProvider;
import br.net.mirante.singular.showcase.component.input.core.select.CaseInputCoreSelectComboRadio;
import br.net.mirante.singular.showcase.component.input.core.select.CaseInputCoreSelectComposite;
import br.net.mirante.singular.showcase.component.input.core.select.CaseInputCoreSelectDefault;
import br.net.mirante.singular.showcase.component.input.core.select.CaseInputCoreSelectOtherTypes;
import br.net.mirante.singular.showcase.component.input.core.select.CaseInputCoreSelectProvider;
import br.net.mirante.singular.showcase.component.input.core.select.CaseInputCoreSelectSearch;
import br.net.mirante.singular.showcase.component.interaction.CaseInteractionDependsOnOptions;
import br.net.mirante.singular.showcase.component.interaction.CaseInteractionEnabled;
import br.net.mirante.singular.showcase.component.interaction.CaseInteractionExists;
import br.net.mirante.singular.showcase.component.interaction.CaseInteractionRequired;
import br.net.mirante.singular.showcase.component.interaction.CaseInteractionVisible;
import br.net.mirante.singular.showcase.component.layout.CaseGrid;
import br.net.mirante.singular.showcase.component.layout.CaseGridList;
import br.net.mirante.singular.showcase.component.layout.CaseGridTable;
import br.net.mirante.singular.showcase.component.layout.CaseMasterDetail;
import br.net.mirante.singular.showcase.component.layout.CaseMasterDetailButtons;
import br.net.mirante.singular.showcase.component.layout.CaseMasterDetailColumns;
import br.net.mirante.singular.showcase.component.layout.CaseMasterDetailNested;
import br.net.mirante.singular.showcase.component.map.CaseGoogleMaps;
import br.net.mirante.singular.showcase.component.validation.CaseValidationBetweenFields;
import br.net.mirante.singular.showcase.component.validation.CaseValidationCustom;
import br.net.mirante.singular.showcase.component.validation.CaseValidationPartial;
import br.net.mirante.singular.showcase.component.validation.CaseValidationRequired;
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
            .addCase(CaseInputCoreSelectOtherTypes.class)
            .addCase(CaseInputCoreSelectComposite.class)
            .addCase(CaseInputCoreSelectProvider.class)
            .addCase(CaseInputCoreMultiSelectDefault.class)
            .addCase(CaseInputCoreMultiSelectCombo.class)
            .addCase(CaseInputCoreMultiSelectCheckbox.class)
            .addCase(CaseInputCoreMultiSelectPickList.class)
            .addCase(CaseInputCoreMultiSelectComposite.class)
            .addCase(CaseInputCoreMultiSelectProvider.class)
            .addCase(CaseInputCoreSelectSearch.class)
            .addCase(CaseInputCoreBasic.class)
            .addCase(CaseInputCoreBoolean.class)
            .addCase(CaseInputCoreString.class)
            .addCase(CaseInputCoreTextArea.class)
            .addCase(CaseInputCoreDecimal.class)
            .addCase(CaseInputCoreMonetario.class)
        ;
        group("File", Icone.FOLDER)
            .addCase(CaseFileAttachment.class)
        ;
        group("Layout", Icone.GRID)
            .addCase(CaseGrid.class)
            .addCase(CaseGridList.class)
            .addCase(CaseGridTable.class)
            .addCase(CaseMasterDetail.class)
            .addCase(CaseMasterDetailColumns.class)
            .addCase(CaseMasterDetailButtons.class)
            .addCase(CaseMasterDetailNested.class)
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
        ;
        group("Custom", Icone.WRENCH)
                .addCase(CaseCustomStringMapper.class)
                .addCase(CaseCustonRangeMapper.class)
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
