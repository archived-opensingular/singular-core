package br.net.mirante.singular.form.mform;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * Recuperador de dicionário com base no ID do mesmo. É provida pela aplicação
 * host de modo a permitir o recuperação do dicionário de definições para o ID
 * solicitado. Tipicamente é utilziado no processo de deserialziação,
 * recuperação de instancias peristidas ou mesmo criação de uma nova versão.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public abstract class SDictionaryLoader<KEY extends Serializable> {

    /**
     * Retorna o dicionário para o tipo informado se possível. Faz algumsa
     * verificações de integridade no dicionário.
     *
     * @param dictionatyId
     *            Identificador do dicionário a ser carregado.
     */
    public final Optional<SDictionary> loadDictionary(KEY dictionaryId) {
        Optional<SDictionary> d = loadDictionaryImpl(Objects.requireNonNull(dictionaryId));
        if (d.isPresent() && !d.get().getSerializableDictionarySelfReference().isPresent()) {
            RefSDictionary ref = createDictionaryRef(dictionaryId);
            if (ref == null) {
                throw new SingularFormException(getClass().getName() + " devolveu um dicionário para o id=" + dictionaryId
                        + ", mas não configurou SDictionary.setSerializableDictionarySelfReference(), "
                        + "o que impedirá a serialização de instâncias desse dicionário. Recomenda-se usar "
                        + RefSDictionaryByLoader.class.getName() + " ou implementar createDictionaryRef()");
            } else {
                d.get().setSerializableDictionarySelfReference(ref);
            }
        }
        return d;
    }

    protected abstract RefSDictionary createDictionaryRef(KEY dictionaryId);

    /**
     * Implementa a efetiva recuperação do dicionário. O dicionário retornado
     * deve configurar
     * {@link SDictionary#setSerializableDictionarySelfReference(RefSDictionary)}
     * .
     *
     * @param dictionatyId
     *            Identificador do dicionário a ser carregado.
     */
    protected abstract Optional<SDictionary> loadDictionaryImpl(KEY dictionatyId);

    /**
     * Retorna o dicionário para o tipo informado ou dispara exception se não
     * encontrar.
     *
     * @param dictionaryId
     *            Identificador do dicionário a ser carregado.
     * @exception SingularFormException
     *                Senão encontrar o dicionário.
     */
    public final SDictionary loadDictionaryOrException(KEY dictionaryId) throws SingularFormException {
        return loadDictionary(dictionaryId)
                .orElseThrow(() -> new SingularFormException("Não foi encontrado dicionário para o o id=" + dictionaryId));
    }

    /**
     * Encontrar o dicionário associado ao tipo usando
     * {@link #loadDictionaryOrException(String)} e recupera o tipo a partir do
     * dicionário.
     *
     * @param dictionatyId
     *            Identificador do dicionário a ser carregado.
     * @return Nunca Null
     * @exception SingularFormException
     *                Senão encontrar o dicionário ou o tipo no dicionário.
     */
    public final SType<?> loadType(KEY dictionatyId, String typeName) {
        return loadDictionaryOrException(dictionatyId).getType(typeName);
    }
}
