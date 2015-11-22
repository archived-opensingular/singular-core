package br.net.mirante.singular.showcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Throwables;

import br.net.mirante.singular.showcase.input.core.CaseInputCoreAttachment;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreDate;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreInteger;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreMultiSelectCheckbox;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreMultiSelectCombo;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreMultiSelectDefault;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreMultiSelectPickList;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreSelectComboRadio;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreSelectDefault;
import br.net.mirante.singular.showcase.input.core.CaseInputCoreSelectOtherTypes;

public class ShowCaseTable {

    private final Map<String, ShowCaseGroup> groups = new LinkedHashMap<>();

    public ShowCaseTable() {

        // @formatter:off
        group("Input")
            .addCase(CaseInputCoreDate.class)
            .addCase(CaseInputCoreInteger.class)
            .addCase(CaseInputCoreSelectComboRadio.class)
            .addCase(CaseInputCoreSelectDefault.class)
            .addCase(CaseInputCoreSelectOtherTypes.class)
            .addCase(CaseInputCoreMultiSelectCombo.class)
            .addCase(CaseInputCoreMultiSelectCheckbox.class)
            .addCase(CaseInputCoreMultiSelectPickList.class)
            .addCase(CaseInputCoreMultiSelectDefault.class)
            .addCase(CaseInputCoreAttachment.class)
        ;
        //@formatter:on
    }

    private ShowCaseGroup group(String groupName) {
        ShowCaseGroup group = groups.get(groupName);
        if (group == null) {
            group = new ShowCaseGroup(groupName);
            groups.put(groupName, group);
        }
        return group;
    }

    public Collection<ShowCaseGroup> getGroups() {
        return groups.values();
    }

    public static class ShowCaseGroup {

        private final String groupName;

        private final Map<String, ShowCaseItem> itens = new TreeMap<>();

        public ShowCaseGroup(String groupName) {
            this.groupName = groupName;
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
    }

    public static class ShowCaseItem {

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
