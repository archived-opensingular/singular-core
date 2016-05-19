/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.spring.SpringTypeLoader;


public class SingularStudioTypeLoader extends SpringTypeLoader<String> {

    private final Map<String, TemplateEntry> entries = new LinkedHashMap<>();

    public SingularStudioTypeLoader() {

    }

    private void add(Class<? extends SPackage> packageClass, String typeName) {
        String simpleName = StringUtils.defaultIfBlank(StringUtils.substringAfterLast(typeName, "."), typeName);
        add(typeName, simpleName, () -> {
            SDictionary d = SDictionary.create();
            d.loadPackage(packageClass);
            return d.getType(typeName);
        });
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
