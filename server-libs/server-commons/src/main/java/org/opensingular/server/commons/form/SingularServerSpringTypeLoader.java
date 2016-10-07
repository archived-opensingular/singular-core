/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.form;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SType;
import org.opensingular.form.spring.SpringTypeLoader;
import org.opensingular.server.commons.config.SingularServerConfiguration;

public class SingularServerSpringTypeLoader extends SpringTypeLoader<String> {

    private final Map<String, Supplier<SType<?>>> entries = new LinkedHashMap<>();

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    public SingularServerSpringTypeLoader() {
    }

    @PostConstruct
    private void init() {
        singularServerConfiguration.getFormTypes().forEach(this::add);
    }

    private void add(Class<? extends SType<?>> type) {
        String typeName   = SFormUtil.getTypeName(type);
        String simpleName = StringUtils.defaultIfBlank(StringUtils.substringAfterLast(typeName, "."), typeName);
        add(typeName, simpleName, () -> {
            SDictionary d = SDictionary.create();
            d.loadPackage(SFormUtil.getTypePackage(type));
            return d.getType(type);
        });
    }

    private void add(String typeName, String displayName, Supplier<SType<?>> typeSupplier) {
        entries.put(typeName, typeSupplier);
    }

    @Override
    protected Optional<SType<?>> loadTypeImpl(String typeId) {
        return Optional.ofNullable(entries.get(typeId)).map(Supplier::get);
    }
}
