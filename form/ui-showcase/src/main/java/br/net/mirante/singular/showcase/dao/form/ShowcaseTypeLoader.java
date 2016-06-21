/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.dao.form;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.*;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import br.net.mirante.singular.showcase.component.CaseBaseForm;
import br.net.mirante.singular.showcase.component.ShowCaseType;
import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado.SPackageNotificacaoSimplificadaDinamizado;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.gas.SPackageNotificacaoSimplificadaGasMedicinal;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.habilitacao.SPackageHabilitacaoEmpresa;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vegetal.SPackageNotificacaoSimplificadaFitoterapico;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.spring.SpringTypeLoader;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ShowCaseTable;
import br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseGroup;
import br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseItem;
import br.net.mirante.singular.showcase.view.page.form.examples.ExamplePackage;
import br.net.mirante.singular.showcase.view.page.form.examples.SPackageCurriculo;
import br.net.mirante.singular.showcase.view.page.form.examples.SPackagePeticaoGGTOX;


public class ShowcaseTypeLoader extends SpringTypeLoader<String> {

    @Inject
    private ShowCaseTable showCaseTable;

    private final Map<String, TemplateEntry> entries = new LinkedHashMap<>();

    public ShowcaseTypeLoader() {
        add(SPackageCurriculo.class, SPackageCurriculo.TIPO_CURRICULO, ShowCaseType.FORM);
        add(ExamplePackage.class, ExamplePackage.Types.ORDER.name, ShowCaseType.FORM);
        add(SPackagePeticaoGGTOX.class, SPackagePeticaoGGTOX.NOME_COMPLETO, ShowCaseType.FORM);
        add(SPackagePeticaoPrimariaSimplificada.class, SPackagePeticaoPrimariaSimplificada.NOME_COMPLETO, ShowCaseType.FORM);
        add(SPackageNotificacaoSimplificadaDinamizado.class, SPackageNotificacaoSimplificadaDinamizado.NOME_COMPLETO, ShowCaseType.FORM);
        add(SPackageNotificacaoSimplificadaGasMedicinal.class, SPackageNotificacaoSimplificadaGasMedicinal.NOME_COMPLETO, ShowCaseType.FORM);
        add(SPackageNotificacaoSimplificadaFitoterapico.class, SPackageNotificacaoSimplificadaFitoterapico.NOME_COMPLETO, ShowCaseType.FORM);
        add(SPackageNotificacaoSimplificadaBaixoRisco.class, SPackageNotificacaoSimplificadaBaixoRisco.NOME_COMPLETO, ShowCaseType.FORM);
        add(SPackageHabilitacaoEmpresa.class, SPackageHabilitacaoEmpresa.NOME_COMPLETO, ShowCaseType.FORM);

    }

    @PostConstruct
    private void init() {
        for (ShowCaseGroup group : showCaseTable.getGroups()) {
            for (ShowCaseItem item : group.getItens()) {
                String itemName = group.getGroupName() + " - " + item.getComponentName();
                for (CaseBase cb : item.getCases()) {
                    if (cb instanceof CaseBaseForm) {
                        CaseBaseForm c = (CaseBaseForm)cb;
                        if (c.getSubCaseName() == null) {
                            add(itemName, c, group.getTipo());
                        } else {
                            add(itemName + " - " + c.getSubCaseName(), c, group.getTipo());
                        }
                    }
                }
            }
        }
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
        add(c.getTypeName(), displayName, () -> c.getCaseType(), tipo);
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
        for(TemplateEntry t : entries.values()){
            if(t.getType().getName().equals(type)){
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

        private final String displayName;
        private final Supplier<SType<?>> typeSupplier;
        private final ShowCaseType tipo;

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
