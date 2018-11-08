package org.opensingular.form.document;

import org.opensingular.form.SDictionary;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Is a reference to a {@link org.opensingular.form.SType} that retrieves the type based in a prior {@link
 * RefDictionary}.
 *
 * @author Daniel C. Bordin
 * @since 2018-10-17
 */
abstract class RefTypeFromRefDictionary extends RefType {

    private final RefDictionary refDictionary;

    RefTypeFromRefDictionary(@Nonnull RefDictionary refDictionary) {
        this.refDictionary = Objects.requireNonNull(refDictionary);
    }

    @Nonnull
    protected SDictionary getDictionary() {
        return getRefDictionary().get();
    }

    @Nonnull
    RefDictionary getRefDictionary() {
        return refDictionary;
    }
}
