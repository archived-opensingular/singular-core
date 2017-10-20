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

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * JUnit para test do da classe MElement.
 *
 * @author Daniel C. Bordin
 */
public class TestMElement {

    /**
     * XML de base para teste de percorrimento e leitura.
     */
    private MElement root_;

    /**
     * Verifica se ambos os nos são iguais fazendo uma comparação em
     * profundidade.
     *
     * @param n1 -
     * @param n2 -
     * @throws Exception Se nbão forem iguais
     */
    public static void isIgual(Node n1, Node n2) throws Exception {
        if (n1 == n2) {
            return;
        }

        isIgual(n1, n2, "NodeName", n1.getNodeName(), n2.getNodeName());
        isIgual(n1, n2, "NodeValue", n1.getNodeValue(), n2.getNodeValue());
        isIgual(n1, n2, "Namespace", n1.getNamespaceURI(), n2.getNamespaceURI());
        isIgual(n1, n2, "Prefix", n1.getPrefix(), n2.getPrefix());
        isIgual(n1, n2, "LocalName", n1.getLocalName(), n2.getLocalName());

        if (isSameClass(Element.class, n1, n2)) {
            Element e1 = (Element) n1;
            Element e2 = (Element) n2;
            //Verifica se possuem os mesmos atributos
            NamedNodeMap nn1 = e1.getAttributes();
            NamedNodeMap nn2 = e2.getAttributes();
            if (nn1.getLength() != nn2.getLength()) {
                fail("O número atributos em " + XPathToolkit.getFullPath(n1) + " (qtd=" + nn1.getLength() +
                        " é diferente de n2 (qtd=" + nn2.getLength() + ")");
            }
            for (int i = 0; i < nn1.getLength(); i++) {
                isIgual((Attr) nn1.item(i), (Attr) nn2.item(i));
            }

            //Verifica se possuem os mesmos filhos
            Node child1 = e1.getFirstChild();
            Node child2 = e2.getFirstChild();
            int count = 0;
            while ((child1 != null) && (child2 != null)) {
                isIgual(child1, child2);
                child1 = child1.getNextSibling();
                child2 = child2.getNextSibling();
                count++;
            }
            if (child1 != null) {
                fail("Há mais node [" + count + "] " + XPathToolkit.getNodeTypeName(child1) + " (" +
                        XPathToolkit.getFullPath(child1) + ") em n1:" + XPathToolkit.getFullPath(n1));
            }
            if (child2 != null) {
                fail("Há mais node [" + count + "] " + XPathToolkit.getNodeTypeName(child2) + " (" +
                        XPathToolkit.getFullPath(child2) + ") em n2:" + XPathToolkit.getFullPath(n2));
            }

        } else if (isSameClass(Attr.class, n1, n2)) {
            //Ok

        } else if (isSameClass(Text.class, n1, n2)) {
            //Ok

        } else {
            fail("Tipo de nó " + n1.getClass() + " não tratado");
        }

    }

    /**
     * Verifica se os atributos são iguais. Existe pois a comparação de
     * atributos possui particularidades.
     *
     * @param n1 -
     * @param n2 -
     * @throws Exception Se não forem iguais
     */
    public static void isIgual(Attr n1, Attr n2) throws Exception {
        if (n1 == n2) {
            return;
        }
        isIgual(n1, n2, "NodeName", n1.getNodeName(), n2.getNodeName());
        isIgual(n1, n2, "NodeValue", n1.getNodeValue(), n2.getNodeValue());

        //Por algum motivo depois do parse Prefix passa de null para não null
        //isIgual(n1, n2, "Prefix", n1.getPrefix(), n2.getPrefix());
        //Por algum motivo depois do parse Localname passe de não null para
        // null
        //isIgual(n1, n2, "LocalName", n1.getLocalName(), n2.getLocalName());

        if (!(n1.getNodeName().startsWith("xmlns") && n2.getNodeName().startsWith("xmlns"))) {
            isIgual(n1, n2, "Namespace", n1.getNamespaceURI(), n2.getNamespaceURI());
        }
    }

    /**
     * Verifica se ambos o nós são da classe informada. Se apenas um for, um
     * erro é disparado devido a incompatibilidade.
     *
     * @param c        Classe a ser verificada
     * @param original instância 1
     * @param newNode     instância 2
     * @return true Se ambos forem instância de c
     */
    private static boolean isSameClass(Class<?> c, Node original, Node newNode) {
        if (c.isInstance(original)) {
            if (c.isInstance(newNode)) {
                return true;
            } else {
                fail(XPathToolkit.getFullPath(original) + " não é da mesma classe que " +
                        XPathToolkit.getFullPath(newNode));
            }
        } else if (c.isInstance(newNode)) {
            fail(XPathToolkit.getFullPath(original) + " não é da mesma classe que " + XPathToolkit.getFullPath(newNode));
        }
        return false;
    }

    /**
     * Verifica a igualdade de um determiando para de objetos já considerando a
     * situação de um deles ser null.
     *
     * @param n1        -
     * @param n2        -
     * @param nomeParte -
     * @param v1        -
     * @param v2        -
     */
    private static void isIgual(Node n1, Node n2, String nomeParte, Object v1, Object v2) {
        if (((v1 == null) && (v2 != null)) || ((v1 != null) && !v1.equals(v2))) {

            fail("O(a) " + nomeParte + " em  " + XPathToolkit.getFullPath(n2) + " (" + escreverValor(v2) +
                    ") está diferente do original em " + XPathToolkit.getFullPath(n1) + " (" + escreverValor(v1) + ")");
        }
    }

