package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.enums;

/**
 * Interface que deve ser utilizada pelas enumerações {@link Enum} que
 * necessitam trabalhar com um ID diferente do valor ORDINAL.
 * 
 * @author alessandro.leite
 * @since 23/10/2009
 * @param <E>
 * @param <ID>
 *            Tipo do identificador da enumeração.
 */
public interface EnumId<E extends Enum<E>, ID> {

	/**
	 * Retorna o identificador da enumeração.
	 */
	ID getCodigo();

	/**
	 * Retorna a instância da própria enumeração.
	 */
	E getEnum();

	/**
	 * @return Retorna o valor da enumeração.
	 */
	String getDescricao();	
}