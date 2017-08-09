package org.opensingular.lib.commons.views;

public interface ViewOutput<T> {

    /**
     * Obtem a saída de escrita do conteúdo da view.
     *
     * @return Deve ser sempre diferente de null.
     */
    T getOutput();

    ViewOutputFormat getFormat();

}