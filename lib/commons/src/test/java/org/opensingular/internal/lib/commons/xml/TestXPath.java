/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.internal.lib.commons.xml;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.opensingular.internal.lib.commons.xml.XmlUtil.isNodeTypeElement;

/**
 * JUnit para testar a class XPathToolkit.
 *
 * @author Daniel C. Bordin
 */
public class TestXPath {

    /** XML utilizado para os diversos teste de procurar e nome */
    private MElement raiz_;

    /**
     * Method chamado pelo JUnit antes de cada método testXXXX para
     * que esse estabeleça o ambiente do teste.
     */
    @Before
    public void setUp() throws Exception {
        if (raiz_ != null) {
            return;
        }
        //@formatter:off
        String xml =
                "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                        + "<pedido cliente=\"Paulo\">          \n"
                        + "    <cd cod=\"1\">                  \n"
                        + "       <grupo>Pato Fu</grupo>       \n"
                        + "       <nome>Acustico</nome>        \n"
                        + "       <ano>2002</ano>              \n"
                        + "       <faixa>Minha Musica</faixa>  \n"
                        + "       <faixa>Sua Musica</faixa>    \n"
                        + "       <faixa>Nossa Musica</faixa>  \n"
                        + "    </cd>                           \n"
                        + "    <cd cod=\"4\">                  \n"
                        + "       <grupo>Paralamas</grupo>     \n"
                        + "       <nome>9 Luas</nome>          \n"
                        + "       <ano>1999</ano>              \n"
                        + "       <faixa>9 Luas</faixa>        \n"
                        + "       <faixa>7 Luas</faixa>        \n"
                        + "    </cd>                           \n"
                        + "    <cd cod=\"6\">                  \n"
                        + "       <grupo>U2</grupo>            \n"
                        + "       <nome>Zooropa</nome>         \n"
                        + "       <ano>1997</ano>              \n"
                        + "       <faixa>Babyface</faixa>      \n"
                        + "       <faixa>Numb</faixa>          \n"
                        + "       <faixa>Lemon</faixa>         \n"
                        + "    </cd>                           \n"
                        + "    <versao>2003</versao>           \n"
                        + "</pedido>                           \n"
                        + "";
        //@formatter:on
        InputStream in = new ByteArrayInputStream(xml.getBytes());
        raiz_ = MParser.parse(in, false, false);
    }

    /**
     * Verifica se esta encontrando o elemento correto via XPath e
     * para cada elemento obtido, verifica se o path montado via
     * getFullPath aponta corretamente para o elemento original.
     */
    @Test
    public void testSingleFind() throws Exception {
        Node cd = raiz_.getFirstChild();
        while (!isNodeTypeElement(cd)) {
            cd = cd.getNextSibling();
        }

        MElementResult rsCd = new MElementResult(raiz_, "cd");
        rsCd.next();

        checkPath(true, raiz_, "cd/@cod", "1");
        checkPath(true, raiz_, "cd/ano", "2002");
        checkPath(true, raiz_, "/cd/ano", null);

        // O first child é um nó de texto (quebra de linha)
        checkPath(true, raiz_.getFirstChild(), "/pedido/cd/ano", "2002");

        checkPath(true, cd, "/pedido/cd/ano", "2002");
        checkPath(true, cd, "/ano", null);
        checkPath(true, cd, "ano", "2002");
        checkPath(true, cd, "@cod", "1");
        checkPath(true, cd, "@xpto", null);

        checkPath(true, rsCd, "/pedido/cd/ano", "2002");
        checkPath(true, rsCd, "/ano", null);
        checkPath(true, rsCd, "ano", "2002");
        checkPath(true, rsCd, "@cod", "1");
        checkPath(true, rsCd, "@xpto", null);


        checkPath(false, raiz_, "/pedido/cd[2]/nome", "9 Luas");
        checkPath(false, raiz_, "/pedido[1]/cd[2]/nome", "9 Luas");
        checkPath(false, raiz_, "/pedido/cd[2]/faixa[2]", "7 Luas");
        checkPath(false, raiz_, "/pedido/cd[@cod=6]/nome", "Zooropa");
        checkPath(false, raiz_, "/pedido/cd[@cod=6]/nome/../ano", "1997");
        checkPath(false, raiz_, "/pedido/cd[ano=1997]/faixa[last()]", "Lemon");
        checkPath(false, raiz_, "/pedido/cd[ano=1997]/faixa[last()]", "Lemon");
        checkPath(false, raiz_, "cd[2]/nome", "9 Luas");
        checkPath(false, raiz_, "cd[@cod=6]/nome", "Zooropa");
        checkPath(false, raiz_, "cd[@cod=6]/nome/../ano", "1997");
        checkPath(false, raiz_, "cd[ano=1997]/faixa[last()]", "Lemon");
        checkPath(true, raiz_, "@cliente", "Paulo");
        checkPath(false, raiz_, "cd[1]/@cod", "1");
        checkPath(false, raiz_, "cd[2]/@cod", "4");
        checkPath(false, raiz_, "cd[2]/@xpto", null);
    }

