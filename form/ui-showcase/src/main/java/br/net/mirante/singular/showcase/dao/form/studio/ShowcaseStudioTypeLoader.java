/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.dao.form.studio;

import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SFormUtil;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.spring.SpringTypeLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ShowcaseStudioTypeLoader extends SpringTypeLoader<Class<SType<?>>> {

    private Map<Class<? extends SPackage>, SDictionary> dictionaries = new HashMap<>();

    @Override
    protected Optional<SType<?>> loadTypeImpl(Class<SType<?>> typeClass) {
        Class<? extends SPackage> packageClass = SFormUtil.getTypePackage(typeClass);
        String typeName = SFormUtil.getTypeName(typeClass);
        if (!dictionaries.containsKey(packageClass)) {
            SDictionary d = SDictionary.create();
            d.loadPackage(packageClass);
            dictionaries.put(packageClass, d);
        }
        return Optional.ofNullable(dictionaries.get(packageClass).getType(typeName));
    }
}