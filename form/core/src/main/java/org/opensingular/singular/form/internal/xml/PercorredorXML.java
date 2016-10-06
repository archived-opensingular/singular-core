/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.internal.xml;

import org.w3c.dom.Element;

/**
 * Leitor de documentos XML. Versão DOM<br><br>
 * Essa classe serve para o percorrimento de documentos XML de vários níveis.
 * Sua utilidade é a busca dos valores de elementos definidos em diferentes
 * níveis da hierarquia do documento. Essa classe permite percorrer os
 * elementos que são compostos de outros elementos.<br>
 * Para criar uma instância, deve-se passar o elemento raiz da estrutura de
 * elementos e o nome do sub-elemento da raiz que será percorrido pela classe.
 * A lista de elementos a serem percorridos é formada por todos os elementos
 * que compõem o elemento raiz com o nome especificado. No documento<br><br>
 * <xmp>
 * <dados-turma>
 * <aluno>
 * <nome>João</nome>
 * <idade>22</idade>
 * </aluno>
 * <aluno>
 * <nome>Roberto</nome>
 * <idade>43</idade>
 * </aluno>
 * <professor>
 * <nome>Dr. Carlos<nome>
 * <especialidade>Física</especialidade>
 * </professor>
 * </dados-turma>
 * </xmp>
 * <p>
 * deseja-se obter os dados dos alunos da turma. Para isso cria-se uma
 * instância dessa classe para a raiz <dados-turma> e para o nome de elemento
 * 'aluno'. Ao ser chamado o método next(), o elemento atual do percorredor
 * referencia o próximo elemento 'aluno' da lista de elementos (bloco) do
 * raiz. O elemento atual aponta para o primeiro da lista assim que a primeira
 * chamada ao método next() é feita.<p>
 * <p>
 * Após chamado o método next(), pode-se chamar getValor( "nome" ) para
 * obter-se o valor do campo <nome> do primeiro aluno da turma (João). O mesmo
 * pode ser feito para o campo <idade>. Apos chamar novamente next(), o
 * elemento atual do percorredor referencia agora o próximo elemento <aluno>
 * de <dados-turma>.<p>
 * <p>
 * O elemento atual do percorredor pode estar em estados inválidos. Isso ocorre
 * quando o método next() não foi chamado ainda (INICIO_BLOCO) ou quando o
 * atual já caminhou além dos elementos existentes na lista (FIM_BLOCO). Se
 * não está em algum desses dois estados, o elemento atual está em uma posição
 * válida da lista (VALIDO).<p>
 * <p>
 * Todos os métodos não-estáticos definidos atuam sobre o elemento atual de
 * percorrimento, enquanto que os métodos estáticos atuam sobre o elemento pai
 * passado como parâmetro. <p>
 * <p>
 * É possível ainda criar um percorredor sem um nome do elemento a ser busca
 * na lista de filhos. Nesse caso, o percorredor retorna todos os filhos do
 * elemento.
 *
 * @author Daniel Bordin - Mirante Informática
 * @author Ricardo Campos - Mirante Informática
 * @see MElementResult
 * @deprecated Utilizar MElementResult, pois no futuro será removida do pacote
 */

public class PercorredorXML {

    /**
     * Estado em que o elemento atual é válido.
     *
     * @deprecated Utilize isAfterLast(), isBeforeFirst() e isAtualValido()
     */
    public static final byte VALIDO = 0;

    /**
     * Estado em que o elemento atual não percorreu nenhum elemento ainda.
     *
     * @deprecated Utilize isAfterLast(), isBeforeFirst() e isAtualValido()
     */
    public static final byte INICIO_BLOCO = 1;

    /**
     * Estado em que o elemento atual está além do último elemento da lista.
     *
     * @deprecated Utilize isAfterLast(), isBeforeFirst() e isAtualValido()
     */
    public static final byte FIM_BLOCO = 2;

    /**
     * Elemento que referencia o nó inicial da hierarquia do documento.
     */
    private final MElement raiz_;

    /**
     * Elemento para percorrimento da lista de elementos do elemento raiz.
     */
    private MElement atual_;

    /**
     * Estado em que o elemento atual se encontra.
     */
    private byte estadoAtual_;