    /**
     * Testa a exatidão e a performance de consulta que retorna vários
     * Elements.
     */
    @Test
    public void testResult() throws Exception {
        checkResult(true, "cd", 3);
        checkResult(true, null, 4);
        checkResult(false, "cd/grupo", 3);
        checkResult(false, "cd/faixa", 8);
        checkResult(false, "cd[grupo='Pato Fu']", 1);
        checkResult(false, "cd[@cod=4]/faixa", 2);
    }

    private void checkResult(boolean otimizado, String xPath, int qtdEsperado) {

        long inicio = System.currentTimeMillis();
        for (int rep = 0; rep < 1000; rep++) {
            MElementResult rs = raiz_.selectElements(xPath);
            assertEquals(rs.isBeforeFirst(), true);
            assertEquals(rs.isAtualValido(), false);
            assertEquals(rs.isAfterLast(), false);

            int qtd = 0;
            while (rs.next()) {
                assertEquals(rs.isBeforeFirst(), false);
                assertEquals(rs.isAtualValido(), true);
                assertEquals(rs.isAfterLast(), false);
                qtd++;
                rs.getTagName();
            }
            assertEquals(rs.isBeforeFirst(), false);
            assertEquals(rs.isAtualValido(), false);
            assertEquals(rs.isAfterLast(), true);

            assertEquals("Qtd de itens encontrados diferentes do lidos", qtdEsperado, qtd);
        }
        long fim = System.currentTimeMillis();

        System.out.println("(base=1000X) xPath='" + xPath + "' percorrimento= " + (fim - inicio) + "ms");
        if (otimizado && (fim - inicio > 100)) {
            fail("O xPath '" + xPath + "' deveria estar otimizado para executar em menos de " + (fim - inicio) + "ms");
        }
    }


    /**
     * Verifica o tempo para busca via xPath. Quando especificado
     * comparação com o XMLToolkit, a diferença de tempo deverá de 100
     * vezes mais rápido para o XMLToolkit puro.
     *
     * @param otimizado Se compara a performa da API XPath com chamada via
     *                 XPathToolkit. A diferença deverá de 100 vezes mais
     *                 rápido.
     * @param ref Elemento a partir do qual será iniciada a busca.
     * @param xPath Path do no a ser procurado
     */
    //@formatter:off
    /**
    private void checkPerformance(boolean otimizado, Node ref, String xPath) throws Exception {
        int base = 1000;
        int qtd = 1;
        long inicio = System.currentTimeMillis();
        for (int i = qtd * base; i != 0; i--) {
            XPathToolkit.selectNode(ref, xPath);
        }
        long fim = System.currentTimeMillis();
        long mediaToolkit = Math.max((fim - inicio) / qtd, 1);
        if (!otimizado) {
            System.out.println("(base=" + base + "X) kit=" + mediaToolkit + "ms :" + xPath);
            return;
        }
        inicio = System.currentTimeMillis();
        for (int i = qtd * base; i != 0; i--) {
            XPathAPI.selectSingleNode(ref, xPath);
        }
        fim = System.currentTimeMillis();
        long mediaXPath = Math.max((fim - inicio) / qtd, 1);
        long extra = (100 * mediaXPath) / mediaToolkit - 100;
        System.out.println(
                "(base="
                        + base
                        + "X) xPath="
                        + mediaXPath
                        + "ms  kit="
                        + mediaToolkit
                        + "ms :"
                        + xPath);
        if (otimizado && (extra < 100)) {
            fail("Otimização menor do que 100% para o caminho" + xPath);
        }

    }
    //@formatter:off
**/
    private void checkPath(boolean otimizado, Node no, String xPath, String valor)
            throws Exception {
        if (no instanceof EWrapper) {
            no = ((EWrapper) no).getOriginal();
        }
        checkFind(no, xPath, valor);
        //checkPerformance(otimizado, no, xPath);
    }

