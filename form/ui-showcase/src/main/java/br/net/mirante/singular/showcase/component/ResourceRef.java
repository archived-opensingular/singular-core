/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component;

import com.google.common.base.Throwables;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Representa um referência um recurso no class path e seu respectivo nome para
 * exibição.
 */
public class ResourceRef implements Serializable {

    private final Class<?> referenceClass;
    private final String resourcePath;
    private final String displayName;

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
            throw Throwables.propagate(e);
        }
    }

    public static Optional<ResourceRef> forSource(Class<?> target) {
        return forClassWithExtension(target, "java");
    }

    public static Optional<ResourceRef> forClassWithExtension(Class<?> target, String extension) {
        return verifyExists(new ResourceRef(target, target.getSimpleName() + '.' + extension));
    }

    private static Optional<ResourceRef> verifyExists(ResourceRef ref) {
        return ref.exists() ? Optional.of(ref) : Optional.empty();
    }

    public String getExtension() {
        final String displayName = getDisplayName();
        if (displayName != null) {
            return displayName.substring(displayName.lastIndexOf('.') + 1);
        }
        return null;
    }
}
