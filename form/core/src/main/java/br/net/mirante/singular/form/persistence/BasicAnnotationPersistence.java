package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SInstance;

/**
 * Serviço com as operações básicas de persistência de anotações, mas sem as funções de recuperação e listagem.
 *
 * @author Vinicius Uriel
 */
public interface BasicAnnotationPersistence<INSTANCE extends SInstance> {

    /**
     * Insere uma instância nova e devolve a chave do novo registro.
     *
     * @return Nunca Null
     */
    public AnnotationKey insertAnnotation(AnnotationKey annotationKey, INSTANCE instance);

    /**
     * Apaga a anotação correspondente a chave informada.
     * O form ao qual a anotação está associada deve estar previamente persistido
     */
    public void deleteAnnotation(AnnotationKey annotationKey);

    /**
     * Atualiza as anotações na base de dados, com base no atributo AnnotationKey.
     * O form ao qual a anotação está associada deve estar previamente persistido
     * @param instance
     * @param annotationKey
     */
    public void updateAnnotation(AnnotationKey annotationKey, INSTANCE instance);

    /**
     * Atualiza ou insere as anotações da instância de acordo se a mesma ja tiver ou não um FormKey associado (como atributo da instância).
     * * O form ao qual a anotação está associada deve estar previamente persistido
     * @return Chave da instância criada ou atualizada.
     */
    public AnnotationKey insertOrUpdateAnnotation(AnnotationKey annotationKey, INSTANCE instance);

    /**
     * Salva as alterações na versão atual e incrementa versão da anotação.
     * O form ao qual a anotação está associada deve estar previamente persistido
     * @param instance
     * @return
     */
    public AnnotationKey newAnnotationVersion(AnnotationKey key, INSTANCE instance);


    public AnnotationKey keyFromClassifier(FormKey formKey, String classifier);

}