    /**
     * Primeira procura o elemento de acordo xPAth fornecido, depois
     * verifica se o elemento pois o texto com o valor informado e
     * por último verifica se o getFullPath do elemento encontrado
     * leva ao próprio elemento.
     * @param no Ponto de base da pesquisa
     * @param xPath Query XPAth válida
     * @param valor Valor a ser encontrado no Element
     * @throws TransformerException -
     */
    private void checkFind(Node no, String xPath, String valor) throws TransformerException {
//        Node resultAPI = XPathAPI.selectSingleNode(no, xPath);
//        String vResultAPI = MElementWrapper.getValorTexto(resultAPI);
//        if (!isIgual(valor, vResultAPI)) {
//            fail(
//                    "xPath/valor errado: Utilizando API apache a pesquisa '"
//                            + xPath
//                            + "' a partir de '"
//                            + XPathToolkit.getFullPath(no)
//                            + "' encontrou '"
//                            + vResultAPI
//                            + "' em vez de '"
//                            + valor
//                            + "'");
//        }

        Node result = XPathToolkit.selectNode(no, xPath);
        String vResult = MElementWrapper.getValorTexto(result);
        if (!isIgual(valor, vResult)) {
            fail(
                    "XPathToolkit.selectNode errado: resultado da pesquisa "
                            + xPath
                            + " a partir de "
                            + XPathToolkit.getFullPath(no)
                            + " encontrou '"
                            + vResult
                            + "' em vez de '"
                            + valor
                            + "'");
        }

        if (vResult == null) {
            //System.out.println(xPath + "->null");
            return;
        }

        String fullPath = XPathToolkit.getFullPath(result);
        String relativePath = XPathToolkit.getFullPath(result, no);

        //System.out.println(xPath + "->" + fullPath + "->" + relativePath);
        Node resultado2 = XPathToolkit.selectNode(no, fullPath);
        if (result != resultado2) {
            String fullPath2 = null;
            String vResult2 = null;
            if (resultado2 != null) {
                fullPath2 = XPathToolkit.getFullPath(resultado2);
                vResult2 = MElementWrapper.getValorTexto(resultado2);
            }
            fail(
                    "Ao aplicar fullPath do Elemento em uma pesquisa XPath, "
                            + "não encontrou o mesmo elemento:\noriginal("
                            + fullPath
                            + "):"
                            + vResult
                            + "\n"
                            + "obtido("
                            + fullPath2
                            + "): "
                            + vResult2);
        }

        Node resultado3 = XPathToolkit.selectNode(no, relativePath);
        if (result != resultado3) {
            String fullPath3 = null;
            String vResult3 = null;
            if (resultado3 != null) {
                fullPath3 = XPathToolkit.getFullPath(resultado3);
                vResult3 = MElementWrapper.getValorTexto(resultado3);
            }
            fail(
                    "Ao aplicar relativePath do Elemento em uma pesquisa XPath, "
                            + "não encontrou o mesmo elemento:\noriginal("
                            + relativePath
                            + "):"
                            + vResult
                            + "\n"
                            + "obtido("
                            + fullPath3
                            + "): "
                            + vResult3);
        }

    }

