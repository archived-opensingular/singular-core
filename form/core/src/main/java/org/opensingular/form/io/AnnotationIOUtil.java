package org.opensingular.form.io;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.core.annotation.DocumentAnnotations;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.internal.lib.commons.xml.MElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utility class for write and read form annotations.
 *
 * @author Daniel C. Bordin
 * @since 2018-10-14
 */
public final class AnnotationIOUtil {

    private AnnotationIOUtil() {}

    /**
     * Generates a binary representation of document's annotations for temporary storaged (it's not design for long
     * storage).
     * @return Null is there is no annotation to be saved
     */
    @Nullable
    static byte[] toBinaryPreservingRuntimeEdition(@Nonnull SDocument document) {
        if (document.getDocumentAnnotations().hasAnnotations()) {
            return SFormBinaryUtil.writePreservingRuntimeEdition(document.getDocumentAnnotations().getAnnotations());
        }
        return null;
    }

    /**
     * Loads into the document (in the proper {@link SInstance}) the annotation defined in the XML.
     *
     * @param contentAnnotations If null or blank, the method does nothing
     */
    public static void loadFromXmlIfAvailable(@Nonnull SDocument document, @Nullable String contentAnnotations) {
        loadFromXmlIfAvailable(document, SFormXMLUtil.parseXml(contentAnnotations));
    }

    /**
     * Loads into the document (in the proper {@link SInstance}) the annotation defined in the XML.
     *
     * @param contentAnnotations If null, the method does nothing
     */
    public static void loadFromXmlIfAvailable(@Nonnull SDocument document, @Nullable MElement contentAnnotations) {
        if (contentAnnotations != null) {
            load(document, iAnnotations -> SFormXMLUtil.fromXML(iAnnotations, contentAnnotations));
        }
    }

    /**
     * Loads into the document (in the proper {@link SInstance}) the annotation defined in the byte array.
     *
     * @param contentAnnotations If null, the method does nothing
     */
    static void loadFromBytesIfAvailable(@Nonnull SDocument document, @Nullable byte[] contentAnnotations) {
        if (contentAnnotations != null) {
            load(document, iAnnotations -> SFormBinaryUtil.read(iAnnotations, contentAnnotations));
        }
    }

    private static void load(@Nonnull SDocument document, @Nonnull Consumer<SIList<SIAnnotation>> finalRead) {
        SIList<SIAnnotation> iAnnotations = DocumentAnnotations.newAnnotationList(document, false);
        finalRead.accept(iAnnotations);
        document.getDocumentAnnotations().loadAnnotations(iAnnotations);
    }

    /** Generates a XML representation of the document's annotation if there is any annotation in it. */
    @Nonnull
    public static Optional<String> toXmlString(@Nonnull SInstance instance) {
        return toXML(instance).map(MElement::toStringExato);
    }

    /** Generates a XML representation of the document's annotation if there is any annotation in it. */
    @Nonnull
    public static Optional<MElement> toXML(@Nonnull SInstance instance) {
        return toXML(instance, null);
    }

    /** Generates a XML representation of the document's annotation if there is any annotation in it. */
    @Nonnull
    public static Optional<MElement> toXML(@Nonnull SInstance instance, @Nullable String classifier) {
        return toXML(instance.getDocument(), classifier);
    }

    /** Generates a XML representation of the document's annotation if there is any annotation in it. */
    @Nonnull
    public static Optional<MElement> toXML(@Nonnull SDocument document, @Nullable String classifier) {
        DocumentAnnotations documentAnnotations = document.getDocumentAnnotations();
        if (documentAnnotations.hasAnnotations()) {
            if (classifier != null) {
                return SFormXMLUtil.toXML(documentAnnotations.persistentAnnotationsClassified(classifier));
            } else {
                return SFormXMLUtil.toXML(documentAnnotations.getAnnotations());
            }
        }
        return Optional.empty();
    }
}
