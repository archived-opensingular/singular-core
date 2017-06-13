package org.opensingular.form.decorator.action;

/**
 * Interface para provedores de ícones. Utiliza a carga de serviços da JVM (ServiceLoader).
 * Novas implementações podem ser registradas via este mecanismo.
 */
public interface SIconProvider {
    /**
     * Ordem de prioridade para este provider.
     */
    int order();
    
    /**
     * Retorna o ícone.
     * @return o ícone resolvido pelo ID, se este for válido para este provider, ou null caso contrário. 
     */
    SIcon resolve(String id);
}