    /**
     * Nome dos elementos da lista que serão percorridos.
     */
    private final String nomeElemento_;

    /**
     * Cria percorredor para todos os elementos filhos do raiz.
     *
     * @param raiz elemento raiz da hierarquia do documento
     */
    public PercorredorXML(Element raiz) {
        if (raiz == null) {
            throw new IllegalArgumentException("Elemento raiz nulo");
        }
        raiz_ = MElement.toMElement(raiz);
        nomeElemento_ = null;
        estadoAtual_ = INICIO_BLOCO;
    }

    /**
     * Cria percorredor para percorrimento de elementos com determinado nome.
     *
     * @param raiz elemento raiz da hierarquia do documento
     * @param nomeElem nome dos elementos que serão percorridos
     */
    public PercorredorXML(Element raiz, String nomeElem) {
        if (raiz == null) {
            throw new IllegalArgumentException("Elemento raiz nulo");
        }

        int pos = nomeElem.lastIndexOf(XMLToolkit.SEPARADOR_ELEMENT);
        if (pos != -1) {
            Element r = XMLToolkit.getElement(raiz, nomeElem.substring(0, pos));
            if (r == null) {
                nomeElemento_ = "_____NAOEXISTEPAI______";
                raiz_ = MElement.toMElement(raiz);
                estadoAtual_ = FIM_BLOCO;
            } else {
                nomeElemento_ = nomeElem.substring(pos + 1);
                raiz_ = MElement.toMElement(r);
                estadoAtual_ = INICIO_BLOCO;
            }
        } else {
            raiz_ = MElement.toMElement(raiz);
            nomeElemento_ = nomeElem;
            estadoAtual_ = INICIO_BLOCO;
        }
    }

    /**
     * Conta o número de vezes que os elementos a serem percorridos aparecem
     * no elemento raiz.
     *
     * @return o número de vezes que os elementos a serem percorridos ocorrem
     * no raiz
     */
    public final int count() {
        if (nomeElemento_ == null) {
            return raiz_.countFilhos();
        } else {
            return raiz_.count(nomeElemento_);
        }
    }

    /**
     * Determina se algum elemento da lista é o elemento atual de
     * percorrimento.<br>
     * Estado inválido ocorre quando o atual não caminhou ainda ou se ele já
     * atingiu o fim da lista.
     *
     * @return <code>true</code> se o elemento atual não referencia nenhum
     * elemento da lista; <code>false</code> se o atual referencia
     * algum elemento da lista
     *
     * @deprecated Utilize ! isAtualValido()
     */
    public final boolean estadoInvalido() {
        return (estadoAtual_ == INICIO_BLOCO) || (estadoAtual_ == FIM_BLOCO);
    }

    /**
     * Retorna o elemento atual do percorredor.<br>
     * Método de instância.
     *
     * @return o elemento atual do percorredor
     */
    public final MElement getAtual() {
        if (estadoInvalido()) {
            throw new IllegalStateException(msgErroAtual());
        }

        return atual_;
    }

    public boolean isBeforeFirst() {
        return estadoAtual_ == INICIO_BLOCO;
    }

    public final boolean isAtualValido() {
        return (estadoAtual_ == INICIO_BLOCO) || (estadoAtual_ == FIM_BLOCO);
    }

    public boolean isAfterLast() {
        return estadoAtual_ == FIM_BLOCO;
    }

    /**
     * Busca um elemento dentro do elemento atual do percorredor.<br>
     *
     * @param nomeElemento o nome de um elemento dentro do atual
     * @return o primeiro elemento de nome <i>nomeElemento</i> dentro do
     * elemento atual
     *
     * @deprecated utilize o método de getElement de MElement da
     * seguinte forma <code>getAtual().getElement(nomeElemento)</code>.
     */
    public final MElement getElement(String nomeElemento) {
        if (estadoInvalido()) {
            throw new IllegalArgumentException(msgErroAtual());
        }
        return atual_.getElement(nomeElemento);
    }

    /**
     * @deprecated Utilize isBeforeFirst(), isAfterLast()
     */
    public final byte getEstado() {
        return estadoAtual_;
    }

