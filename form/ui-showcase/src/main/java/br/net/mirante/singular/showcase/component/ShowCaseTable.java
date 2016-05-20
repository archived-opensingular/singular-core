/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.wicket.util.string.StringValue;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;

import br.net.mirante.singular.form.SPackage;
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
import br.net.mirante.singular.showcase.view.page.form.ListPage;
import br.net.mirante.singular.util.wicket.resource.Icone;

@Service
public class ShowCaseTable {

    private final Map<String, ShowCaseGroup> formGroups = new LinkedHashMap<>();
    private final Map<String, ShowCaseGroup> studioGroups = new LinkedHashMap<>();

    private final Map<Group, List<Class<? extends SPackage>>> casePorGrupo = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public ShowCaseTable() {

        Reflections reflections = new Reflections("br.net.mirante.singular.showcase.component.form");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(CaseItem.class);
        for (Class<?> aClass : annotated) {
            if (SPackage.class.isAssignableFrom(aClass)) {
                Class<? extends SPackage> sPackage = (Class<? extends SPackage>) aClass;
                final CaseItem annotation = aClass.getAnnotation(CaseItem.class);

                List<Class<? extends SPackage>> classes = casePorGrupo.get(annotation.group());
                if (classes == null) {
                    classes = new ArrayList<>();
                }
                classes.add(sPackage);
                casePorGrupo.put(annotation.group(), classes);
            }

        }

        // @formatter:off
        addGroup(Group.INPUT);

        addGroup("File", Icone.FOLDER, ListPage.Tipo.FORM)
            .addCase(CaseFileAttachment.class)
            .addCase(CaseFileMultipleAttachments.class)
        ;
        addGroup("Layout", Icone.GRID, ListPage.Tipo.FORM)
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
        addGroup("Validation", Icone.BAN, ListPage.Tipo.FORM)
            .addCase(CaseValidationRequired.class)
            .addCase(CaseValidationCustom.class)
            .addCase(CaseValidationBetweenFields.class)
            .addCase(CaseValidationPartial.class);
        addGroup("Interaction", Icone.ROCKET, ListPage.Tipo.FORM)
            .addCase(CaseInteractionExists.class)
            .addCase(CaseInteractionEnabled.class)
            .addCase(CaseInteractionVisible.class)
            .addCase(CaseInteractionRequired.class)
            .addCase(CaseInteractionDependsOnOptions.class)
            .addCase(CaseInitListener.class)
            .addCase(CaseUpdateListener.class)
        ;
        addGroup("Custom", Icone.WRENCH, ListPage.Tipo.FORM)
                .addCase(CaseCustomStringMapper.class)
                .addCase(CaseCustonRangeMapper.class)
                .addCase(CaseAnnotation.class)
        ;
        addGroup("Maps", Icone.MAP, ListPage.Tipo.FORM)
                .addCase(CaseGoogleMaps.class)
        ;

//        addGroup("Input", Icone.PUZZLE, ListPage.Tipo.STUDIO)
//                .addCase(CaseInputCoreDate.class)
//        ;
        //@formatter:on
    }

    public ShowCaseItem findCaseItemByComponentName(String name) {
        return getGroups().stream()
                .map(ShowCaseGroup::getItens)
                .flatMap(Collection::stream)
                .filter(f -> name.equalsIgnoreCase(f.getComponentName()))
                .findFirst().orElse(null);
    }

    private void addGroup(Group groupEnum) {
        final ShowCaseGroup group = addGroup(groupEnum.getName(), groupEnum.getIcone(), groupEnum.getTipo());

        final List<Class<? extends SPackage>> classes = casePorGrupo.get(groupEnum);
        if (classes != null) {
            for (Class<? extends SPackage> packageClass : classes) {
                final CaseItem annotation = packageClass.getAnnotation(CaseItem.class);
                final CaseBase caseBase = new CaseBase(packageClass, annotation.componentName(), annotation.subCaseName());
                for (Class resource : annotation.resources()) {
                    caseBase.getAditionalSources().add(ResourceRef.forSource(resource).orElse(null));
                }
                group.addCase(caseBase);
            }
        }

    }

    private ShowCaseGroup addGroup(String groupName, Icone icon, ListPage.Tipo tipo) {
        Map<String, ShowCaseGroup> groups;
        if (ListPage.Tipo.FORM.equals(tipo)) {
            groups = formGroups;
        } else {
            groups = studioGroups;
        }
        
        ShowCaseGroup group = groups.get(groupName);
        if (group == null) {
            group = new ShowCaseGroup(groupName, icon, tipo);
            groups.put(groupName, group);
        }

        return group;
    }

    public Collection<ShowCaseGroup> getGroups() {
        final List<ShowCaseGroup> groups = new ArrayList<>(formGroups.values());
        groups.addAll(studioGroups.values());
        return groups;
    }

    public Collection<ShowCaseGroup> getGroups(StringValue tipoValue) {
        if (tipoValue.isNull() || ListPage.Tipo.FORM.toString().equals(tipoValue.toString())) {
            return formGroups.values();
        } else if (ListPage.Tipo.STUDIO.toString().equals(tipoValue.toString())) {
            return studioGroups.values();
        } else {
            return Collections.emptyList();
        }

    }

    public static class ShowCaseGroup implements Serializable {

        private final String groupName;
        private final Icone  icon;
        private final ListPage.Tipo tipo;

        private final Map<String, ShowCaseItem> itens = new TreeMap<>();

        public ShowCaseGroup(String groupName, Icone icon, ListPage.Tipo tipo) {
            this.groupName = groupName;
            this.icon = icon;
            this.tipo = tipo;
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

        public ListPage.Tipo getTipo() {
            return tipo;
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
