/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import org.opensingular.singular.commons.base.SingularUtil;

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
            return IOUtils.toString(in, Charset.forName("UTF-8"));
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
        final String displayName = getDisplayName();
        if (displayName != null) {
            return displayName.substring(displayName.lastIndexOf('.') + 1);
        }
        return null;
    }
}
