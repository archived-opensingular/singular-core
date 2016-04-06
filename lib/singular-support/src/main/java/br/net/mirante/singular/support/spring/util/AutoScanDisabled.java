package br.net.mirante.singular.support.spring.util;

/**
 * Anotação de marcação para indicar que um componente do spring não deve ser carregado durante o auto-scan.
 * Para isso é preciso definir um filtro no autoscan do tipo annotation e configurar essa anotação como a anotação
 * de marcação.
 *
 */
public @interface AutoScanDisabled {
}
