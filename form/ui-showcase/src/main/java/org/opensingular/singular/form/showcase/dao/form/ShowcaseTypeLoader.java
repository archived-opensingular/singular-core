/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.dao.form;

import org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form.STypeCredenciamentoEscolaGoverno;
import org.opensingular.form.exemplos.notificacaosimplificada.form.baixorisco.STypeNotificacaoSimplificadaBaixoRisco;
import org.opensingular.form.exemplos.notificacaosimplificada.form.dinamizado.STypeNotificacaoSimplificadaDinamizado;
import org.opensingular.form.exemplos.notificacaosimplificada.form.gas.STypeNotificacaoSimplificadaGasMedicinal;
import org.opensingular.form.exemplos.notificacaosimplificada.form.habilitacao.STypeHabilitacaoEmpresa;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vegetal.STypeNotificacaoSimplificadaFitoterapico;
import org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.form.STypeGestaoObras;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.singular.form.showcase.component.CaseBaseForm;
import org.opensingular.singular.form.showcase.component.ShowCaseTable;
import org.opensingular.singular.form.showcase.view.page.form.examples.STypeCurriculo;
import org.opensingular.form.spring.SpringTypeLoader;
import org.opensingular.singular.form.showcase.component.CaseBase;
import org.opensingular.singular.form.showcase.component.ShowCaseType;
import org.opensingular.singular.form.showcase.view.page.form.examples.STypeExample;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


public class ShowcaseTypeLoader extends SpringTypeLoader<String> {

    @Inject
    private ShowCaseTable showCaseTable;

    private final Map<String, TemplateEntry> entries = new LinkedHashMap<>();

    public ShowcaseTypeLoader() {
        add(STypeCurriculo.class, ShowCaseType.FORM);
        add(STypeExample.class, ShowCaseType.FORM);
//        add(STypePeticaoGGTOX.class, ShowCaseType.FORM);
        add(STypeNotificacaoSimplificadaDinamizado.class, ShowCaseType.FORM);
        add(STypeNotificacaoSimplificadaGasMedicinal.class, ShowCaseType.FORM);
        add(STypeNotificacaoSimplificadaFitoterapico.class, ShowCaseType.FORM);
        add(STypeNotificacaoSimplificadaBaixoRisco.class, ShowCaseType.FORM);
        add(STypeHabilitacaoEmpresa.class, ShowCaseType.FORM);
        add(STypeCredenciamentoEscolaGoverno.class, ShowCaseType.FORM);
        add(STypeGestaoObras.class, ShowCaseType.FORM);

    }

    @PostConstruct
    private void init() {
        for (ShowCaseTable.ShowCaseGroup group : showCaseTable.getGroups()) {
            for (ShowCaseTable.ShowCaseItem item : group.getItens()) {
                String itemName = group.getGroupName() + " - " + item.getComponentName();
                for (CaseBase cb : item.getCases()) {
                    if (cb instanceof CaseBaseForm) {
                        CaseBaseForm c = (CaseBaseForm) cb;
                        if (StringUtils.isEmpty(c.getSubCaseName())) {
                            add(itemName, c, group.getTipo());
                        } else {
                            add(itemName + " - " + c.getSubCaseName(), c, group.getTipo());
                        }
                    }
                }
            }
        }
    }

    private <TYPE extends SType<?>> void add(Class<TYPE> typeClass, ShowCaseType tipo) {
        Preconditions.checkArgument(typeClass.isAnnotationPresent(SInfoType.class));

        SDictionary d     = SDictionary.create();
        SType<?>    sType = d.getType(typeClass);

        String typeName = sType.getName();
        String simpleName = Optional.ofNullable(sType.asAtr().getLabel())
                .orElseGet(() -> StringUtils.defaultIfBlank(StringUtils.substringAfterLast(typeName, "."), sType.getName()));

        add(sType.getName(), simpleName, () -> {
            SDictionary dictionary = SDictionary.create();
            return dictionary.getType(typeClass);
        }, tipo);
    }

    private void add(Class<? extends SPackage> packageClass, String typeName, ShowCaseType tipo) {
        String simpleName = StringUtils.defaultIfBlank(StringUtils.substringAfterLast(typeName, "."), typeName);
        add(typeName, simpleName, () -> {
            SDictionary d = SDictionary.create();
            d.loadPackage(packageClass);
            return d.getType(typeName);
        }, tipo);
    }

    public void add(String displayName, CaseBaseForm c, ShowCaseType tipo) {
        add(c.getTypeName(), displayName, c::getCaseType, tipo);
    }

    private void add(String typeName, String displayName, Supplier<SType<?>> typeSupplier, ShowCaseType tipo) {
        entries.put(typeName, new TemplateEntry(displayName, typeSupplier, tipo));
    }

    @Override
    protected Optional<SType<?>> loadTypeImpl(String typeName) {
        return Optional.ofNullable(entries.get(typeName)).map(e -> e.getType());
    }

    public Collection<TemplateEntry> getEntries() {
        return entries.values();
    }

    public TemplateEntry findEntryByType(String type) {
        for (TemplateEntry t : entries.values()) {
            if (t.getType().getName().equals(type)) {
                return t;
            }
        }
        return null;
    }

    public ShowCaseTable getShowCaseTable() {
        return showCaseTable;
    }

    public void setShowCaseTable(ShowCaseTable showCaseTable) {
        this.showCaseTable = showCaseTable;
    }

    public static class TemplateEntry {

        private final String             displayName;
        private final Supplier<SType<?>> typeSupplier;
        private final ShowCaseType       tipo;

        public TemplateEntry(String displayName, Supplier<SType<?>> typeSupplier, ShowCaseType tipo) {
            this.displayName = displayName;
            this.typeSupplier = typeSupplier;
            this.tipo = tipo;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SType<?> getType() {
            return typeSupplier.get();
        }

        public ShowCaseType getTipo() {
            return tipo;
        }
    }
}