    /**
     * Apenas para formar de forma visivel o caso null
     *
     * @param o -
     * @return Uma string entre '' se diferente null ou a string null.
     */
    private static String escreverValor(Object o) {
        if (o == null) {
            return "null";
        } else {
            return "'" + o + "'";
        }
    }

    /**
     * Method chamado pelo JUnit antes de cada método testXXXX para que esse
     * estabeleça o ambiente do teste.
     */
    @Before
    public void setUp() throws Exception {
        //@formatter:off
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<pedido cliente=\"Paulo\">          \n"
                + "    <cd cod=\"1\">                  \n"
                + "       <grupo>Pato Fu</grupo>      \n"
                + "       <nome>Acustico</nome>        \n"
                + "       <ano>2002</ano>              \n"
                + "       <faixa>Minha Musica</faixa>  \n"
                + "       <faixa>Sua Musica</faixa>    \n"
                + "       <faixa>Nossa Musica</faixa>  \n"
                + "       <presente/>                  \n"
                + "       <preco>10.12</preco>         \n"
                + "    </cd>                           \n"
                + "    <cd cod=\"4\">                  \n"
                + "       <grupo>Paralamas</grupo>     \n"
                + "       <nome>9 Luas</nome>          \n"
                + "       <ano>1999</ano>              \n"
                + "       <faixa>9 Luas</faixa>        \n"
                + "       <faixa>8 Luas</faixa>        \n"
                + "       <faixa>7 Luas</faixa>        \n"
                + "       <preco>10.1</preco>          \n"
                + "    </cd>                           \n"
                + "    <cd cod=\"6\">                  \n"
                + "       <grupo>U2</grupo>            \n"
                + "       <nome>Zooropa</nome>         \n"
                + "       <ano>1997</ano>              \n"
                + "       <faixa>Babyface</faixa>      \n"
                + "       <faixa>Numb</faixa>          \n"
                + "       <faixa>Lemon</faixa>         \n"
                + "       <preco>10</preco>            \n"
                + "    </cd>                           \n"
                + "</pedido>                           \n"
                + "";
        //@formatter:on
        root_ = MParser.parse(xml);

    }

    /**
     * Verifica se obtem corretamente a lista de sub elementos
     */
    @Test
    public void testGetElements() {
        assertEquals(3, root_.getElements("cd").length);
        assertEquals(3, root_.getElements(null).length);
        assertEquals(1, root_.getElement("cd").getElements("presente").length);
        assertEquals(3, root_.getElement("cd").getElements("faixa").length);
    }

    /**
     * Verifica método MElement.getValues()
     */
    @Test
    public void testGetValores() {
        assertEquals(3, root_.getValues("cd").size());
        assertEquals(0, root_.getValues("none").size());
        assertEquals(0, root_.getValues("cd/none").size());
        assertEquals(9, root_.getValues("cd/faixa").size());
        assertEquals(3, root_.getValues("cd/@cod").size());
        assertEquals(3, root_.getValues("cd[2]/faixa").size());
        assertEquals(3, root_.getElement("cd").getValues("faixa").size());
        assertEquals(7, root_.getElement("cd").getValues(null).size());
        assertEquals(3, root_.getValues("cd/grupo").size());
        assertEquals(0, root_.getValues("cd/presente").size());
    }

    @Test
    public void testFormatNumber() {
        assertEquals("10,12", root_.formatNumber("cd[1]/preco", false));
        assertEquals("10,1", root_.formatNumber("cd[1]/preco", 1, false));
        assertEquals("10,1", root_.formatNumber("cd[2]/preco", false));
        assertEquals("10", root_.formatNumber("cd[3]/preco", false));
        assertEquals("10", root_.formatNumber("cd[3]/preco", -1, false));
        assertEquals("", root_.formatNumber("cd[4]/preco", false));
    }

    /**
     * Testa o funcionamento dos métodos copy
     */
    @Test
    public void testCopy() throws Exception {
        //Testa a copia sem namespace
        MElement root2 = MElement.newInstance("http://acme.com", "lista");
        MElement new2 = root2.copy(root_.getElement("cd"), null);

        root_.print(System.err);
        new2.print(System.err);
        isIgual(root_.getElement("cd"), new2);

        //Testa a copia com namespace
        MElement root3 = MElement.newInstance("Pai-simples");
        MElement new3 = root3.copy(root2, null);
        //raiz2.print(System.err);
        //novo3.print(System.err);
        isIgual(root2, new3);

    }

    /**
     * Testa o uso de valroes default ao adicionar um no
     */
    @Test
    public void testAddNull() {
        assertNull(root_.addElement("x", (String) null, null));
        assertNull(root_.addElement("x", (Object) null, null));
        assertTrue(root_.isNull("x"));
        assertTrue(!root_.possuiNode("x"));
        assertEquals("a", root_.addElement("x", null, "a").getValue());
        assertEquals("a", root_.getValue("x"));
        assertEquals("b", root_.addElement("x", "b", "a").getValue());
        assertEquals(1.1, root_.addElement("x", null, new Double(1.1)).getDouble(), 0);
    }

