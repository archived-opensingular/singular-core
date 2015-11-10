package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;

import br.net.mirante.singular.form.mform.MDicionarioResolver;

/**
 * <p>
 * É um resolver de dicionário que pode ser serializado, provavelemente junto
 * com a instância. Em geral, irá recuperar o dicionário lendo de algum
 * singleton, constante ou mesmo recriar o dicionário do zero.
 * </p>
 * <p>
 * ATENÇÃO: Se for criado como um classe anonima ou closure, cuidado para não
 * colocar um referência implicita a um dado que não deva ser serializado.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public abstract class MDicionarioResolverSerializable extends MDicionarioResolver implements Serializable {

}
