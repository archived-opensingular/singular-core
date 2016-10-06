package org.opensingular.singular.form.persistence;

import org.opensingular.singular.form.SIList;
import org.opensingular.singular.form.type.core.annotation.SIAnnotation;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de persistência para alteração e recuperação de instâncias. Se diferencia de {@link BasicAnnotationPersistence} ao
 * acrescentar a funcionalidades de pesquisa.
 *
 * @author Vinicius Nunes
 */
public interface AnnotationPersistence extends BasicAnnotationPersistence {

    /**
     * Recupera a instância correspondete a chava ou dispara Exception se não encontrar.
     */
    public SIList<SIAnnotation> loadAnnotation(AnnotationKey key);

    /**
     * Tentar recupeara a instância correspondente a chave, mas pode retornar resultado vazio.
     */
    public Optional<SIList<SIAnnotation>> loadOpt(AnnotationKey key);

    /**
     * Retorna uma lista de SIList<SIAnnotation> onde cada SIList é uma lista
     * de anotações cujo classifier é o mesmo.
     *
     * Retorna todas as SILists de anotações de todos os classifiers do formulário
     * @param formKey
     * @return
     */
    public List<SIList<SIAnnotation>> loadAll(FormKey formKey);

}