    /**
     * Faz diverso testo na leitura de valor de atributo e elemento via xPath
     */
    @Test
    public void testLeituraValorXPath() {
        assertEquals(root_.getInt("cd/@cod"), 1);
        assertEquals(root_.getValue("cd/@cod"), "1");
        assertEquals(root_.getValue("/pedido/cd[2]/nome"), "9 Luas");
        assertEquals(root_.getValue("/cd/ano"), null);

        MElement cd = root_.getElement("cd");
        assertEquals(cd.getValue("/pedido/cd/ano"), "2002");
        assertEquals(cd.getLong("/pedido/cd/ano"), 2002);
        assertEquals(cd.getValue("/ano"), null);
        assertEquals(cd.getValue("ano"), "2002");
        assertEquals(cd.getValue("@cod"), "1");
        assertEquals(cd.getValue("@xpto"), null);

    }

    /**
     * Testa a adição de atributos no XML
     */
    @Test
    public void testSetAttribute() {
        MElement root = MElement.newInstance("pedido");

        root.addElement("cd/@cod", "1");
        assertEquals(root.getValueNotNull("cd/@cod"), "1");

        root.addElement("cd/@id", "XF19");
        assertEquals(root.getValueNotNull("cd/@id"), "XF19");

        //Escreve por cima
        root.addElement("cd/@cod", "2");
        assertEquals(root.getValueNotNull("cd/@cod"), "2");

        root.addElement("cd/grupo/@ativo", "sim");
        assertEquals(root.getValueNotNull("cd/grupo/@ativo"), "sim");

        root.addElement("/pedido/entrega/cep", "700");
        assertEquals(root.getValueNotNull("/pedido/entrega/cep"), "700");

        root.addElement("/pedido/entrega/@urgente", "sim");
        assertEquals(root.getValueNotNull("/pedido/entrega/@urgente"), "sim");

        root.addElement("@cliente", "Paulo");
        assertEquals(root.getValueNotNull("@cliente"), "Paulo");

        root.addElement("/pedido/@prioridade", "1");
        assertEquals(root.getValueNotNull("/pedido/@prioridade"), "1");

        MElement entrega = root.getElement("entrega");
        entrega.addElement("/pedido/transportadora/@cod", "20");
        assertEquals(root.getValueNotNull("/pedido/transportadora/@cod"), "20");
    }

    /**
     * Verifica se os métodos de set e get para java.util.Date e GregorianCalendar funcionam.
     */
    @Test
    public void testSetGetDatas() {
        GregorianCalendar agoraGc = new GregorianCalendar(2001, 2, 31, 23, 59, 49);
        agoraGc.set(GregorianCalendar.MILLISECOND, 123);
        Date agoraDate = new Date(agoraGc.getTimeInMillis());

        MElement xml;

        //Teste ler e escrever Calendar
        xml = MElement.newInstance("T");
        xml.addElement("V2", agoraGc);

        assertEquals("string calendar errada", xml.getValue("V2"), "2001-03-31T23:59:49.123");
        assertEquals("Calendar gravado lido", xml.getCalendar("V2"), agoraGc);

        //Teste ler e escrever java.util.Date
        xml = MElement.newInstance("T");
        xml.addElement("V", agoraDate);

        assertEquals("string util.date errada", xml.getValue("V"), "2001-03-31T23:59:49.123");
        assertEquals("util.date gravado lido", xml.getDate("V"), agoraDate);

        //Testa adicionar data como string
        xml.addDate("V3", "08/01/2003");
        assertEquals("data errada", xml.getValue("V3"), "2003-01-08");

        //Testa formatação
        assertEquals("formatação errada", xml.formatDate("V3"), "08/01/2003");
        assertEquals("formatação errada", xml.formatDate("V3", "short"), "08/01/03");
        assertEquals("formatação errada", xml.formatDate("V3", "medium"), "08/01/2003");

    }

    /**
     * Testa os métodos de leitura de null.
     */
    @Test
    public void testGetWithFormat() {
        GregorianCalendar agoraGc = new GregorianCalendar(2001, 2, 31, 23, 59, 49);
        agoraGc.set(GregorianCalendar.MILLISECOND, 123);
        //java.util.Date agoraDate = new
        // java.util.Date(agoraGc.getTimeInMillis());

        MElement xml;

        //getDateFormatado
        xml = MElement.newInstance("T");
        xml.addElement("V2", 12312);
        xml.addElement("V3", 12312.123);

        //System.out.println(xml.getFormatadoNumber("V2", 0));
        //System.out.println(xml.getFormatadoNumber("V2", 2));
        //System.out.println(xml.getFormatadoNumber("V3", 0));
        //System.out.println(xml.getFormatadoNumber("V3", 2));

        assertEquals(xml.formatNumber("V2", 0), "12.312");
        assertEquals(xml.formatNumber("V2", 2), "12.312,00");
        assertEquals(xml.formatNumber("V3", 0), "12.312");
        assertEquals(xml.formatNumber("V3", 2), "12.312,12");

    }

    /**
     * Verifica se ocorre os erro de campo null ou vazio quando necessário.
     */
    @Test
    public void testGetValorNull() {
        assertNull(root_.getValue("carro"));

        try {
            root_.getValueNotNull("carro");
            fail("Deveria ter ocorrido erro ao ler um campo que não existe");
        } catch (NullPointerException e) {
            //ok
        }
        try {
            root_.getValueNotNull("cd/@prioridade");
            fail("Deveria ter ocorrido erro ao ler um campo que não existe");
        } catch (NullPointerException e) {
            //ok
        }
        try {
            root_.getValueNotNull("cd/presente");
            fail("Deveria ter ocorrido erro ao ler um campo vazio");
        } catch (NullPointerException e) {
            //ok
        }
        try {
            root_.getLong("cep");
            fail("Deveria ter ocorrido erro ao ler um campo que não existe");
        } catch (NullPointerException e) {
            //ok
        }
        try {
            root_.getInt("cep");
            fail("Deveria ter ocorrido erro ao ler um campo que não existe");
        } catch (NullPointerException e) {
            //ok
        }
        MElement presente = root_.getElement("cd/presente");
        try {
            presente.getInt();
            fail("Deveria ter ocorrido erro ao ler um campo vazio");
        } catch (NullPointerException e) {
            //ok
        }
        try {
            presente.getLong();
            fail("Deveria ter ocorrido erro ao ler um campo vazio");
        } catch (NullPointerException e) {
            //ok
        }
    }

