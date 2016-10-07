package org.opensingular.form.processor;

import org.opensingular.form.TypeBuilder;
import org.opensingular.form.SType;

/**
 * Processador de tipo chamado assim que um novo tipo é adicionado ao dicionário, ou seja, assim que torna-se um tipo
 * efetivamente. Suas implementações deve ser registradas no dicionário ou no pacote para garantir a execução.
 *
 * @author Daniel C. Bordin
 */
public interface TypeProcessorPosRegister {

    /**
     * @param type         Tipo que foi carregado
     * @param onLoadCalled Indica se o tipo teve o método {@link SType#onLoadType(TypeBuilder)} chamado para o tipo ou
     *                     não (se for a extensão de um tipo que já teve o método chamado).
     */
    public void processTypePosRegister(SType<?> type, boolean onLoadCalled);
}
