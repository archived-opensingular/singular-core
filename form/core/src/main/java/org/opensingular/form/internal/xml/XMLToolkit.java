/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.internal.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta classe tem por objetivo auxiliar na montagem de um
 * um arquivo XML e permitir a transformação de objetos Element
 * em um arquivo XML. <p>
 * <p>
 * É possível montar uma árvore XML com objeto org.w3c.Element
 * usando os métodos desta classe, no entanto, este procedimento não
 * é prático, pois exige mais de um passo para adicionar uma
 * única informação. Esta classe essencialmente fornece métodos
 * para agilizar este processo. <p>
 * <p>
 * Outro ponto importante é que seria possível gerar o XML
 * diretamente em um arquivo. Este procedimento é particulamente
 * eficiente para XML com estruturas simples que vão ser gravadas
 * em arquivo ou enviado via rede. Para este casos, provavelmente
 * este método será mais rápido. No entanto, para os casos em que a
 * estrutura XML passa a ser um pouco mais complexa, este método
 * deixar de ser prático, passado a ser mais fácil usar esta classe. <p>
 * <p>
 * A montagem de um estrutura de objetos Element em vez do arquivo
 * XML também é bem mais simples e de melhor performance, quando
 * o XML será consumido pelo mesmo processo (aplicação) que está
 * gerando-o. Neste caso, evita-se fazer um parse do arquivo, pois
 * a estrutura já fica disponível em memória.<p>
 * <p>
 * <b>Exemplo de uso</b> (na prática a ordem pode ser outra):<br>
 * <p>
 * Passo 1: <i>Cria o elemento raiz:</i><br>
 * <xmp>
 * Element raiz = XMLToolkit.newRootElement("pedido");
 * // XML resultado:
 * // <pedido/>
 * </xmp>
 * <p>
 * Passo 2: <i>Adicionar sub-elementos:</i><br>
 * <xmp>
 * Element item1 = XMLToolkit.addElement(raiz,"item");
 * Element item2 = XMLToolkit.addElement(raiz,"item");
 * // XML resultado em raiz:
 * // <pedido>
 * //   <item/>
 * //   <item/>
 * // </pedido>
 * </xmp>
 * <p>
 * Passo 3: <i>Adicionar elementos com valores:</i><br>
 * <xmp>
 * XMLToolkit.addElement(item1,"nome","arroz");
 * XMLToolkit.addElement(item1,"qtd",10);
 * XMLToolkit.addElement(item2,"nome","milho");
 * XMLToolkit.addElement(item2,"qtd",100);
 * XMLToolkit.addElement(item2,"unidade","kg");
 * XMLToolkit.addElement(raiz,"responsavel","Paulo Santos");
 * // XML resultado em raiz:
 * // <pedido>
 * //   <item>
 * //      <nome>arroz</nome>
 * //      <qtd>10</qtd>
 * //   </item>
 * //   <item>
 * //      <nome>milho</nome>
 * //      <qtd>100</qtd>
 * //      <unidade>kg</unidade>
 * //   </item>
 * //   <responsavel>Paulo Santos</reponsavel>
 * // </pedido>
 * </xmp>
 *
 * @author Daniel Bordin
 * @author Ricardo Campos
 * @version 24/08/2000
 * @since 20/04/2000 12:07:27
 * @deprecated Utilize MElement (é bem mais simples)
 */

public final class XMLToolkit {

    /**
     * Para ao retornar resultado vazio não criar novo objeto.
     */
    private static final String[] LISTA_VAZIA = new String[0];

    /**
     * Caracter separador de nomes de Elementos (Xpath).
     */
    public static final char SEPARADOR_ELEMENT = '/';

    /**
     * Esconde contrutor por ser uma classe utilitária.
     */
    private XMLToolkit() {
    }

    /**
     * Adiciona um elemento a um elemento pai.<br>
     * O elemento é adicionado sem valor.
     *
     * @param parent o elemento dentro do qual um elemento será inserido
     * @param elementName o nome do elemento que será inserido
     * @return o elemento que foi adicionado
     */
    public static Element addElement(Element parent, String elementName) {
        return addElementNS(parent, null, elementName);
    }