    /**
     * Verifica se os dois objeto são iguais. Inclusive no caso de nulos.
     * @param o1 -
     * @param o2 -
     * @return true se identicos
     */
    private boolean isIgual(Object o1, Object o2) {
        return ((o1 == null) && (o2 == null)) || ((o1 != null) && o1.equals(o2));
    }

    @Test
    public void testGetNomeTipo(){
        MDocument document = MDocument.newInstance();
        MElement element = document.createRaiz("pai");

        Comment comment = document.createComment("comentario aqui");
        Text textNode = document.createTextNode("textNode");
        DocumentFragment documentFragment = document.createDocumentFragment();

        Assert.assertEquals("Comment Node", XPathToolkit.getNomeTipo(comment));
        Assert.assertEquals("Document Node", XPathToolkit.getNomeTipo(document));
        Assert.assertEquals("Element Node", XPathToolkit.getNomeTipo(element));
        Assert.assertEquals("Text Node", XPathToolkit.getNomeTipo(textNode));
        Assert.assertEquals("Document Fragment Node", XPathToolkit.getNomeTipo(documentFragment));

        Assert.assertNull(XPathToolkit.getNomeTipo(null));
    }

    @Test
    public void testIsSimple(){
        MElement element = MElement.newInstance("pai");
        element.addElement("filho", "filhoVal");

        Assert.assertFalse(XPathToolkit.isSimples("//teste"));
        Assert.assertFalse(XPathToolkit.isSimples(":teste"));
        Assert.assertFalse(XPathToolkit.isSimples("::teste"));
        Assert.assertFalse(XPathToolkit.isSimples("*teste"));
        Assert.assertFalse(XPathToolkit.isSimples("[teste"));
        Assert.assertFalse(XPathToolkit.isSimples("teste]"));
        Assert.assertFalse(XPathToolkit.isSimples("te.ste"));
        Assert.assertTrue(XPathToolkit.isSimples("filho"));
    }

    @Test(expected = SingularException.class)
    public void testSelectNodeException(){
        MElement element = MElement.newInstance("pai");
        MElement filho = element.addElement("filho", "filhoVal");

        XPathToolkit.selectNode(filho, ".75481");
    }

    @Test
    public void testSelectElements(){
        MDocument document = MDocument.newInstance();
        MElement element = document.createRaiz("pai");

        MElement filho = element.addElement("filho", "filhoVal");
        MElementWrapper wrapper = new MElementWrapper(filho);

        Comment comment = document.createComment("comentario aqui");

        Assert.assertTrue(XPathToolkit.selectElements(wrapper, "test") instanceof MElementResult);
        Assert.assertTrue(XPathToolkit.selectElements(element.getNode("filho"), "comentario aqui") instanceof MElementResult);
        Assert.assertTrue(XPathToolkit.selectElements(element.getNode("filho"), null) instanceof MElementResult);
        Assert.assertTrue(XPathToolkit.selectElements(comment, "vazio") instanceof MElementResult);
    }

    @Test(expected = SingularException.class)
    public void testSelectNodeListException(){
        MDocument document = MDocument.newInstance();
        XPathToolkit.selectNodeList(document, ".erro");
    }

    @Test
    public void testSelectNodeIterator(){
        MDocument document = MDocument.newInstance();
        MElement element = document.createRaiz("pai");

        element.addElement("filho", "filhoVal");
        MElementWrapper wrapper = new MElementWrapper(element);

        NodeIterator iterator = XPathToolkit.selectNodeIterator(wrapper, "filho");

        Assert.assertNotNull(iterator);
        Assert.assertEquals("filhoVal", iterator.nextNode().getFirstChild().getNodeValue());
    }

    @Test(expected = SingularException.class)
    public void testSelectNodeIteratorException(){
        MDocument document = MDocument.newInstance();
        XPathToolkit.selectNodeIterator(document, ".erro");
    }
}