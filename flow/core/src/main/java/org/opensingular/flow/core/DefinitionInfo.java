package org.opensingular.flow.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para declarar metadados de uma definição
 * A presença dessa anotação é obrigatória nas definições de fluxo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefinitionInfo {

    /**
     * Rerpesenta a chave do fluxo
     *
     * @return
     */
    String value();
}
