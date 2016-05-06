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

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.cancelamento.SPackageRetencao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado.SPackageNotificacaoSimplificadaDinamizado;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.gas.SPackageNotificacaoSimplificadaGasMedicinal;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.habilitacao.SPackageHabilitacaoEmpresa;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vegetal.SPackageNotificacaoSimplificadaFitoterapico;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.spring.SpringTypeLoader;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ShowCaseTable;
import br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseGroup;
import br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseItem;
import br.net.mirante.singular.showcase.view.page.form.examples.ExamplePackage;
import br.net.mirante.singular.showcase.view.page.form.examples.SPackageCurriculo;
import br.net.mirante.singular.showcase.view.page.form.examples.SPackagePeticaoGGTOX;


public class ShowcaseTypeLoader extends SpringTypeLoader<String> {

    private final Map<String, TemplateEntry> entries = new LinkedHashMap<>();

    public ShowcaseTypeLoader() {
        add(SPackageCurriculo.class, SPackageCurriculo.TIPO_CURRICULO);
        add(ExamplePackage.class, ExamplePackage.Types.ORDER.name);
        add(SPackagePeticaoGGTOX.class, SPackagePeticaoGGTOX.NOME_COMPLETO);
        add(SPackageNotificacaoSimplificadaBaixoRisco.class, SPackageNotificacaoSimplificadaBaixoRisco.NOME_COMPLETO);
        add(SPackageNotificacaoSimplificadaDinamizado.class, SPackageNotificacaoSimplificadaDinamizado.NOME_COMPLETO);
        add(SPackageNotificacaoSimplificadaGasMedicinal.class, SPackageNotificacaoSimplificadaGasMedicinal.NOME_COMPLETO);
        add(SPackageNotificacaoSimplificadaFitoterapico.class, SPackageNotificacaoSimplificadaFitoterapico.NOME_COMPLETO);
        add(SPackageRetencao.class, SPackageRetencao.NOME_COMPLETO);
        add(SPackageHabilitacaoEmpresa.class, SPackageHabilitacaoEmpresa.NOME_COMPLETO);

        for (ShowCaseGroup group : new ShowCaseTable().getGroups()) {
            for (ShowCaseItem item : group.getItens()) {
                String itemName = group.getGroupName() + " - " + item.getComponentName();
                for (CaseBase c : item.getCases()) {
                    if (c.getSubCaseName() == null) {
                        add(itemName, c);
                    } else {
                        add(itemName + " - " + c.getSubCaseName(), c);
                    }
                }
            }
        }
    }

    private void add(Class<? extends SPackage> packageClass, String typeName) {
        String simpleName = StringUtils.defaultIfBlank(StringUtils.substringAfterLast(typeName, "."), typeName);
        add(typeName, simpleName, () -> {
            SDictionary d = SDictionary.create();
            d.loadPackage(packageClass);
            return d.getType(typeName);
        });
    }

    private void add(String displayName, CaseBase c) {
        add(c.getTypeName(), displayName, () -> c.getCaseType());
    }

    private void add(String typeName, String displayName, Supplier<SType<?>> typeSupplier) {
        entries.put(typeName, new TemplateEntry(displayName, typeSupplier));
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

    public static class TemplateEntry {

        private final String displayName;
        private final Supplier<SType<?>> typeSupplier;

        public TemplateEntry(String displayName, Supplier<SType<?>> typeSupplier) {
            this.displayName = displayName;
            this.typeSupplier = typeSupplier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SType<?> getType() {
            return typeSupplier.get();
        }
    }
}