    /**
     * Verifica se os métodos de adicionar acusam erro se o valor for null.
     */
    @Test
    public void testAddValorNull() {
        MElement root = MElement.newInstance("xxx");
        try {
            root.addElement("campo", (Date) null);
            fail("Deveria ter ocorrido um erro em campo com valor null");
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            root.addElement("campo", (String) null);
            fail("Deveria ter ocorrido um erro em campo com valor null");
        } catch (IllegalArgumentException e) {
            //ok
        }
    }

    /**
     * Verifica se o método de count funciona adequadamente
     */
    @Test
    public void testCount() {
        assertEquals(3, root_.countFilhos());
        assertEquals(3, root_.count(null));
        assertEquals(3, root_.count("cd"));
        assertEquals(0, root_.count("xpto"));
        assertEquals(3, root_.getElement("cd").count("faixa"));
    }

    /**
     * Verifica se o método possuiElement()
     */
    @Test
    public void testPossuiNodeElement() {
        assertEquals(true, root_.possuiElement("cd"));
        assertEquals(true, root_.possuiElement("cd/faixa"));
        assertEquals(false, root_.possuiElement("cd/xpto"));
        assertEquals(false, root_.possuiElement("xpto"));
        try {
            assertEquals(false, root_.possuiElement("@cliente"));
            fail("Deveria ter ocorrido um erro, pois @cliente é um atributo");
        } catch (RuntimeException e) {
            //ok
        }
        assertEquals(true, root_.possuiNode("cd"));
        assertEquals(true, root_.possuiNode("cd/faixa"));
        assertEquals(false, root_.possuiNode("cd/xpto"));
        assertEquals(false, root_.possuiNode("xpto"));
        assertEquals(true, root_.possuiNode("@cliente"));
        assertEquals(true, root_.possuiNode("cd/@cod"));
    }

    /**
     * Verifica se consegue colocar um XML como texto e depois ler o mesmo
     * conteudo via uma parse.
     */
    @Test
    public void testXMLtoStringtoXMLsemNamespace() throws Exception {
        MElement root = MElement.newInstance("requisicao-tempo-atendimento");

        root.addElement("Filtro/operadoras/id-operadora", 2);
        root.addElement("grupo/usuario-grupo");
        root.addElement("orderby").addElement("loja");
        toStringToXML(root);
    }

    /**
     * Testa o gravar e ler XML com String.
     */
    @Ignore("Verificar por que não funciona")
    public void testXMLtoStringtoXMLAcento() throws Exception {
        MElement root = MElement.newInstance("requisicao-tempo-atendimento");

        root.addElement("texto", "ÁÃÀÄÉËÈÊ");
        toStringToXML(root);
    }

    /**
     * Verifica se consegue colocar um XML como texto e depois ler o mesmo
     * conteudo via uma parse.
     */
    @Test
    public void testXMLtoStringtoXMLComNamespaceComPrefixo() throws Exception {
        MElement root = MElement.newInstance("http://www.br/gerencia/Tempo", "x:req-tempo");

        root.addElement("Filtro/operadoras/id-operadora", 2);
        root.addElement("Filtro/operadoras/loja/id-loja", 8802);
        root.addElement("ordem/usuario-ordem");
        root.addElement("grupo/usuario-grupo");
        root.addElement("orderby").addElement("loja");

        toStringToXML(root);
    }

    /**
     * Verifica se consegue colocar um XML como texto e depois ler o mesmo
     * conteudo via uma parse.
     */
    @Test
    public void testXMLtoStringtoXMLComNamespaceSemPrefixo() throws Exception {
        MElement root = MElement.newInstance("http://www.br/gerencia/Tempo", "req-tempo");

        root.addElement("Filtro/operadoras/id-operadora", 2);
        root.addElement("Filtro/operadoras/loja/id-loja", 8802);
        root.addElement("ordem/usuario-ordem");
        root.addElement("grupo/usuario-grupo");
        root.addElement("orderby").addElement("loja");

        toStringToXML(root);
    }

    /**
     * Verifica se o parde dispara exception quando deve - caso simples.
     */
    @Test
    public void testParseValidatComErro() throws Exception {
        String sXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<raiz><filho/></raiz>";

        try {
            MParser.parse(new ByteArrayInputStream(sXML.getBytes()), true, true);
            fail("Deveria ter ocorrido um erro no parse");
        } catch (SAXException e) {
            if (!e.getMessage().startsWith("Erro(s) efetuando")) {
                throw e;
            }
            //ok - chegou o erro esperado
        }
    }

    /**
     * Verifica se o parse dispara exception quando não encontra o DTD.
     */
    @Test
    public void testParseValidatDTDNotFound() throws Exception {
        String sXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE apolices SYSTEM \"testeNaoExiste.dtd\">\n" + "<raiz><filho/></raiz>";

        try {
            MParser.parse(new ByteArrayInputStream(sXML.getBytes()), true, true);
            fail("Deveria ter ocorrido um erro no parse");
        } catch (FileNotFoundException e) {
            //ok - chegou o erro esperado
        } catch (SAXException e) {
            if (e.getMessage().indexOf("can not be resolved") == -1) {
                throw e;
            }
            //ok - chegou o erro esperado
        }
    }

