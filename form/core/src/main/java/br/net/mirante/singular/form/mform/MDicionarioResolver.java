package br.net.mirante.singular.form.mform;

import java.util.Optional;

/**
 * <p>
 * O Dicionário Resolver é provida pela aplicação host de modo a permitir o
 * recuperação do dicionário de definições para o tipo solicitado. Tipicamente é
 * utilziado no processo de deserialziação, recuperação de instancias peristidas
 * ou mesmo criação de uma nova versão. Pode ser trabalhado de duas formas:
 * </p>
 * <ul>
 * <li>Provendo explicitamente o Dicionário Resolver durente o processo de
 * deserialziação ou recuperação da persistência.
 * <li>Definindo o resolver de forma global (singleton) mediante
 * {@link #setDefault(MDicionarioResolver)}.</li>
 * </ul>
 * <p>
 * Se não for informado explicitamente o resolver a ser usado, então
 * automaticamente deve ser usado o resolver default.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public abstract class MDicionarioResolver {

    private static MDicionarioResolver defaultResolver;

    public static void setDefault(MDicionarioResolver resolver) {
        defaultResolver = resolver;
    }

    /**
     * Retorna o resolvedor de dicionário default da aplicação ou dispara uma
     * exception senão estiver configurado.
     * 
     * @return Nunca Null
     */
    public static MDicionarioResolver getDefault() {
        if (defaultResolver == null) {
            throw new SingularFormException("O dicionário resolver default não está configurado");
        }
        return defaultResolver;
    }

    /**
     * Retorna o dicionário para o tipo informado se possível.
     *
     * @param typeName
     *            Nome completo do tipo para o qual se deseja o dicionário.
     *            Tipicamente deve ser o tipo que representa um documento como
     *            um todo.
     */
    public abstract Optional<MDicionario> loadDicionaryForType(String typeName);

    /**
     * Retorna o dicionário para o tipo informado ou dispara exception se não
     * encontrar.
     *
     * @param typeName
     *            Nome completo do tipo para o qual se deseja o dicionário.
     *            Tipicamente deve ser o tipo que representa um documento como
     *            um todo.
     * @exception SingularFormException
     *                Senão encontrar o dicionário.
     */
    public final MDicionario loadDicionaryForTypeOrException(String typeName) throws SingularFormException {
        return loadDicionaryForType(typeName)
                .orElseThrow(() -> new SingularFormException("Não foi encontrado dicionário para o tipo " + typeName));
    }

    /**
     * Encontrar o dicionário associado ao tipo usando
     * {@link #loadDicionaryForTypeOrException(String)} e recupera o tipo a
     * partir do dicionário.
     *
     * @return Nunca Null
     * @exception SingularFormException
     *                Senão encontrar o dicionário ou o tipo no dicionário.
     */
    public final MTipo<?> loadType(String typeName) {
        return loadDicionaryForTypeOrException(typeName).getTipo(typeName);
    }

    /**
     * Creates a resolver that always return the same dictionary.
     *
     * @param onlyDict MDicionario to be returned.
     * @return MDicionarioResolver that always return onlyDict.
     */
    public static MDicionarioResolver of(final MDicionario onlyDict){
        return new MDicionarioResolver(){
            @Override
            public Optional<MDicionario> loadDicionaryForType(String typeName) {
                return Optional.of(onlyDict);
            }
        };
    }
}