    /**
     * Adiciona um elemento a um elemento pai.<br>
     * O elemento é adicionado sem valor.
     *
     * @param parent o elemento dentro do qual um elemento será inserido
     * @param namespaceURI poder ser null
     * @param qualifiedName o nome do elemento que será inserido
     * @return o elemento que foi adicionado
     */
    public static Element addElementNS(Node parent, String namespaceURI, String qualifiedName) {
        return MElementWrapper.addElementNS(parent, namespaceURI, qualifiedName);
    }

    private static boolean isVazio(String s) {
        return (s == null) || (s.length() == 0);
    }

    /**
     * Adiciona um elemento binario no formato BASE64 dentro do
     * elemento pai. O formato BASE64 é definido pelo RFC1521 do
     * RFC1521. Ele transforma um binário em uma string, um codificação
     * de 6 bits. Deste modo, um array binário ocupa 33% mais espaço no
     * formato BASE, contudo passa a ser uma string simples. É necessário
     * levar em consideração questões de gasto de memória e de custo de
     * conversão de binário para string e string para binário ao se decidir
     * pelo uso deste formato.
     *
     * @param pai o elemento dentro do qual um elemento será inserido
     * @param nome o nome do elemento que será inserido
     * @param value o array binário do elemento adicionado
     * (a ser convertido p/ BASE64)
     * @return o elemento que foi adicionado
     */
    public static Element addElement(Element pai, String nome, byte[] value) {
        return addElement(pai, nome, MElementWrapper.toBASE64(value));
    }

    /**
     * Adiciona um elemento com valor a um elemento pai.<br>
     *
     * @param pai o elemento dentro do qual um elemento será inserido
     * @param nome o nome do elemento que será inserido
     * @param value o valor <code>double</code> do elemento adicionado
     * (é feita conversão para String)
     * @return o elemento que foi adicionado
     */
    public static Element addElement(Element pai, String nome, double value) {
        return addElement(pai, nome, Double.toString(value));
    }

    /**
     * Adiciona um elemento com valor a um elemento pai.<br>
     *
     * @param pai o elemento dentro do qual um elemento será inserido
     * @param nome o nome do elemento que será inserido
     * @param value o valor <code>int</code> do elemento adicionado
     * (é feita conversão para String)
     * @return o elemento que foi adicionado
     */
    public static Element addElement(Element pai, String nome, int value) {
        return addElement(pai, nome, Integer.toString(value));
    }

    /**
     * Adiciona um elemento com valor a um elemento pai.<br>
     *
     * @param pai o elemento dentro do qual um elemento será inserido
     * @param nome o nome do elemento que será inserido
     * @param value o valor <code>long</code> do elemento adicionado
     * (é feita conversão para String)
     * @return o elemento que foi adicionado
     */
    public static Element addElement(Element pai, String nome, long value) {
        return addElement(pai, nome, Long.toString(value));
    }

    /**
     * Adiciona um elemento binario no formato BASE64 dentro do
     * elemento pai até esgotar as InputStream.
     * O formato BASE64 é definido pelo RFC1521 do
     * RFC1521. Ele transforma um binário em uma string, um codificação
     * de 6 bits. Deste modo, um array binário ocupa 33% mais espaço no
     * formato BASE, contudo passa a ser uma string simples. É necessário
     * levar em consideração questões de gasto de memória e de custo de
     * conversão de binário para string e string para binário ao se decidir
     * pelo uso deste formato.
     *
     * @param pai o elemento dentro do qual um elemento será inserido
     * @param nome o nome do elemento que será inserido
     * @param in Stream com os dados a serem convertidos p/ BASE64.
     * @return o elemento que foi adicionado
     *
     * @throws IOException Se houver erro na leitura dos dados
     */
    public static Element addElement(Element pai, String nome, InputStream in) throws IOException {
        return addElement(pai, nome, MElementWrapper.toBASE64(in));
    }

