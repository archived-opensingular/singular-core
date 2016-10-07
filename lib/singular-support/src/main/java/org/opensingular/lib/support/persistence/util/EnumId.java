package org.opensingular.lib.support.persistence.util;

/**
 * Interface que deve ser utilizada pelas enumerações {@link Enum} que
 * necessitam trabalhar com um ID diferente do valor ORDINAL.
 *
 * @param <E>
 * @param <ID> Tipo do identificador da enumeração.
 * @author alessandro.leite
 * @since 23/10/2009
 */
public interface EnumId<E extends Enum<E>, ID> {

    /**
     * Retorna o identificador da enumeração.
     */
    ID getCodigo();

    /**
     * @return Retorna o valor da enumeração.
     */
    String getDescricao();

    Enum<E> valueOfEnum(ID codigo);
}