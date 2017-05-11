package org.opensingular.form.decorator.action;

import java.io.Serializable;

public interface SIcon extends Serializable {
    String getId();
    String getCssClass();

    static SIcon resolve(String s) {
        return SIconProviders.resolve(s);
    }
}