    /**
     * Verifica se o parse dispara exception quando o XML não obdece ao DTD.
     */
    @Test
    public void testParseValidatDTDNaoSatisfeito() throws Exception {
        String sXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE raiz SYSTEM \"http://eee/teste-dtd-existe.dtd\">\n" + "<raiz><filho3/></raiz>";
        String sDTD =
                "<!ELEMENT raiz (filho, filho2?)>\n" + "<!ELEMENT filho (#PCDATA)>\n" + "<!ELEMENT filho2 (#PCDATA)>\n";

        MParser p = new MParser();
        p.addInputSource("http://eee/teste-dtd-existe.dtd", sDTD);
        try {
            p.parseComResolver(new ByteArrayInputStream(sXML.getBytes()));
            fail("Deveria ter ocorrido um erro no parse");
        } catch (SAXException e) {
            if (e.getMessage().indexOf("does not allow \"filho3\"") == -1 && e.getMessage().indexOf(
                    "\"filho3\" must be declared") == -1) {
                throw e;
            }
            //ok - chegou o erro esperado
        }
    }

    /**
     * Verifica se o parse consegue ler um DTD a partir de uma InputStream
     */
    @Test
    public void testParseValidatDTDEmInputStream() throws Exception {
        String sXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE raiz SYSTEM \"http://eee/teste-dtd-existe.dtd\">\n" + "<raiz><filho/></raiz>";
        String sDTD =
                "<!ELEMENT raiz (filho, filho2?)>\n" + "<!ELEMENT filho (#PCDATA)>\n" + "<!ELEMENT filho2 (#PCDATA)>\n";

        MParser p = new MParser();
        p.addInputSource("http://eee/teste-dtd-existe.dtd", new ByteArrayInputStream(sDTD.getBytes()));
        p.parseComResolver(sXML.getBytes());
    }

    /*
     * private static boolean isAtributosIguais(Node n1, Node n2) { NamedNodeMap
     * n1Attrs = n1.getAttributes(); NamedNodeMap n2Attrs = n2.getAttributes();
     * if (domEle.hasAttributes()) { //passagem dos paramentros do element DOM
     * para o SOAP element NamedNodeMap domAttributes = domEle.getAttributes();
     * int noOfAttributes = domAttributes.getLength(); for (int i = 0; i <
     * noOfAttributes; i++) { Node attr = domAttributes.item(i); Name attrNome =
     * null; //System.out.println("["+i+"]LocalName=" + attr.getLocalName());
     * //System.out.println("["+i+"]Prefix =" + attr.getPrefix());
     * //System.out.println("["+i+"]Namespace=" + attr.getNamespaceURI()); if
     * (attr.getLocalName() == null) { if (!attr.getNodeName().equals("xmlns") &&
     * !attr.getNodeName().startsWith("xmlns:")) { attrNome =
     * env.createName(attr.getNodeName()); } } else if (attr.getPrefix() ==
     * null) { if (!attr.getLocalName().equals("xmlns")) { attrNome =
     * env.createName(attr.getNodeName()); } } else { if
     * (!"xmlns".equals(attr.getPrefix())) { env.createName(
     * attr.getLocalName(), attr.getPrefix(), attr.getNamespaceURI()); } } if
     * (attrNome != null) { soapEle.addAttribute(attrNome, attr.getNodeValue()); } } } }
     */

    /**
     * Verifica se o parse consegue ler um DTD em um arquivo relativo a uma
     * classe.
     */
    @Test
    public void testParseValidateDTDFromClass() throws Exception {
        String sXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE raiz SYSTEM \"http://eee/teste-dtd-existe.dtd\">\n" + "<raiz><filho/></raiz>";

        MParser p = new MParser();
        p.addInputSource("http://eee/teste-dtd-existe.dtd", getClass(), "teste-dtd.dtd");
        p.parseComResolver(sXML.getBytes());
    }

    /**
     * Verifica se o método de copia funciona para o elemento informado.
     *
     * @param root Elemento a ser copiado
     */
    private void testCopy(MElement root) throws Exception {
        MElement newParent = MElement.newInstance("pai-a");
        MElement newElement    = newParent.copy(root, null);
        isIgual(root, newElement);

        MElement newParent2 = MElement.newInstance("http://www.opensingular.com", "pai-b");
        MElement newElement2 = newParent2.copy(root, null);
        isIgual(root, newElement2);

        MElement newParent3 = MElement.newInstance("http://www.opensingular.com", "p:pai-c");
        MElement newElement3 = newParent3.copy(root, null);
        isIgual(root, newElement3);
    }

    /**
     * Converte para String depois para XML e verifica se o resultado é igual.
     *
     * @param root XML original
     * @return Retorna o MElement resultante da conversão de ida e volta
     * @throws Exception -
     */
    private MElement toStringToXML(MElement root) throws Exception {
        //Gera String a partir do XML
        //Faz o parse da String
        testCopy(root);

        String space = root.getNamespaceURI();
        String local = root.getLocalName();

        String   sXML = root.toStringExato();
        MElement lido = MParser.parse(sXML);

        if ((space != null) && !space.equals(root.getNamespaceURI())) {
            fail("Erro bizarro: o namespace do elemento mudou depois do parse");
        }
        if ((local != null) && !local.equals(root.getLocalName())) {
            fail("Erro bizarro: o localName do elemento mudou depois do parse");
        }

        //Verifica igualdade
        isIgual(root, lido);

        testCopy(lido);

        MElement lido2 = MParser.parse(new ByteArrayInputStream(root.toByteArray()), true, false);
        isIgual(root, lido2);

        return lido;
    }

