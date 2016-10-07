/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.apache.wicket.util.string.StringValue;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.form.SPackage;
import org.opensingular.singular.form.showcase.component.form.xsd.XsdCaseSimple;
import org.opensingular.singular.form.showcase.component.form.xsd.XsdCaseSimple2;
import org.opensingular.lib.wicket.util.resource.Icone;

@Service
public class ShowCaseTable {

    private final Map<String, ShowCaseGroup> formGroups = new LinkedHashMap<>();
    private final Map<String, ShowCaseGroup> studioGroups = new LinkedHashMap<>();

    private final Map<Group, List<Class<?>>> casePorGrupo = new LinkedHashMap<>();

    public ShowCaseTable() {

        Reflections reflections = new Reflections("org.opensingular.singular.showcase.component");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(CaseItem.class);
        for (Class<?> aClass : annotated) {


            final CaseItem annotation = aClass.getAnnotation(CaseItem.class);

            List<Class<?>> classes = casePorGrupo.get(annotation.group());
            if (classes == null) {
                classes = new ArrayList<>();
            }
            classes.add(aClass);
            casePorGrupo.put(annotation.group(), classes);


        }

        // @formatter:off
        addGroup(Group.INPUT);
        addGroup(Group.FILE);
        addGroup(Group.LAYOUT);
        addGroup(Group.VALIDATION);
        addGroup(Group.INTERACTION);
        addGroup(Group.CUSTOM);
        addGroup(Group.MAPS);

        addGroup("XSD", Icone.CODE, ShowCaseType.FORM)
            .addCase(new XsdCaseSimple())
            .addCase(new XsdCaseSimple2());

        addGroup(Group.STUDIO_SAMPLES);
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

        final List<Class<?>> classes = casePorGrupo.get(groupEnum);
        if (classes != null) {
            for (Class<?> caseClass : classes) {
                final CaseItem caseItem = caseClass.getAnnotation(CaseItem.class);
                CaseBase caseBase = null;
                if (SPackage.class.isAssignableFrom(caseClass)) {
                    caseBase = new CaseBaseForm(caseClass, caseItem.componentName(), caseItem.subCaseName(), caseItem.annotation());
//                } else if (CollectionDefinition.class.isAssignableFrom(caseClass)) {
//                    caseBase = new CaseBaseStudio(caseClass, caseItem.componentName(), caseItem.subCaseName(), caseItem.annotation());
//                } else {
//                    throw new RuntimeException("Apenas classes do tipo " + SPackage.class.getName() + " e " + CollectionDefinition.class.getName() + " podem ser anotadas com @" + CaseItem.class.getName());
                }

                if (!caseItem.customizer().isInterface()) {
                    createInstance(caseItem).customize(caseBase);
                }
                for (Resource resource : caseItem.resources()) {
                    Optional<ResourceRef> resourceRef;
                    if (resource.extension().isEmpty()) {
                        resourceRef = ResourceRef.forSource(resource.value());
                    } else {
                        resourceRef = ResourceRef.forClassWithExtension(resource.value(), resource.extension());
                    }
                    if (resourceRef.isPresent()) {
                        caseBase.getAditionalSources().add(resourceRef.get());
                    }
                }
                group.addCase(caseBase);
            }
        }

    }

    private CaseCustomizer createInstance(CaseItem caseItem) {
        try {
            return caseItem.customizer().newInstance();
        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }

        return null;
    }

    private ShowCaseGroup addGroup(String groupName, Icone icon, ShowCaseType tipo) {
        Map<String, ShowCaseGroup> groups;
        if (ShowCaseType.FORM.equals(tipo)) {
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
        if (tipoValue.isNull() || ShowCaseType.FORM.toString().equals(tipoValue.toString())) {
            return formGroups.values();
        } else if (ShowCaseType.STUDIO.toString().equals(tipoValue.toString())) {
            return studioGroups.values();
        } else {
            return Collections.emptyList();
        }

    }

    public static class ShowCaseGroup implements Serializable {

        private final String groupName;
        private final Icone icon;
        private final ShowCaseType tipo;

        private final Map<String, ShowCaseItem> itens = new TreeMap<>();

        public ShowCaseGroup(String groupName, Icone icon, ShowCaseType tipo) {
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
                throw SingularUtil.propagate(e);
            }
        }

        private ShowCaseGroup addCase(CaseBase c) {
            ShowCaseItem item = itens.get(c.getComponentName());
            if (item == null) {
                item = new ShowCaseItem(c.getComponentName(), c.getShowCaseType());
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

        public ShowCaseType getTipo() {
            return tipo;
        }
    }

    public static class ShowCaseItem implements Serializable {

        private final String componentName;

        private final List<CaseBase> cases = new ArrayList<>();
        private ShowCaseType showCaseType;

        public ShowCaseItem(String componentName, ShowCaseType showCaseType) {
            this.componentName = componentName;
            this.showCaseType = showCaseType;
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

        public ShowCaseType getShowCaseType() {
            return showCaseType;
        }
    }
}