    /**
     * Adiciona um elemento com valor a um elemento pai.<br>
     *
     * @param pai o elemento dentro do qual um elemento será inserido
     * @param nome o nome do elemento que será inserido
     * @param value o valor <code>String</code> do elemento adicionado
     * @return o elemento que foi adicionado
     */
    public static Element addElement(Element pai, String nome, String value) {
        return MElementWrapper.addElement(pai, nome, value);
    }

    public static Element addElement(Element pai, String nome, Timestamp time) {
        String valor = null;
        if (time != null) {
            valor = ConversorToolkit.printTimeStamp(time);
        }
        return addElement(pai, nome, valor);
    }

    public static Element addElement(Element pai, String nome, java.util.Date data) {

        String valor = null;
        if (data != null) {
            valor = ConversorToolkit.printDate(data);
        }
        return addElement(pai, nome, valor);
    }

    public static Element addFormatado(Element pai, String nome, double valor) {
        return addElement(pai, nome, ConversorToolkit.printNumber(valor, 2));
    }

    public static Element addFormatado(
            Element pai,
            String nome,
            double value,
            int nrCasasDecimais) {
        return addElement(pai, nome, ConversorToolkit.printNumber(value, nrCasasDecimais));
    }

    public static Element addFormatado(Element parent, String elementName, int value) {
        return addElement(parent, elementName, ConversorToolkit.printNumber(value, 0));
    }

    public static Element addTimeStampMinuto(Element pai, String nome, Timestamp time) {

        String valor = null;
        if (time != null) {
            valor = ConversorToolkit.printTimeStampMinuto(time);
        }
        return addElement(pai, nome, valor);
    }

    /**
     * Metodo utilizado para colocar um elemento (e seu conteúdo) dentro
     * de outro elemento, podendo ser usado um outro nome ao invés do nome
     * do elemento sendo copiado. O elemento é copiado ao final da lista
     * de elementos (append) do elemento de destino.
     *
     * @param pai elemento que receberá um novo elemento com o conteúdo do
     * elemento <code>no</code>
     * @param no elemento cujo conteúdo será colocado dentro de um elemento
     * de nome <code>novoNome</code>, que será colocado dentro do
     * elemento <code>pai</code>
     * @param novoNome nome do elemento que receberá o conteúdo do elemento
     * <code>no</code> e que será colocado dentro de
     * <code>pai</code>; se for <code>null</code>, é usado
     * o nome do elemento <code>no</code>
     */
    public static void copyElement(Element pai, Element no, String novoNome) {
        MElementWrapper.copyElement(pai, no, novoNome);
    }

    /**
     * Metodo utilizado para colocar apenas o conteúdo de um elemento dentro
     * de outro elemento. Os elementos contidos no elemento <code>no</code>
     * serão colocados ao final da lista de elementos elemento <code>pai</code>
     * (append).
     *
     * @param pai elemento que o conteúdo do outro elemento
     * @param no elemento cujo conteúdo será colocado dentro do elemento pai
     */
    public static void copyElement(Element pai, Element no) {
        MElementWrapper.copyElement(pai, no);
    }

    /**
     * Conta o número de ocorrências de elementos com determinado nome
     * dentro de um elemento pai.
     *
     * @param pai o elemento dentro do qual as ocorrências serão contadas
     * @param nomeNo o nome de elementos dentro do elemento <i>pai</i>
     * @return o número de ocorrências do elemento
     */
    public static int count(Element pai, String nomeNo) {
        int qtd = 0;
        Node node = pai.getFirstChild();

        while (node != null) {
            if ((node.getNodeType() == Node.ELEMENT_NODE)
                    && (nomeNo == null || node.getNodeName().equals(nomeNo))) {
                qtd++;
            }
            node = node.getNextSibling();
        }

        return qtd;
    }

