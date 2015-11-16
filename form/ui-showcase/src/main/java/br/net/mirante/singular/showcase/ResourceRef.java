package br.net.mirante.singular.showcase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

/**
 * Representa um referência um recurso no class path e seu respectivo nome para
 * exibição.
 */
public class ResourceRef {

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
            return new String(ByteStreams.toByteArray(in));
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
}
