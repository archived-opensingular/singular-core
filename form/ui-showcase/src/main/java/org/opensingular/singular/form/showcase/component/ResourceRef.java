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

package org.opensingular.singular.form.showcase.component;

import org.apache.commons.io.IOUtils;
import org.opensingular.lib.commons.base.SingularUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Representa um referência um recurso no class path e seu respectivo nome para
 * exibição.
 */
public class ResourceRef implements Serializable {

    private final Class<?> referenceClass;
    private final String   resourcePath;
    private final String   displayName;

    public ResourceRef(Class<?> referenceClass, String resourcePath) {
        this(referenceClass, resourcePath, resourcePath);
    }

    public ResourceRef(Class<?> referenceClass, String resourcePath, String displayName) {
        this.referenceClass = referenceClass;
        this.resourcePath = resourcePath;
        this.displayName = displayName;
    }

    public Class<?> getReferenceClass() {
        return referenceClass;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean exists() {
        return referenceClass.getResource(resourcePath) != null;
    }

    public String getContent() {
        InputStream in = referenceClass.getResourceAsStream(resourcePath);
        try {
            return IOUtils.toString(in, Charset.forName(StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

    public static Optional<ResourceRef> forSource(Class<?> target) {
        return forClassWithExtension(target, "java");
    }

    public static Optional<ResourceRef> forClassWithExtension(Class<?> target, String extension) {
        final LinkedList<String> alternatives = new LinkedList<>();

        final String basePath = target.getSimpleName() + '.' + extension;
        alternatives.add(basePath);

        final StringBuilder sb = new StringBuilder(basePath);
        for (Class<?> clazz = target; clazz.isMemberClass(); clazz = clazz.getEnclosingClass()) {
            sb.insert(0, '$');
            sb.insert(0, clazz.getEnclosingClass().getSimpleName());
            alternatives.addFirst(sb.toString());
        }
        for (String path : alternatives) {
            ResourceRef ref = new ResourceRef(target, path);
            if (ref.exists())
                return Optional.of(ref);
        }
        return Optional.empty();
    }

    public String getExtension() {
        String name = getDisplayName();
        return name == null ? null : name.substring(name.lastIndexOf('.') + 1);
    }
}
