package br.net.mirante.singular.showcase;

import br.net.mirante.singular.showcase.file.CaseFileAttachment;
import br.net.mirante.singular.showcase.input.core.*;
import br.net.mirante.singular.showcase.layout.CaseGrid;
import br.net.mirante.singular.showcase.layout.CaseGridList;
import br.net.mirante.singular.showcase.layout.CaseGridTable;
import br.net.mirante.singular.showcase.validation.CaseValidationCustom;
import br.net.mirante.singular.showcase.validation.CaseValidationRequired;
import br.net.mirante.singular.util.wicket.resource.Icone;
import com.google.common.base.Throwables;

import java.io.Serializable;
import java.util.*;

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
            .addCase(CaseInputCoreMultiSelectCombo.class)
            .addCase(CaseInputCoreMultiSelectCheckbox.class)
            .addCase(CaseInputCoreMultiSelectPickList.class)
            .addCase(CaseInputCoreMultiSelectDefault.class)
            .addCase(CaseInputCoreSelectSearch.class)
            .addCase(CaseInputCoreBasic.class)
            .addCase(CaseInputCoreBoolean.class);
        group("File", Icone.FOLDER)
                .addCase(CaseFileAttachment.class);
        group("Layout", Icone.GRID)
                .addCase(CaseGrid.class)
                .addCase(CaseGridList.class)
                .addCase(CaseGridTable.class);
        group("Validation", Icone.BAN)
                .addCase(CaseValidationRequired.class)
                .addCase(CaseValidationCustom.class);
        //@formatter:on
    }

    public ShowCaseItem findCaseItemByComponentName(String name){
        final ShowCaseItem[] showCaseItem = new ShowCaseItem[1];
        getGroups().stream().forEach(i -> {
           Optional<ShowCaseItem> op = i.getItens().stream()
                   .filter(f -> name.equalsIgnoreCase(f.getComponentName()))
                   .findFirst();
            if(op.isPresent()){
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
        private final Icone icon;

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