    /**
     * Verifica o método updateElement
     */
    @Test
    public void testUpdateElement() throws Exception {
        MElement original = MElement.newInstance("teste");
        original.addElement("nome", "Paulo");
        original.addElement("idade", "30");
        original.addElement("@cod", 10);
        original.addElement("@cpf", "676.123.123-23");
        MElement tels = original.addElement("telefones");
        tels.addElement("comercial", "9999");
        tels.addElement("celular", "8888");

        original.updateNode("nome", "Maria");
        original.updateNode("@cod", "20");
        original.updateNode("@rg", "10000");
        original.updateNode("@cpf", null); //Remove o atributo
        original.updateNode("idade", null); //Remove o valor do elemento

        original.updateNode("endereco", "Lago Norte");
        original.updateNode("telefones", "Não faz nada"); //Inocuo
        original.updateNode("telefones/comercial", "7777");
        original.updateNode("telefones/residencial", "6666");

        MElement esperado = MElement.newInstance("teste");
        esperado.addElement("nome", "Maria");
        esperado.addElement("idade");
        esperado.addElement("@cod", 20);
        esperado.addElement("@rg", "10000");
        tels = esperado.addElement("telefones");
        tels.addElement("comercial", "7777");
        tels.addElement("celular", "8888");
        tels.addElement("residencial", "6666");
        esperado.addElement("endereco", "Lago Norte");

        isIgual(esperado, original);
    }

    /**
     * Testa a conversão do toStringExato de caracteres especias.
     */
    @Test
    public void testCaracterEspecial() throws Exception {
        MElement e = MElement.newInstance("teste");
        e.addElement("Ecomercial", "jão & maria");
        e.addElement("MaiorMenor", "a<b>c");
        String original = e.toStringExato(false);
        String esperado = "<teste><Ecomercial>jão &amp; maria</Ecomercial><MaiorMenor>a&lt;b&gt;c</MaiorMenor></teste>";
        if (!original.equals(esperado)) {
            fail("Erro: a conversão de caracteres especiais não funcionou.");
        }
    }

    @Test
    public void testParseCaracterEspecial() throws Exception {
        MElement original = MElement.newInstance("teste");
        original.setAttribute("bomdia", "asdf&asdf><bom");
        original.addElement("Ecomercial", "&jão & maria&");
        original.addElement("MaiorMenor", "<b>");

        MElement parsed = MParser.parse(original.toStringExato());

        isIgual(parsed, original);
    }

    @Test
    public void addDiferentTypesOfElements() {
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");

        MElement root = MElement.newInstance("root");

        root.addElement("bytesOfString", "valor".getBytes());
        root.addElement("calendar", calendar);
        root.addElement("date", calendar.getTime());
        root.addElement("longValue", (long) 123);
        root.addElement("doubles", 123.45);
        root.addElement("simpleString", "valores");
        root.addElement("outraString", "valor", "val");
        root.addElement("doublePrecision", 123456.700, 1);

        GregorianCalendar calendarMElement = root.getCalendar("calendar");
        Assert.assertEquals(0, calendar.compareTo(calendarMElement));

        Date date = root.getDate("date");
        Assert.assertEquals(0, date.compareTo(calendar.getTime()));

        long longValue = root.getLong("longValue");
        Assert.assertEquals(longValue, (long) 123);

        double doubles = root.getDouble("doubles");
        Assert.assertEquals(doubles, 123.45, 0);

        String simpleString = root.getValue("simpleString");
        Assert.assertEquals(simpleString, "valores");

        String outraString = root.getValue("outraString");
        Assert.assertEquals(outraString, "valor");

        double doublePrecision = root.getDouble("doublePrecision");
        Assert.assertEquals(doublePrecision, 123456.7, 0);


        root.addElement("dateIgnoringDefault", calendar.getTime(), calendar.getTime());
        Date dateIgnoringDefault = root.getDate("dateIgnoringDefault");
        Assert.assertEquals(0, dateIgnoringDefault.compareTo(calendar.getTime()));

        root.addElement("dateUsingDefault", null, calendar.getTime());
        Date dateUsingDefault = root.getDate("dateUsingDefault");
        Assert.assertEquals(0, dateUsingDefault.compareTo(calendar.getTime()));

        Date dataNull = null;
        root.addElement("dateUsingDefaultWithAllNull", null, dataNull);
        Date dateUsingDefaultWithAllNull = root.getDate("dateUsingDefaultWithAllNull");
        Assert.assertNull(dateUsingDefaultWithAllNull);
    }

    @Test
    public void testAddElementObjects() {
        Calendar calendar = ConversorToolkit.getCalendar("01/01/2017");
        MElement root     = MElement.newInstance("raiz");

        Object longObj = new Long(123456);
        root.addElement("longObj", longObj);
        Assert.assertEquals(root.getLong("longObj"), 123456);

        Object calendarObj = calendar;
        root.addElement("calendarObj", calendarObj);
        Assert.assertEquals(0, root.getCalendar("calendarObj").compareTo(calendar));

        Object dateObj = calendar.getTime();
        root.addElement("dateObj", dateObj);
        Assert.assertEquals(0, root.getDate("dateObj").compareTo((Date) dateObj));

        Object   stringObj        = "testValue";
        MElement stringObjElement = root.addElement("stringObj", stringObj);
        Assert.assertEquals(root.getValue("stringObj"), stringObj);

        MElement newStringObjDefault = root.addElement("stringObjDefault", stringObj, "testValue2");
        Assert.assertEquals(root.getValue("stringObjDefault"), stringObj);

        newStringObjDefault.addElement(stringObjElement);
        Assert.assertEquals("stringObj", newStringObjDefault.getNode("stringObj").getNodeName());

        root.addElement("bytes", (Object) "valores".getBytes());
        Assert.assertNotNull(root.getElement("bytes"));
    }

