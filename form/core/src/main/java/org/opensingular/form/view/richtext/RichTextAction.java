package org.opensingular.form.view.richtext;

import java.util.Optional;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

public interface RichTextAction<T extends RichTextContext> {

    String getLabel();
    default Boolean getLabelInline(){
        return Boolean.TRUE;
    }
    String getIconUrl();
    Optional<Class<? extends SType<?>>> getForm();
    Class<? extends T> getType();
    void onAction(T richTextActionContext, Optional<SInstance> sInstance);
}
