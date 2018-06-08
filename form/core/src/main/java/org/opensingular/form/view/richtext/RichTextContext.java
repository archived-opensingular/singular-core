package org.opensingular.form.view.richtext;

import java.io.Serializable;
import javax.annotation.Nonnull;

/**
 * A interface that can be used to create new custom buttons.
 */
public interface RichTextContext extends Serializable {

    /**
     * If The value is null, nothing happens, if the value is empty, the body of richText will be cleaned.
     *
     * @param value The value for RichText body.
     */
    void setReturnValue(@Nonnull String value);

    String getValue();
}