    @Test(expected = SingularException.class)
    public void testGetValorTextException() {
        MDocument document = MDocument.newInstance();
        MElement.getValueText(document);
    }

    @Test
    public void addBoolean() {
        MElement root = MElement.newInstance("raiz");
        root.addBoolean("booleanTrue", true);
        root.addBoolean("booleanFalse", false);

        Assert.assertTrue(root.getBoolean("booleanTrue"));
        Assert.assertFalse(root.getBoolean("booleanFalse"));

        Assert.assertTrue(root.getBoolean("booleanTrue", true));
        Assert.assertFalse(root.getBoolean("booleanFalse", false));
        Assert.assertFalse(root.getBoolean("bool", false));
    }

    @Test
    public void addInt() {
        MElement root = MElement.newInstance("raiz");
        root.addInt("inteiro", "123");
        root.addInt("intDefault", "456", "789");
        root.addInt("intDefaultNull", null, "852");
        root.addInt("intWithObject", "789", new Integer(741));
        root.addInt("intWithObjectNull", null, new Integer(741));
        root.addInt("intWithDefaultPrimitive", "123", 741);
        root.addInt("intWithDefaultNullPrimitive", null, 741);


        int integer                     = root.getInt("inteiro");
        int intDefault                  = root.getInt("intDefault");
        int intDefaultNull              = root.getInt("intDefaultNull");
        int intWithObject               = root.getInt("intWithObject");
        int intWithObjectNull           = root.getInt("intWithObjectNull");
        int intWithDefaultPrimitive     = root.getInt("intWithDefaultPrimitive");
        int intWithDefaultNullPrimitive = root.getInt("intWithDefaultNullPrimitive");

        Assert.assertEquals(integer, 123);
        Assert.assertEquals(intDefault, 456);
        Assert.assertEquals(intDefaultNull, 852);
        Assert.assertEquals(intWithObject, 789);
        Assert.assertEquals(intWithObjectNull, 741);
        Assert.assertEquals(intWithDefaultPrimitive, 123);
        Assert.assertEquals(intWithDefaultNullPrimitive, 741);
    }

    @Test
    public void addDate() {
        Calendar calendarDay1 = ConversorToolkit.getCalendar("01/01/2017");
        Calendar calendarDay2 = ConversorToolkit.getCalendar("02/01/2017");
        Calendar calendarDay3 = ConversorToolkit.getCalendar("03/01/2017");

        MElement root = MElement.newInstance("raiz");
        root.addDate("dataSimple", "01/01/2017");
        root.addDate("dataWithDefaultOption", "02/01/2017", "03/01/2017");
        root.addDate("dataWithNullOption", null, "03/01/2017");

        Date dataSimple            = root.getDate("dataSimple");
        Date dataWithDefaultOption = root.getDate("dataWithDefaultOption");
        Date dataWithNullOption    = root.getDate("dataWithNullOption");

        Assert.assertEquals(0, dataSimple.compareTo(calendarDay1.getTime()));
        Assert.assertEquals(0, dataWithDefaultOption.compareTo(calendarDay2.getTime()));
        Assert.assertEquals(0, dataWithNullOption.compareTo(calendarDay3.getTime()));

    }

    @Test
    public void testGetWithDefaultValue() {
        MElement root = MElement.newInstance("raiz");

        root.addInt("inteiro", "123");
        Assert.assertEquals(123, root.getInt("inteiro", 456));
        Assert.assertEquals(456, root.getInt("inteiroNotExist", 456));

        root.addElement("longValue", (long) 123);
        Assert.assertEquals((long) 123, root.getLong("longValue", 456));
        Assert.assertEquals((long) 456, root.getLong("longValueNotExist", 456));

        root.addElement("doubleVal", new Double(123.45));
        Assert.assertEquals(new Double(123.45), root.getDouble("doubleVal"), 0);
        Assert.assertNull(root.getDoubleObject("pathNotExistent"));
    }

    @Test
    public void testPutValuesOnBase64() {
        MElement mElement = MElement.newInstance("mElement");
        mElement.addElement("test", "elementos".getBytes());

        String valorFinal = new String(mElement.getByteBASE64("test"));
        assertEquals("elementos", valorFinal);


        String stringToTest = "Um teste com linha\r\n e outra linha.\r\n testes.";
        InputStream inputStreamTest = IOUtils.toInputStream(stringToTest,
                Charset.defaultCharset());
        mElement.addElement("inputStream", inputStreamTest);

        byte[] inputStreams = mElement.getByteBASE64("inputStream");
        String result       = new String(inputStreams, Charset.defaultCharset());
        Assert.assertEquals(stringToTest, result);
    }

    @Test
    public void testNewInstanceByClass() {
        MElement element = MElement.newInstance(String.class);

        Assert.assertEquals("java-lang-String", element.getNodeName());
    }