    /**
     * Conta o número de ocorrências de Element filhos.
     *
     * @param pai elemento que terá seu filhos contados.
     * @return -
     */
    public static int countElement(Element pai) {
        int qtd = 0;
        Node node = pai.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                qtd++;
            }
            node = node.getNextSibling();
        }
        return qtd;
    }

    public static byte[] getByteBASE64(Element pai, String nome) {
        String valor = getValor(pai, nome);
        if (valor != null) {
            return null;
        } else {
            return MElementWrapper.fromBASE64(null);
        }
    }

    public static void getByteBASE64(Element pai, String nome, OutputStream out)
            throws IOException {
        String valor = getValor(pai, nome);

        if (valor == null) {
            throw new IOException(
                    "A conversão BASE64 não foi possível: "
                            + "o elemento '"
                            + nome
                            + "' não foi encontrado");
        }
        MElementWrapper.fromBASE64(valor, out);
    }

    public static java.util.Date getDate(Element pai, String nomeElemento) throws ParseException {
        String valor = getValor(pai, nomeElemento);
        if (!isVazio(valor)) {
            return ConversorToolkit.getDateFromData(valor);
        }
        return null;
    }

    public static java.sql.Date getDateSQL(Element pai, String nomeElemento)
            throws ParseException {
        String valor = getValor(pai, nomeElemento);
        if (!isVazio(valor)) {
            return new java.sql.Date(ConversorToolkit.getDateFromData(valor).getTime());
        }
        return null;
    }

    /**
     * A partir do contexto informado procura o element no xPath.
     *
     * @param contexto Ponte de incio da busca.
     * @param xPath Path a ser
     * @return O elemento no destino ou null se o xPath não existir
     *
     * @throws RuntimeException Se o node no xPath não for um Element
     * @see XPathToolkit
     */
    public static Element getElement(Node contexto, String xPath) throws RuntimeException {
        return XPathToolkit.selectElement(contexto, xPath);
    }

    /**
     * A partir do contexto informado procura o Node no xPath (Elemento,
     * atributo, etc.).
     *
     * @param contexto Ponte de incio da busca.
     * @param xPath Path a ser
     * @return O Node no destino ou null se o xPath não existir
     *
     * @see XPathToolkit
     */
    public static Node getNode(Node contexto, String xPath) {
        return XPathToolkit.selectNode(contexto, xPath);
    }

    /**
     * Busca elementos dentro de um elemento pai.
     *
     * @param pai o elemento dentro do qual será feita a busca
     * @param nome o nome de elementos dentro do elemento pai
     * @return todos os elementos de nome <i>nomeElemento</i> dentro do
     * elemento pai; se nenhum elemento foi encontrado, retorna um
     * array com zero posições
     */
    @SuppressWarnings("SuspiciousToArrayCall")
    public static Element[] getElementos(Element pai, String nome) {
        List<Node> lista = new ArrayList<>();
        Node node = pai.getFirstChild();

        while (node != null) {
            if ((node.getNodeType() == Node.ELEMENT_NODE) && (node.getNodeName().equals(nome))) {
                lista.add(node);
            }

            node = node.getNextSibling();
        }

        //monta o array de Element
        return lista.toArray(new Element[lista.size()]);
    }

    public static int getInt(Node contexto, String xPath) {
        return ConversorToolkit.getInt(getValorNotNull(contexto, xPath));
    }

    public static long getLong(Node contexto, String xPath) {
        return Long.parseLong(getValorNotNull(contexto, xPath));
    }

    public static String getValor(Node pai, String xPath) {
        return getValorTexto(getNode(pai, xPath));
    }

    static String getValorNotNull(Node contexto, String xPath) {

        Node no = getNode(contexto, xPath);
        if (no == null) {
            throw new NullPointerException(
                    "xPath '" + xPath + "' não existe em '" + XPathToolkit.getFullPath(contexto) + "'");
        }
        String valor = getValorTexto(no);
        if (valor == null) {
            throw new NullPointerException(
                    "No '" + xPath + "' está vazio (fullPath=" + XPathToolkit.getFullPath(no) + ")");
        }
        return valor;
    }

    /**
     * Busca os valores de elementos dentro de um elemento pai.
     *
     * @param pai o elemento onde o valor de outro elemento será buscado
     * @param nomeElemento o nome de um elemento dentro do elemento <i>pai</i>
     * @return uma string com o valor do elemento
     */
    public static String[] getValores(Element pai, String nomeElemento) {
        String str;
        List<String> lista = null;
        Node node = pai.getFirstChild();

        //obtem os valores de todos elementos com nome 'nomeElemento'
        while (node != null) {
            if ((node.getNodeType() == Node.ELEMENT_NODE)
                    && (node.getNodeName().equals(nomeElemento))) {

                str = getValorTexto(node);
                if (str != null) { //elemento texto?
                    if (lista == null) {
                        lista = new ArrayList<>();
                    }
                    lista.add(str);
                }
            }

            node = node.getNextSibling();
        }

        //monta o array de String
        if (lista == null) {
            return LISTA_VAZIA;
        }
        return lista.toArray(new String[lista.size()]);
    }

    /**
     * Retorna o valor do no passado como parâmetro. Se for um Element
     * retorna o texto imediatamente abaixo.
     *
     * @param no do qual será extraido o texto
     * @return pdoe ser null
     */
    public static String getValorTexto(Node no) {
        return MElement.getValorTexto(no);
    }

    /**
     * Cria um documento XML.
     *
     * @return um documento sem nós
     */
    public static Document newDocument() {
        return MElementWrapper.newDocument();
    }

    /**
     * Cria um elemento XML em um novo documento.
     *
     * @param elementName o nome do elemento que será criado
     * @return o elemento que foi criado
     *
     * @see MElement#newInstance
     * @deprecated Utilizar MElement.newInstance
     */
    public static Element newRootElement(String elementName) {
        return MElementWrapper.newRootElement(elementName);
    }

    /**
     * Cria um elemento XML em um novo documento para um determinado
     * namespace.
     *
     * @param namespaceURI Tipicamente o name space possui o formato de uma
     * URL (não é obrigatório) no formato, por exemplo,
     * http://www.miranteinfo.com/sisfinanceiro/cobranca/registraPagamento.
     * @param qualifiedName o nome do elemento que será criado. Pode conter
     * prefixo (ex.: "fin:ContaPagamento").
     * @return o elemento que foi criado
     *
     * @see MElement#newInstance
     * @deprecated Utilizar MElement.newInstance
     */
    public static Element newRootElement(String namespaceURI, String qualifiedName) {
        return MElementWrapper.newRootElement(namespaceURI, qualifiedName);
    }

    /**
     * Faz parse de uma InputStream.
     *
     * @see MParser#parseToElement(InputSource, boolean, boolean, EntityResolver)
     * @deprecated Utilizar MElement.parse
     */
    public static Element parse(InputStream in, boolean namespaceAware, boolean validating)
            throws SAXException, IOException {
        return MParser.parseToElement(new InputSource(in), namespaceAware, validating, null);
    }

    /**
     * Verifica se existe um filho com o nome informado.
     *
     * @param pai No de base da pesquisa.
     * @param nomeElemento No do elemento a ser procurado
     * @return boolean Se existir.
     */
    public static boolean possuiElement(Element pai, String nomeElemento) {
        return getElement(pai, nomeElemento) != null;
    }

    /**
     * Gera um arquivo XML a partir do elemento fornecido.<br>
     * Não insere quebra de linha após cada elemento e não indenta os níveis
     * do XML.
     *
     * @param out uma stream de saída de dados
     * @param root o elemento raiz da estrutura XML que será impressa
     */
    public static void printDocument(PrintWriter out, Element root) {
        XMLToolkitWriter.printDocument(out, root, true);
    }

    /**
     * Gera um arquivo XML a partir do elemento fornecido.<br>
     * Insere uma quebra de linha após cada elemento e indenta os níveis do XML.
     *
     * @param out uma stream de saída de dados
     * @param root o elemento raiz da estrutura XML que será impressa
     */
    public static void printDocumentIndentado(PrintStream out, Element root) {
        PrintWriter wr = new PrintWriter(out);
        XMLToolkitWriter.printDocumentIndentado(wr, root, true);
        wr.flush();
    }

    /**
     * Gera um arquivo XML a partir do elemento fornecido.<br>
     * Insere uma quebra de linha após cada elemento e indenta os níveis do XML.
     *
     * @param out uma stream de saída de dados
     * @param root o elemento raiz da estrutura XML que será impressa
     */
    public static void printDocumentIndentado(PrintWriter out, Element root) {
        XMLToolkitWriter.printDocumentIndentado(out, root, true);
    }
}