    /**
     * Cria um percorredor para o elemento atual sendo percorrido.<br>
     * Método de instância.
     *
     * @param nomeElem nome de um elemento dentro do elemento atual do
     * percorredor
     * @return uma instância dessa classe tendo como raiz o elemento
     * <i>nomeElem</i> do elemento atual
     *
     * @deprecated utilize o método de getpercorredor de MElement da
     * seguinte forma <code>getAtual().getPercorredor(nomeElem)</code>.
     */
    public final PercorredorXML getSubPercorredor(String nomeElem) {
        if (estadoInvalido()) {
            throw new IllegalStateException(msgErroAtual());
        }

        return new PercorredorXML(atual_, nomeElem);
    }

    /**
     * Retorma o valor (text) do element atual.
     *
     * @return String -
     *
     * @deprecated utilize o método de getValor de MElement da
     * seguinte forma <code>getAtual().getValor()</code>.
     */
    public final String getValor() {
        if (estadoInvalido()) {
            throw new IllegalStateException(msgErroAtual());
        }
        return atual_.getValor();
    }

    /**
     * Retorna o valor de um elemento.
     *
     * @param nomeElemento o nome de um elemento dentro do elemento atual do
     * percorredor
     * @return uma string com o valor do primeiro elemento
     * <i>nomeElemento</i> dentro do atual
     *
     * @deprecated utilize o método de getValor de MElement da
     * seguinte forma <code>getAtual().getValor(nomeElemento)</code>.
     */
    public final String getValor(String nomeElemento) {
        if (estadoInvalido()) {
            throw new IllegalStateException(msgErroAtual());
        }
        return atual_.getValor(nomeElemento);
    }

    /**
     * Busca os valores de todos os elementos dentro do elemento atual com
     * determinado nome.
     *
     * @param nomeElemento o nome de elementos dentro do atual do percorredor
     * @return um array de string com os valores dos elementos
     * <i>nomeElemento</i>, na ordem em que aparecem no atual
     *
     * @deprecated utilize o método de getValores de MElement da
     * seguinte forma <code>getAtual().getValores(nomeElemento)</code>.
     */
    public final String[] getValores(String nomeElemento) {
        if (estadoInvalido()) {
            throw new IllegalStateException(msgErroAtual());
        }
        return atual_.getValores(nomeElemento);
    }

    /**
     * Mensagem de erro para o estado do elemento de percorrimento.
     *
     * @return a string dizendo o erro
     */
    private String msgErroAtual() {
        if (estadoInvalido()) {
            return "O elemento atual está no "
                    + ((estadoAtual_ == INICIO_BLOCO) ? "início" : "final")
                    + " da lista";
        }

        return "(elemento atual em estado válido)";
    }

    /**
     * Caminha o elemento atual do percorredor para o próximo da lista.<br>
     * A lista é formada por todos os elementos dentro do raiz que têm o nome
     * passado ao construtor.
     *
     * @return <code>false</code> se não existem mais elementos na lista de
     * percorrimento; <code>true</code> se o atual caminhou para o
     * próximo elemento da lista
     */
    public final boolean next() {
        if (estadoAtual_ == FIM_BLOCO) {
            return false;
        }

        if (nomeElemento_ == null) {
            if (estadoAtual_ == INICIO_BLOCO) {
                atual_ = raiz_.getPrimeiroFilho();
            } else {
                atual_ = atual_.getProximoIrmao();
            }
        } else {
            if (estadoAtual_ == INICIO_BLOCO) {
                atual_ = raiz_.getPrimeiroFilho(nomeElemento_);
            } else {
                atual_ = atual_.getProximoGemeo();
            }
        }

        if (atual_ == null) {
            estadoAtual_ = FIM_BLOCO;
        } else {
            estadoAtual_ = VALIDO;
        }

        return estadoAtual_ == VALIDO;
    }

    /**
     * Verifica se existe o elemento com o nome especificado no elemento atual
     *
     * @param nomeElemento Nome do elemento a ser procurado
     * @return true se existir
     *
     * @deprecated utilize o método de possuieElement de MElement da
     * seguinte forma <code>getAtual().possuiElement(nomeElemento)</code>.
     */
    public final boolean possuiElement(String nomeElemento) {
        return atual_.possuiElement(nomeElemento);
    }

    /**
     * Imprime o elemento atual.
     *
     * @return -
     */
    public String toString() {
        return "atual=" + atual_;
    }
}