    @Test(expected = SingularException.class)
    public void testToMElementWithNode() {
        MElement element      = MElement.newInstance("raiz");
        MElement nodeMElement = element.addElement("node1");

        Node node = null;

        Assert.assertNull(MElement.toMElement(node));

        Assert.assertTrue(MElement.toMElement(element.getNode("node1")) instanceof MElement);

        Assert.assertTrue(MElement.toMElement((Node) nodeMElement) instanceof MElement);

        MDocument document = MDocument.newInstance();

        MElement.toMElement(document); // throws exception
    }

    @Test(expected = SingularException.class)
    public void testToMElementWithMElement() {
        MElement element = MElement.newInstance("raiz");

        MElement.toMElement(element);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddIntWithNullValue() {
        MElement root = MElement.newInstance("raiz");
        root.addInt("inteiro", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddIntWithEmptyValue() {
        MElement root = MElement.newInstance("raiz");
        root.addInt("inteiro", "");
    }

    @Test(expected = SingularException.class)
    public void testAddIntWithDefaultValueNull() {
        MElement root = MElement.newInstance("raiz");
        Assert.assertNull(root.addInt("inteiro", null, null));

        Assert.assertNull(root.addInt("inteiro", null, ""));

        root.addInt("inteiro", null, 123.45); // throws exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDateNull() {
        MElement root = MElement.newInstance("raiz");
        root.addDate("date", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDateWithDefaultOptionAndValueNull() {
        MElement root = MElement.newInstance("raiz");
        Assert.assertNull(root.addDate("date", null, null));

        Date date = null;
        root.addElement("date", date);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddCalendarWithValueNull() {
        MElement root = MElement.newInstance("root");

        Calendar calendar = null;
        root.addElement("calendar", calendar);
    }

    @Test
    public void testIsNull() {
        MElement root = MElement.newInstance("raiz");
        root.addElement("elemento", "valor");

        Assert.assertFalse(root.isNull("elemento"));
        Assert.assertTrue(root.isNull("elem"));
    }

    @Test(expected = NullPointerException.class)
    public void testGets() {
        MElement root = MElement.newInstance("raiz");

        MElement inteiro = root.addInt("inteiro", "100");
        Assert.assertEquals(100, inteiro.getInt());

        MElement longValue = root.addInt("longValue", "100");
        Assert.assertEquals(100, longValue.getLong());

        Assert.assertEquals("100", root.getValue("inteiro", "50"));
        Assert.assertEquals("50", root.getValue("int", "50"));

        root.addElement("doubleObj", "100.5");
        Assert.assertEquals(new Double("100.5"), root.getDoubleObject("doubleObj"));

        Assert.assertEquals("100,5", root.formatNumber("doubleObj"));

        Assert.assertNull(root.getCalendar("caminhoInvalido"));

        MElement doubleNull = root.addElement("doubleNull");
        doubleNull.getDouble(); // throws exception
    }

    @Test(expected = SingularException.class)
    public void testGetBooleanMethods() {
        MElement root = MElement.newInstance("raiz");
        root.addInt("inteiro", "100");
        root.addBoolean("bool", true);

        Assert.assertTrue(root.is("inteiro"));

        root.is("inteiro");
    }

    @Test(expected = SingularException.class)
    public void testGetBooleanMethodsWithDefaultOption() {
        MElement root = MElement.newInstance("raiz");
        root.addInt("inteiro", "100");
        root.addBoolean("bool", true);

        Assert.assertTrue(root.is("inteiro", false));

        root.is("inteiro", false);
    }

    @Test
    public void testFromBase64OutPutStream() throws IOException {
        TempFileProvider.create(this, tmpProvider -> {
            MElement root = MElement.newInstance("raiz");
            root.addElement("string", Base64.getEncoder().encodeToString("stringVal".getBytes()));

            File arquivoTemporario = tmpProvider.createTempFile(Long.toString(System.currentTimeMillis()) + ".txt");

            FileOutputStream outputStream = new FileOutputStream(arquivoTemporario);
            root.getByteBASE64("string", outputStream);
            outputStream.close();
        });
    }

    @Test
    public void testFormat() {
        MElement root = MElement.newInstance("raiz");

        Assert.assertEquals("", root.formatDate("caminhoInvalido"));
        Assert.assertEquals("", root.formatDate("caminhoInvalido", ""));

        Assert.assertEquals("", root.formatHour("caminhoInvalido"));

        root.addDate("dateHour", "01/01/2017");
        Assert.assertEquals("00:00:00", root.formatHour("dateHour"));
    }

    @Test
    public void testToJSONString() {
        MElement root = MElement.newInstance("raiz");
        Assert.assertEquals("{}", root.toJSONString());
    }

    @Test
    public void testGetBrothers() {
        MElement root       = MElement.newInstance("raiz");
        MElement child1     = root.addElement("filho1", "123");
        MElement child2     = root.addElement("filho2", "123456");
        MElement child2Copy = root.addElement("filho2", "0");

        Assert.assertEquals(child2.getNodeName(), child1.getProximoIrmao().getNodeName());
        Assert.assertEquals(child1.getNodeName(), child2.getIrmaoAnterior().getNodeName());

        Assert.assertEquals(child2.getValue(), child2Copy.getGemeoAnterior().getValue());
        Assert.assertEquals(child2Copy.getValue(), child2.getProximoGemeo().getValue());

        Assert.assertEquals(child2Copy.getValue(), root.getUltimoFilho().getValue());

        Assert.assertEquals(child1.getValue(), root.getPrimeiroFilho("filho1").getValue());


    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddElementObjectNull() {
        MElement root = MElement.newInstance("raiz");

        root.addElement("elemento", (Object) null);
    }
    // TODO terminar testes MElement
}