package org.opensingular.form.io;

import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.internal.lib.commons.xml.BinaryElementIO;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for generating and reading a binary representation of a {@link SInstance}.
 * <p>This class wasn't designed for long term storage, but only for runtime purposes.</p>
 *
 * @author Daniel C. Bordin
 * @since 2018-10-12
 */
public final class SFormBinaryUtil {

    private SFormBinaryUtil() {}

    /** Generates the binary representation of the {@link SInstance}. */
    public static void write(@Nonnull OutputStream out, @Nonnull SInstance instance) throws IOException {
        MElement xml = SFormXMLUtil.toXMLOrEmptyXML(instance);
        BinaryElementIO.write(out, xml);
    }

    /** Generates the binary representation of the {@link SInstance}. */
    @Nonnull
    public static byte[] write(@Nonnull SInstance instance) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            write(out, instance);
            return out.toByteArray();
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        }
    }

    /** Generates the binary representation of the {@link SInstance} preserving the runtime information (attributes). */
    @Nonnull
    public static byte[] writePreservingRuntimeEdition(@Nonnull SInstance instance) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writePreservingRuntimeEdition(out, instance);
            return out.toByteArray();
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        }

    }

    /** Generates the binary representation of the {@link SInstance} preserving the runtime information (attributes). */
    public static void writePreservingRuntimeEdition(@Nonnull OutputStream out, @Nonnull SInstance instance)
            throws IOException {
        MElement xml = SFormXMLUtil.toXMLPreservingRuntimeEdition(instance);
        BinaryElementIO.write(out, xml);
    }

    /** Creates a {@link SInstance} that can be serialized  with informed type and document factory. */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T extends SInstance> T read(@Nonnull RefType refType, @Nullable byte[] content,
            @Nonnull SDocumentFactory documentFactory) {
        SInstance instance = documentFactory.createInstance(refType, false);
        if (content != null) {
            read(instance, content);
        }
        return (T) instance;
    }

    /** Loads the binary representation into the {@link SInstance}. */
    public static void read(@Nonnull SInstance instance, @Nonnull byte[] content) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(content)) {
            read(instance, in);
        } catch (IOException e) {
            throw new SingularFormException("Fail to read content", e);
        }
    }

    /** Loads the binary representation into the {@link SInstance}. */
    public static void read(@Nonnull SInstance instance, @Nonnull InputStream in) throws IOException {
        MElement xml = BinaryElementIO.read(in);
        SFormXMLUtil.fromXML(instance, xml);
    }
}
