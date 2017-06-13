package org.opensingular.form.decorator.action;

import java.io.Serializable;

/**
 * Interface para resolução de ícones, de forma independente do framework de apresentação.
 */
public interface SIcon extends Serializable {
    
    /**
     * Identificador do ícone.
     */
    String getId();
    
    /**
     * Classe CSS do ícone.
     */
    String getCssClass();

    static SIcon resolve(String s) {
        return SIconProviders.resolve(s);
    }
}
