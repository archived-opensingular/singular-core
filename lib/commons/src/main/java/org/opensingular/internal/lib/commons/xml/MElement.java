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

package org.opensingular.internal.lib.commons.xml;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.io.StringPrintWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * Representa um Element com diversos métodos utilitários para
 * leitura e montagem do XML. Essa classe substitui a classe XMLToolkit.
 * O MElement é um Element (implementa essa interface) adicionado dos métodos
 * do XMLToolkit.
 * <p/>
 * É possível montar uma árvore XML com objeto org.w3c.dom.Element
 * usando os métodos desta classe, no entanto, este procedimento não
 * é prático, pois exige mais de um passo para adicionar uma
 * única informação.
 * <p/>
 * A montagem de um estrutura de objetos Element em vez do arquivo
 * XML também é bem mais simples e de melhor performance Evita-se fazer um
 * parse do arquivo.
 * <p/>
 * <p/>
 * <b>Exemplo de uso</b>:<br>
 * <p/>
 * Passo 1: <i>Cria o elemento raiz:</i>
 * <xmp>
 * MElement raiz = MElement.newInstance("pedido");
 * raiz.printTabulado(System.out);
 * // XML resultado:
 * // <pedido/>
 * </xmp>
 * <p/>
 * Passo 2: <i>Adicionar sub-elementos:</i>
 * <xmp>
 * MElement item1 = raiz.addElement("item");
 * MElement item2 = raiz.addElement("item");
 * raiz.printTabulado(System.out);
 * // XML resultado em raiz:
 * // <pedido>
 * //   <item/>
 * //   <item/>
 * // </pedido>
 * </xmp>
 * <p/>
 * Passo 3: <i>Adicionar elementos com valores:</i>
 * <xmp>
 * item1.addElement("@cod",310);
 * item1.addElement("nome","arroz");
 * item1.addElement("qtd",10);
 * item2.addElement("@cod",410);
 * item2.addElement("nome","milho");
 * item2.addElement("qtd",21);
 * item2.addElement("unidade","kg");
 * raiz.addElement("responsavel","Paulo Santos");
 * raiz.printTabulado(System.out);
 * // XML resultado em raiz:
 * // <pedido>
 * //   <item cod="310">
 * //      <nome>arroz</nome>
 * //      <qtd>10</qtd>
 * //   </item>
 * //   <item cod="410">
 * //      <nome>milho</nome>
 * //      <qtd>21</qtd>
 * //      <unidade>kg</unidade>
 * //   </item>
 * //   <responsavel>Paulo Santos</responsavel>
 * // </pedido>
 * </xmp>
 * <p/>
 * Passo 4: <i>Percorrendo todos os elementos filhos:</i>
 * <xmp>
 * //getPrimeiroFilho() e getProximoIrmao já retornam MElement
 * MElement filho = raiz.getPrimeiroFilho();
 * while (filhos != null) {
 * System.out.println(filho.getNodeName());
 * filhos = filhos.getProximoIrmao()
 * }
 * // XML resultado:
 * //   item
 * //   item
 * //   responsavel
 * </xmp>
 * <p/>
 * Passo 5: <i>Percorrendo todos os elementos "item":</i>
 * <xmp>
 * //getPrimeiroFilho(String) e getProximoGemeo já retornam MElement
 * MElement item = raiz.getPrimeiroFilho("item");
 * while (item != null) {
 * System.out.println(
 * item.getValue("@cod") + " - " +   //Le um atributo
 * item.getValue("nome") + " - " +   //Le o valor de um Element
 * item.formatNumber("qtd",1) + " " +//Le formatando a saida
 * item.getValue("unidade","n/d"));  //Le valor (usando default)
 * <p/>
 * item = item.getProximoGemeo()
 * }
 * // XML resultado:
 * //   310 - arroz - 10.0 - kg
 * //   410 - milho - 21.0 - n/d
 * </xmp>
 * <p/>
 * Passo 6: <i>Percorrendo todos os elementos "item" com qtd=21:</i>
 * <xmp>
 * // selectElements aceita consultas xPath
 * MElementResult item = raiz.selectElements("item[qtd=21]");
 * while (item.next()) {
 * System.out.println(
 * item.getValue("@cod") + " - " +   //Le um atributo
 * item.getValue("nome") + " - ");   //Le o valor de um Element
 * }
 * // XML resultado:
 * //   410 - milho
 * </xmp>
 * <p/>
 * <b>Criando um MElement.</b> Existe 3 formas de se obter um MElement:
 * <xmp>
 * //Novo element
 * MElement raiz1 = MElement.newIntance("pedido");
 * <p/>
 * //Novo element com namespace
 * MElement raiz2 = MElement.newIntance("http://www.com/ordem", "pedido");
 * <p/>
 * //Convertendo um Element
 * //Toda alteração em wrapper reflete-se no Element original.
 * Element original = ....
 * MElement wraper = MElement.toElement(original);
 * </xmp>
 * <p/>
 * <b>MElement e SQL.</b> Um problema muito comum é o tratamento de null
 * tanto ao ler quanto ao gravar no banco de dados. O MElement possui alguns
 * facilitadores nesse sentido.<p>
 * <p/>
 * <i>Montando XML a partir do Banco</i>
 * <xmp>
 * <pre>
 *    MElement raiz = MElement.newInstance("pedido");
 *    java.sql.Date agora = new java.sql.Date(System.currentTimeMillis());
 *
 *    Resultset rs = ....
 *
 *    while(rs.next()) {
 *        MElement item = raiz.addElement("item");
 *
 *        // Se o nome for null, o addElement(String,String) dispara erro
 *        // Nesse caso não é problema, pois no banco o campo é not null
 *        item.addElement("nome", rs.getString("nome"));
 *        item.addElement("qtd" , rs.getInt("qtd");
 *
 *        // Por ser possível que o campo und seja null, é utilizado
 *        // o método addElement(String, String, String) onde o último valor
 *        // é o default (se o segundo=null). Por ser o terceiro null, caso o
 *        // rs.getString("und") seja null, simplesmente não adicionada a tag
 *        item.addElement("und" , rs.getString("und"), null);
 *
 *        // Nesse caso adiciona o terceiro valor se dt for null
 *        item.addElement("dt"  , rs.getDate("dt"), agora);
 *    }
 * </pre>
 * </xmp>
 * <p/>
 * <i>Preenchendo um PreparedStatement a partir do MElement</i>
 * <pre>
 * <xmp>
 *    MElement raiz =....
 *    PreparedStatement ps = ....
 *
 *    //Caso qualquer um dos 4 campos sejam null, já chama ps.setNull(int)
 *
 *    //Métodos específicos para os tipos principais
 *    raiz.setSQLInt(ps, 1, "id");
 *    raiz.setSQLString(ps, 2, "nome");
 *    raiz.setSQLDouble(ps, 3, "salario");
 *
 *    //Recebe como parâmetro o tipo SQL a ser utilizado no parâmetro
 *    raiz.setSQL(ps, 4, TYPES.VARCHAR, "descr");
 * </xmp>
 * </pre>
 *
 * @author Daniel C. Bordin
 */
public abstract class MElement implements Element, Serializable {

    private final PrintWriterFactory writerFactory;
    private final MElementWriter xmlWriter;
    private final MElementWriter jsonWriter;


    protected MElement() {
        this(new UTF8PrintWriterFactory());
    }

    protected MElement(PrintWriterFactory writerFactory) {
        this.writerFactory = writerFactory;
        this.xmlWriter = new XMLMElementWriter(StandardCharsets.UTF_8);
        this.jsonWriter = new JSONToolkit();
    }

    /**
     * Pega em tempo de compilação situações onde tenta-se converte MElement
     * para MElement. Como tal passo é totalmente desnecessário, retorna void.
     * Na prática gera um erro em tempo de compilação em tal caso.
     *
     * @param no elemento não precisa ser convertido
     */
    public static void toMElement(MElement no) {
        throw SingularException.rethrow("Não deveria ser chamadado esse metodo com um parâmetro MElement");
    }

    public static MElement toMElement(Element no) {
        if (no == null) {
            return null;
        } else if (no instanceof MElement) {
            return (MElement) no;
        }
        return new MElementWrapper(no);
    }

    /**
     * Gerar um wrapper MElement baseado no Node informado.
     *
     * @param no Element original.
     * @return Null se no for null. O próprio se esse já for MElement.
     */
    public static MElement toMElement(Node no) {
        if (no == null) {
            return null;
        } else if (no instanceof MElement) {
            return (MElement) no;
        } else if (!XmlUtil.isNodeTypeElement(no)) {
            throw new SingularException("no " + XPathToolkit.getFullPath(no) + " não é Element");
        }
        return new MElementWrapper((Element) no);
    }

    /**
     * Cria um novo MElement com tag raiz com o nome da classe informada. O
     * MElement contém internamente um Element embutido.
     *
     * @param toCall Classe cujo nome sera o nome da tag
     * @return MElement wrapper.
     */
    public static MElement newInstance(Class<?> toCall) {
        return newInstance(toCall.getName().replace('.', '-'));
    }

    /**
     * Cria um novo MElement com tag raiz no nome informado. O MElement contém
     * internamente um Element embutido.
     *
     * @param rootName nome da tag raiz
     * @return MElement wrapper.
     */
    public static MElement newInstance(String rootName) {
        return new MElementWrapper(rootName);
    }

    /**
     * Cria um novo MElement com tag raiz no nome e namespace especificados. O
     * MElement contém internamente um Element embutido. MElement retornado.
     *
     * @param nameSpaceURI Nome do namespace. Tipicamente o name space possui o
     *                     formato de uma URL (não é obrigatório) no formato, por exemplo,
     * @param rootName     o nome do elemento que será criado. Pode conter prefixo
     *                     (ex.: "fi:ContaPagamento").
     * @return -
     */
    public static MElement newInstance(String nameSpaceURI, String rootName) {
        return new MElementWrapper(nameSpaceURI, rootName);
    }

    /**
     * Retorna o valor do no passado como parâmetro. Se for um Element retorna o
     * texto imediatamente abaixo.
     *
     * @param node do qual será extraido o texto
     * @return pdoe ser null
     */
    static String getValueText(Node node) {
        //Não é private, pois a classe XMLToolkit também utiliza
        if (node == null) {
            return null;
        }
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                Node n = node.getFirstChild();
                if (XmlUtil.isNodeTypeText(n)) {
                    return n.getNodeValue();
                }
                break;
            case Node.ATTRIBUTE_NODE:
            case Node.TEXT_NODE:
                String value = node.getNodeValue();
                if (!StringUtils.isEmpty(value)) {
                    return value;
                }
                break;
            default:
                throw new SingularException("getValueText(Node) não trata nó "
                        + XPathToolkit.getNodeTypeName(node));
        }
        return null;
    }

    private PrintWriterFactory getWriterFactory() {
        return writerFactory;
    }

    public final MDocument getMDocument() {
        return MDocument.toMDocument(getOwnerDocument());
    }

    public final void addElement(MElement e) {
        appendChild(e.getOriginal());
    }

    abstract Element getOriginal();

    /**
     * Adiciona um element com o nome informado e devolve a referência.
     *
     * @param name do novo element filho
     * @return Elemento criado
     */
    public final MElement addElement(String name) {
        return addElementNS(null, name);
    }

    /**
     * Adiciona um element com o nome informado no namespace especificado.
     *
     * @param namespaceURI  -
     * @param qualifiedName Nome do novo element filho
     * @return Elemento criado
     */
    public final MElement addElementNS(String namespaceURI, String qualifiedName) {
        return toMElement(MElementWrapper.addElementNS(this, namespaceURI, qualifiedName));
    }

    /**
     * Adiciona um element como o nome informado como filho do atual.
     *
     * @param name  do MElement a ser criado
     * @param value Se for null, um exception é disparada.
     * @return O MElement resultado.
     */
    public final MElement addElement(String name, String value) {
        return toMElement(MElementWrapper.addElement(this, name, value));
    }

    /**
     * Adiciona um no como o nome informado e com o valor informado ou com o
     * default na ausencia do primeiro. Se o default também for null, então o no
     * não é adicionado
     *
     * @param name     do MElement a ser criado
     * @param value    -
     * @param defaultV a ser utilizado se valor==null
     * @return O MElement resultado.
     */
    public final MElement addElement(String name, String value, String defaultV) {
        if (value != null) {
            return addElement(name, value);
        } else if (defaultV != null) {
            return addElement(name, defaultV);
        }
        return null;
    }

    /**
     * Adiciona o elemento como o valor informado como objeto fazendo as devidas
     * converções se necessário. Trata os seguintes objetos de forma especial:
     * Integer, Long, Double, java.util.Date.
     *
     * @param name do elemento a ser criado
     * @param o    Objeto a ser convertido para texto
     * @return MElement criado
     */
    public final MElement addElement(String name, Object o) {
        if (o == null) {
            return addElement(name, (String) null);
        } else if (o instanceof String) {
            //Apenas para não ter que sempre passar por todos os if
            //em geral será String
            return addElement(name, (String) o);
        } else if (o instanceof Integer) {
            return addElement(name, ((Integer) o).intValue());
        } else if (o instanceof Long) {
            return addElement(name, ((Long) o).longValue());
        } else if (o instanceof Double) {
            return addElement(name, ((Double) o).doubleValue());
        } else if (o instanceof java.util.Date) {
            return addElement(name, (java.util.Date) o);
        } else if (o instanceof Calendar) {
            return addElement(name, (Calendar) o);
        } else if (o instanceof InputStream) {
            return addElement(name, (InputStream) o);
        } else if (o instanceof byte[]) {
            return addElement(name, (byte[]) o);
        } else {
            return addElement(name, o.toString());
        }
    }

    /**
     * Adiciona o elemento como o valor informado como objeto fazendo as devidas
     * converções se necessário. Se o valor null, utiliza o valor default. Se o
     * default também for null, então o no não é adicionado.
     *
     * @param name     do elemento a ser criado
     * @param value    Objeto a ser convertido para texto
     * @param defaultV a ser utilizado se valor==null
     * @return MElement criado
     */
    public final MElement addElement(String name, Object value, Object defaultV) {
        if (value != null) {
            return addElement(name, value);
        } else if (defaultV != null) {
            return addElement(name, defaultV);
        }
        return null;
    }

    /**
     * Cria um no indicado pelo nome com o texto resultado da converção do
     * double.
     *
     * @param name  do element ou atributo a ser criado
     * @param value a ser atribuito
     * @return MElement criado ou dono do atributo criado
     */
    public final MElement addElement(String name, double value) {
        return addElement(name, Double.toString(value));
    }

    /**
     * Cria um no indicado pelo nome com o texto resultado da converção do
     * double segundo a precisão desejada.
     *
     * @param name     do element ou atributo a ser criado
     * @param value    a ser atribuito
     * @param precision Informa quantas casas depois d virgula deseja-se manter.
     *                 Se for negativo arredonta os digitos antes da virgula.
     * @return MElement criado ou dono do atributo criado
     */
    public final MElement addElement(String name, double value, int precision) {
        double m      = Math.pow(10, precision);
        String sValue = Double.toString(Math.rint(Math.round(value * m)) / m);
        return addElement(name, sValue);
    }

    /**
     * Cria um no indicado pelo nome com o texto resultado da converção do int.
     *
     * @param name  do element ou atributo a ser criado
     * @param value a ser atribuito
     * @return MElement criado ou dono do atributo criado
     */
    public final MElement addElement(String name, int value) {
        return addElement(name, Integer.toString(value));
    }

    /**
     * Cria um no indicado pelo nome com o texto resultado da converção do long.
     *
     * @param name  do element ou atributo a ser criado
     * @param value a ser atribuito
     * @return MElement criado ou dono do atributo criado
     */
    public final MElement addElement(String name, long value) {
        return addElement(name, Long.toString(value));
    }

    /**
     * Adiciona um elemento binario no formato BASE64 dentro do elemento pai. O
     * formato BASE64 é definido pelo RFC1521 do RFC1521. Ele transforma um
     * binário em uma string, um codificação de 6 bits. Deste modo, um array
     * binário ocupa 33% mais espaço no formato BASE, contudo passa a ser uma
     * string simples. É necessário levar em consideração questões de gasto de
     * memória e de custo de conversão de binário para string e string para
     * binário ao se decidir pelo uso deste formato.
     *
     * @param name  o nome do elemento que será inserido
     * @param value o array binário do elemento adicionado (a ser convertido p/
     *              BASE64)
     * @return o elemento que foi adicionado
     */
    public final MElement addElement(String name, byte[] value) {
        return addElement(name, MElementWrapper.toBASE64(value));
    }

    /**
     * Adiciona um elemento binario no formato BASE64 dentro do elemento pai até
     * esgotar a InputStream. O formato BASE64 é definido pelo RFC1521 do
     * RFC1521. Ele transforma um binário em uma string, um codificação de 6
     * bits. Deste modo, um array binário ocupa 33% mais espaço no formato BASE,
     * contudo passa a ser uma string simples. É necessário levar em
     * consideração questões de gasto de memória e de custo de conversão de
     * binário para string e string para binário ao se decidir pelo uso deste
     * formato.
     *
     * @param name o nome do elemento que será inserido
     * @param in   Stream com os dados a serem convertidos p/ BASE64.
     * @return o elemento que foi adicionado
     */
    public final MElement addElement(String name, InputStream in) {
        return addElement(name, MElementWrapper.toBASE64(in, getWriterFactory().getCharset()));
    }

    /**
     * Adiciona o no com o nome indicado com o valor boolean informado.
     *
     * @param name  do MElement a ser criado
     * @param value -
     * @return O MElement criado (a menos que nome aponte para um atributo).
     */
    public final MElement addBoolean(String name, boolean value) {
        if (value) {
            return addElement(name, Boolean.TRUE.toString());
        } else {
            return addElement(name, Boolean.FALSE.toString());
        }
    }

    /**
     * Adiciona o no com o nome indicado considerando-o como um inteiro.
     *
     * @param name  do MELement a ser criado
     * @param value Se for null, uma exception é disparada.
     * @return O MElement criado (a menos que nome aponte para um atributo).
     */
    public final MElement addInt(String name, String value) {
        if (value == null) {
            return addElement(name, (String) null);
        } else {
            String s = value.trim();
            if (s.length() == 0) {
                return addElement(name, (String) null);
            }
            Integer.parseInt(s); //NOSONAR Testa se é um inteiro
            return addElement(name, s);
        }
    }

    /**
     * Adiciona o no com o nome indicado considerando o valor como sendo uma
     * inteiro a qual será convertida para o formato ISO8601. Caso o valor seja
     * null ou string em branco, então considera o segundo valor para a
     * conversão. Caso o defaultValue também seja null ou em braco, então não é
     * adiciona o nó.
     *
     * @param name         do MELement a ser criado
     * @param value        -
     * @param defaultValue a ser utilizado se valor for null ou vazio
     * @return O MElement criado (a menos que nome aponte para um atributo).
     */
    public final MElement addInt(String name, String value, Object defaultValue) {
        if (value != null) {
            String v = value.trim();
            if (v.length() > 0) {
                Integer.parseInt(v); //NOSONAR Testa se é um inteiro
                return addElement(name, v);
            }
        }
        if (defaultValue != null) {
            if (defaultValue instanceof String) {
                String v = ((String) defaultValue).trim();
                if (v.length() > 0) {
                    Integer.parseInt(v); //NOSONAR Testa se é um inteiro
                    return addElement(name, v);
                }
            } else if (defaultValue instanceof Integer) {
                return addElement(name, defaultValue);
            } else {
                throw new SingularException("Tipo default inválido ("
                        + defaultValue.getClass().getName()
                        + ") para um inteiro");
            }
        }
        return null;
    }

    /**
     * Adiciona o no com o nome indicado considerando o valor como sendo uma
     * inteiro a qual será convertida para o formato ISO8601. Caso o valor seja
     * null ou string em branco, então considera o segundo valor para a
     * conversão. Caso o defaultValue também seja null ou em braco, então não é
     * adiciona o nó.
     *
     * @param name         do MELement a ser criado
     * @param value        -
     * @param defaultValue a ser utilizado se valor for null ou vazio
     * @return O MElement criado (a menos que nome aponte para um atributo).
     */
    public final MElement addInt(String name, String value, int defaultValue) {
        if (value != null) {
            String v = value.trim();
            if (v.length() > 0) {
                Integer.parseInt(v); //NOSONAR Testa se é um inteiro
                return addElement(name, v);
            }
        }
        return addElement(name, defaultValue);
    }

    /**
     * Adiciona o no com o nome indicado considerando o valor como sendo uma
     * data a qual será convertida para o formato ISO8601.
     *
     * @param name  do MELement a ser criado
     * @param value Se for null, uma exception é disparada.
     * @return O MElement criado (a menos que nome aponte para um atributo).
     */
    public final MElement addDate(String name, String value) {
        if (value == null) {
            return addElement(name, (String) null);
        } else {
            return addElement(name, ConversorToolkit.getDateFromData(value));
        }
    }

    /**
     * Adiciona o no com o nome indicado considerando o valor como sendo uma
     * data a qual será convertida para o formato ISO8601. Caso o valor seja
     * null ou string em branco, então considera o segundo valor para a
     * conversão. Caso o defaultValue também seja null ou em braco, então não é
     * adiciona o nó.
     *
     * @param name         do MELement a ser criado
     * @param value        -
     * @param defaultValue a ser utilizado se valor for null ou vazio
     * @return O MElement criado (a menos que nome aponte para um atributo).
     */
    public final MElement addDate(String name, String value, String defaultValue) {
        String trim = StringUtils.trimToNull(value);
        if (trim == null) {
            trim = StringUtils.trimToNull(defaultValue);
        }
        if (trim != null) {
            return addElement(name, ConversorToolkit.getDateFromData(trim));
        }
        return null;
    }

    /**
     * Adiciona um element como o nome e a data informada no formato ISO 8601.
     *
     * @param name  do MElement a ser criado
     * @param value Se for null, um exception é disparada.
     * @return O MElement resultado.
     */
    public final MElement addElement(String name, java.util.Date value) {
        if (value == null) {
            return addElement(name, (String) null);
        } else {
            return addElement(name, ConversorDataISO8601.format(value));
        }
    }

    /**
     * Adiciona um no como o name informado e com o valor informado ou com o
     * default na ausencia do primeiro. Se o default também for null, então o no
     * não é adicionado.
     *
     * @param name         do MElement a ser criado
     * @param value        -
     * @param defaultValue a ser utilizado se valor==null
     * @return O MElement resultado.
     */
    public final MElement addElement(String name, java.util.Date value, java.util.Date defaultValue) {
        if (value != null) {
            return addElement(name, ConversorDataISO8601.format(value));
        } else if (defaultValue != null) {
            return addElement(name, ConversorDataISO8601.format(defaultValue));
        }
        return null;
    }

    /**
     * Adiciona um element como o nome e o Calendar informada no formato ISO
     * 8601.
     *
     * @param name  do MElement a ser criado
     * @param value Se for null, um exception é disparada.
     * @return O MElement resultado.
     */
    public final MElement addElement(String name, Calendar value) {
        if (value == null) {
            return addElement(name, (String) null);
        } else {
            return addElement(name, ConversorDataISO8601.format(value));
        }
    }

    /**
     * Atualiza o Node (Element ou atributo) já exisitente. Se o Node não for
     * localizado, então adiciona se o valor for diferente de null.
     *
     * @param xPath Caminho do Node a ser atualizado ou criado
     * @param value Novo valor do Node. Se for null, então o valor é limpo. Se o
     *              element já existir e valor for null, então transforma a tag em
     *              empty, mas a mantém no XML.
     * @return O Node alterado ou criado, ou null se não for possível atualizar
     * o valor do mesmo.
     */
    public final Node updateNode(String xPath, String value) {
        Node n = getNode(xPath);
        if ((n == null) && !StringUtils.isEmpty(value)) {
            return addElement(xPath, value);
        } else if (n instanceof Element) {
            if (updateElement(value, n)) return null;
        } else if (n instanceof Attr) {
            return updateAttr(xPath, value);

        } else {
            return null;
        }
        return n;
    }

    protected Node updateAttr(String xPath, String value) {
        //Não há como saber quem é o pai (n.getParentNode() retorna null)
        if (value == null) {
            return addElement(xPath, ""); //Força a remoção do atributo
        }
        return addElement(xPath, value);
    }

    protected boolean updateElement(String value, Node n) {
        Node filho = n.getFirstChild();
        if (filho == null) {
            if (!StringUtils.isEmpty(value)) {
                Document d = n.getOwnerDocument();
                Text txt = d.createTextNode(value);
                n.appendChild(txt);
            }
        } else if (XmlUtil.isNodeTypeText(filho)) {
            if (!StringUtils.isEmpty(value)) {
                filho.setNodeValue(value);
            } else {
                n.removeChild(filho);
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * Verifica se existe pelo menos um Element apontado pelo xPath. <br>
     * Dispara erro se existir um Node no endereço, mas esse não Element.
     *
     * @param xPath Endereço do Element ou consulta xpath
     * @return se getElement(xPath) != null
     * @see #possuiNode
     */
    public final boolean possuiElement(String xPath) {
        return getElement(xPath) != null;
    }

    /**
     * Verifica se existe pelo menos um nó apontado pelo xPath.
     *
     * @param xPath Endereço do nó ou consulta xpath
     * @return se getNode(xPath) != null
     */
    public final boolean possuiNode(String xPath) {
        return getNode(xPath) != null;
    }

    /**
     * Verifica se existe o nó apontado pelo xPAth e se possui um texto.
     *
     * @param xPath Endereço do nó ou consulta xpath
     * @return se getValue(xPath) != null
     */
    public final boolean isNull(String xPath) {
        return getValue(xPath) == null;
    }

    /**
     * Conta o número de ocorrências de Elements filhos do no.
     *
     * @return o número de ocorrências
     */
    public final int countFilhos() {
        return count(null);
    }

    /**
     * Conta o número de ocorrências de Elements filhos do no com o nome
     * especificado.
     *
     * @param name do elemento a ser procurado. Se for null conta todos.
     * @return o número de ocorrências
     */
    public final int count(String name) {
        int  qtd  = 0;
        Node node = getFirstChild();
        while (node != null) {
            if (XmlUtil.isNodeTypeElement(node, name)) {
                qtd++;
            }
            node = node.getNextSibling();
        }
        return qtd;
    }

    /**
     * Retorna o texto referente ao elemento atual (sub-texto).
     *
     * @return null se for um tag vazia (ex: <nome/>).
     */
    public final String getValue() {
        return getValueText(this);
    }

    /**
     * Retorna o texto do elemento atual (sub-texto) com inteiro. Dispara
     * NullPointException se não houver um valor disponível.
     */
    public final int getInt() {
        String s = getValue();
        if (s == null) {
            throw new NullPointerException("Tag '" + getFullPath() + "' vazia");
        }
        return Integer.parseInt(s);
    }

    /**
     * Retorna o texto do elemento atual (sub-texto) como long. Dispara
     * NullPointException se não houver um valor disponível.
     *
     * @return -
     */
    public final long getLong() {
        String s = getValue();
        if (s == null) {
            throw new NullPointerException("Tag '" + getFullPath() + "' vazia");
        }
        return Long.parseLong(s);
    }

    /**
     * Retorna o texto do elemento atual (sub-texto) como double. Dispara
     * NullPointException se não houver um valor disponível.
     *
     * @return -
     */
    public final double getDouble() {
        String s = getValue();
        if (s == null) {
            throw new NullPointerException("Tag '" + getFullPath() + "' vazia");
        }
        return Double.parseDouble(s);
    }

    /**
     * Obtem o valor no endereço fornecido.
     *
     * @param xPath Caminho para o valor (string) desejado
     * @return Se existe o destino e apontado pelo nome e nele existe um valor,
     * esse é retornado. Caso contrário devolve null.
     */
    public final String getValue(String xPath) {
        return getValueText(getNode(xPath));
    }

    /**
     * Obtem o valor no endereço fornecido, ou valor default, se a pesquisa
     * resultar em null.
     *
     * @param xPath    Caminho para o valor (string) desejado
     * @param defaultV valor a retornado se a pesquisa for null
     * @return -
     */
    public final String getValue(String xPath, String defaultV) {
        String s = getValueText(getNode(xPath));
        if (s == null) {
            return defaultV;
        }
        return s;
    }

    /**
     * Obtem o valor no endereço fornecido. Se o xPAth não existir ou apontar
     * para um Element ou atributo sem texto, dispara um exception.
     *
     * @param xPath Caminho para o valor (string) desejado
     * @return -
     * @throws NullPointerException Se a pesquisa resultar em null
     */
    public final String getValueNotNull(String xPath) throws NullPointerException {
        Node node = getNode(xPath);
        if (node == null) {
            throw new NullPointerException("xPath '"
                    + xPath
                    + "' não existe em '"
                    + getFullPath()
                    + "'");
        }
        String value = getValueText(node);
        if (value == null) {
            throw new NullPointerException("No '"
                    + xPath
                    + "' está vazio (fullPath="
                    + XPathToolkit.getFullPath(node)
                    + ")");
        }
        return value;
    }

    /**
     * Busca os valores dos elementos filhos com o nome informado. Se o nome for
     * null, retorna o valor de todos os filhos.
     *
     * @param xPath dos elementos a terem os valores retornados
     * @return sempre diferente de null
     */
    public final List<String> getValues(String xPath) {
        return XPathToolkit.getValues(this, xPath);
    }

    /**
     * Equivalente a getBoolean, serve para escrever código mais legíveis.
     *
     * @param xPath caminho xpath ou nome do elemento desejado
     * @return O boolean convertido de string
     */
    public final boolean is(String xPath) {
        return getBoolean(xPath);
    }

    /**
     * Equivalente a getBoolean, serve para escrever código mais legíveis.
     *
     * @param xPath        caminho xpath ou nome do elemento desejado
     * @param defaultValue Valor a ser utilziado se não encontrar nenhum valor
     *                     no caminho indicado no xPath
     * @return O boolean convertido de string
     */
    public final boolean is(String xPath, boolean defaultValue) {
        return getBoolean(xPath, defaultValue);
    }

    /**
     * Retorna o valor em boolean do sub-elemento indicado pelo caminho xpath.
     *
     * @param xPath caminho xpath ou nome do elemento desejado
     * @return O boolean convertido de string
     */
    public final boolean getBoolean(String xPath) {
        String s = getValueNotNull(xPath);
        if (Boolean.TRUE.toString().equals(s)) {
            return true;
        } else if (Boolean.FALSE.toString().equals(s)) {
            return false;
        }
        throw new SingularException("O valor em " + xPath + " não é boolean = " + s);
    }

    /**
     * Retorna o valor em boolean do sub-elemento indicado pelo caminho xpath.
     *
     * @param xPath        caminho xpath ou nome do elemento desejado
     * @param defaultValue Valor a ser utilziado se não encontrar nenhum valor
     *                     no caminho indicado no xPath
     * @return O boolean convertido de string
     */
    public final boolean getBoolean(String xPath, boolean defaultValue) {
        String s = getValue(xPath);
        if (s == null) {
            return defaultValue;
        } else if (Boolean.TRUE.toString().equals(s)) {
            return true;
        } else if (Boolean.FALSE.toString().equals(s)) {
            return false;
        }
        throw new SingularException("O valor em " + xPath + " não é boolean = " + s);
    }

    /**
     * Retorna o valor em int do sub-elemento indicado pelo caminho xpath.
     *
     * @param xPath caminho xpath ou nome do elemento desejado
     * @return O inteiro convertido de string
     */
    public final int getInt(String xPath) {
        return Integer.parseInt(getValueNotNull(xPath));
    }

    /**
     * Retorna o valor do no indicado pelo caminho xpath comvertido para
     * Integer.
     *
     * @param xPath caminho xpath ou nome do elemento desejado
     * @return O inteiro convertido de string ou null se getValue(xPath)==null.
     */
    public final Integer getInteger(String xPath) {
        String s = getValue(xPath);
        if (s == null) {
            return null;
        }
        return Integer.valueOf(s);
    }

    /**
     * Retorna o valor em int do sub-elemento indicado pelo caminho xpath.
     *
     * @param xPath    caminho xpath ou nome do elemento desejado
     * @param defaultV valor a se retornado se o xPath resultar em null
     * @return O long convertido de string
     */
    public final int getInt(String xPath, int defaultV) {
        String s = getValue(xPath);
        if (s == null) {
            return defaultV;
        }
        return Integer.parseInt(s);
    }

    /**
     * Retorna o valor em long do sub-elemento indicado pelo caminho xpath.
     *
     * @param xPath caminho xpath ou nome do elemento desejado
     * @return O long convertido de string
     */
    public final long getLong(String xPath) {
        return Long.parseLong(getValueNotNull(xPath));
    }

    /**
     * Retorna o valor em long do sub-elemento indicado pelo caminho xpath.
     *
     * @param xPath    caminho xpath ou nome do elemento desejado
     * @param defaultV valor a se retornado se o xPath resultar em null
     * @return O long convertido de string
     */
    public final long getLong(String xPath, long defaultV) {
        String s = getValue(xPath);
        if (s == null) {
            return defaultV;
        }
        return Long.parseLong(s);
    }

    /**
     * Retorna o valor em double do sub-elemento indicado pelo caminho xpath.
     * Utiliza Double.parseDouble(), ou seja, o formato deve ser com ponto como
     * separador de decimal.
     *
     * @param xPath caminho xpath ou nome do elemento desejado
     * @return O double convertido de string
     */
    public final double getDouble(String xPath) {
        return Double.parseDouble(getValueNotNull(xPath));
    }

    /**
     * Retorna o valor em double do sub-elemento apontando pelo nome ou caminho
     * xpath apontado. Utiliza Double.parseDouble(), ou seja, o formato deve ser
     * com ponto como separador de decimal.
     *
     * @param xPath    caminho xpath ou nome do elemento desejado
     * @param defaultV valor a se retornado se o xPath resultar em null
     * @return O double convertido de string
     */
    public final double getDouble(String xPath, double defaultV) {
        String s = getValue(xPath);
        if (s == null) {
            return defaultV;
        }
        return Double.parseDouble(s);
    }

    /**
     * Retorna o valor do no indicado pelo caminho xpath comvertido para um
     * objeto Double.
     *
     * @param xPath caminho xpath ou nome do elemento desejado
     * @return O Double convertido de string ou null se getValue(xPath)==null.
     */
    public final Double getDoubleObject(String xPath) {
        String s = getValue(xPath);
        if (s == null) {
            return null;
        }
        return new Double(s);
    }

    /**
     * Converte a String no endereço xPath com codificação BASE64 de volta para
     * um array de bytes.
     *
     * @param xPath endereço do valor (atributo, tag, etc.) a ser convertido
     * @return null se não encontrado
     */
    public final byte[] getByteBASE64(String xPath) {
        return MElementWrapper.fromBASE64(getValue(xPath));
    }

    /**
     * Converte a String no endereço xPath com codificação BASE64 de volta para
     * bytes escrevendo para a saida informada.
     *
     * @param xPath endereço do valor (atributo, tag, etc.) a ser convertido
     * @param out   Destino do bytes convertidos
     */
    public final void getByteBASE64(String xPath, OutputStream out) {
        MElementWrapper.fromBASE64(getValueNotNull(xPath), out);
    }

    /**
     * Transforma o valor do campo para java.util.Date. Espera um campo no
     * formato "yyyy-mm-dd hh:mm:ss.fffffffff".
     *
     * @param xPath endereço do valor (atributo, tag, etc.) a ser convertido
     * @return -
     */
    public final java.util.Date getDate(String xPath) {
        String value = getValue(xPath);
        if (value == null) {
            return null;
        }
        return ConversorDataISO8601.getDate(value);
    }

    /**
     * Transforma o valor do campo para Calendar. Espera um campo no formato
     * "yyyy-mm-dd hh:mm:ss.fffffffff".
     *
     * @param xPath endereço do valor (atributo, tag, etc.) a ser convertido
     * @return -
     */
    public final GregorianCalendar getCalendar(String xPath) {
        String value = getValue(xPath);
        if (value == null) {
            return null;
        }
        return ConversorDataISO8601.getCalendar(value);
    }

    /**
     * Transforma o valor do campo em um número formato com separado de decimal
     * e milhar. Deixa livre a quantidade de casa decimais, ou seja, vai
     * colocar quanta forem necessárias.
     *
     * @param xPath endereço do valor (atributo, tag, etc.) a ser convertido
     * @return string vazia se o elemento não existir ou não tiver valor
     */
    public final String formatNumber(String xPath) {
        return ConversorToolkit.printNumber(getDouble(xPath, 0), -1, false);
    }

    /**
     * Transforma o valor do campo em um número formato com separado de decimal
     * e milhar. Deixa livre a quantidade de casa decimais, ou seja, vai
     * colocar quanta forem necessárias.
     *
     * @param xPath     endereço do valor (atributo, tag, etc.) a ser convertido
     * @param printZero Se falso e o valor zero, então retorna string vazia
     * @return string vazia se o elemento não existir ou não tiver valor
     */
    public final String formatNumber(String xPath, boolean printZero) {
        return ConversorToolkit.printNumber(getDouble(xPath, 0), -1, printZero);
    }

    /**
     * Transforma o valor do campo em um número formato com separado de decimal
     * e milhar.
     *
     * @param xPath   endereço do valor (atributo, tag, etc.) a ser convertido
     * @param decimals qtd. de casas decimais a serem exibidas (-1 deixa livre).
     * @return zero se o elemento não existir ou não tiver valor
     */
    public final String formatNumber(String xPath, int decimals) {
        return ConversorToolkit.printNumber(getDouble(xPath, 0), decimals);
    }

    /**
     * Transforma o valor do campo em um número formato com separado de decimal
     * e milhar com opção de não exibir zeros.
     *
     * @param xPath     endereço do valor (atributo, tag, etc.) a ser convertido
     * @param decimals   qtd. de casas decimais a serem exibidas (-1 deixa livre).
     * @param printZero Se falso e o valor zero, então retorna string vazia
     * @return string vazia se o elemento não existir ou não tiver valor
     */
    public final String formatNumber(String xPath, int decimals, boolean printZero) {
        return ConversorToolkit.printNumber(getDouble(xPath, 0), decimals, printZero);
    }

    /**
     * Transforma o valor do campo em uma string no formato de exibição de data
     * (dd/MM/yyyy).
     *
     * @param xPath endereço do valor (atributo, tag, etc.) a ser convertido
     * @return String vazia se o xPath não existir
     */
    public final String formatDate(String xPath) {
        java.util.Date d = getDate(xPath);
        return (d == null) ? "" : ConversorToolkit.printDate(d);
    }

    /**
     * Transforma o valor do campo em uma string no formato de exibição de data
     * segundo o formato solicitado.
     *
     * @param xPath   endereço do valor (atributo, tag, etc.) a ser convertido
     * @param formato a ser convertido pode ser "short", "medium", "long",
     *                "full" ou então customizado (ver java.text.SimpleDateFormat).
     * @return String vazia se o xPath não existir
     */
    public final String formatDate(String xPath, String formato) {
        java.util.Date d = getDate(xPath);
        return (d == null) ? "" : ConversorToolkit.printDate(d, formato);
    }

    /**
     * Transforma o valor do campo em uma string no formato de exibição de hora
     * (hh:mm:ss).
     *
     * @param xPath endereço do valor (atributo, tag, etc.) a ser convertido
     * @return String vazia se o xPath não existir
     */
    public final String formatHour(String xPath) {
        java.util.Date d = getDate(xPath);
        return (d == null) ? "" : ConversorToolkit.printHour(d);
    }

    /**
     * A partir do atual procura o Node no xPath (Elemento, atributo, etc.).
     *
     * @param xPath caminho do elemento desejado
     * @return O Node no destino ou null se o xPath não existir
     * @see XPathToolkit
     */
    public final Node getNode(String xPath) {
        return XPathToolkit.selectNode(this, xPath);
    }

    /**
     * A partir do atual retorna um percorredor de todos os Element que
     * satisfazem a consulta xPath. Se a consulta for null, retorna todos os
     * filhos. Exemplos:
     * <p/>
     * <xmp>raiz.selectElements(null); //Retorna todos os filhos imediatos
     * raiz.selectElements("aluno"); //todos as tags "aluno" imediatas
     * raiz.selectElements("aluno/nota"); //todos as tags nota de baixo de
     * //todos os filhos aluno raiz.selectElements("aluno[@id=20]");//Todos as
     * tags aluno que possuem //um atributo id igual a 20 </xmp>
     *
     * @param xPath caminho do elemento desejado
     * @return Sempre diferente de null
     * @see XPathToolkit XPathToolkit para entender mais sobre xPath
     */
    @Nonnull
    public final MElementResult selectElements(@Nullable String xPath) {
        return new MElementResult(this, xPath);
    }

    /**
     * Segue a mesma lógia de selectElements(String), mas retorna como iterator.
     *
     * @param xPath caminho dos elementos desejados
     * @return Sempre diferente de null
     */
    @Nonnull
    public final Iterator<MElement> iterator(@Nullable String xPath) {
        MElementResult rs = new MElementResult(this, xPath);
        return rs.iterator();
    }

    /**
     * A partir do atual procura o Element no xPath.
     *
     * @param xPath caminho do elemento desejado
     * @return O Node no destino ou null se o xPath não existir
     * @throws SingularException Se o node no xPath não for um Element
     * @see XPathToolkit
     */
    public final MElement getElement(String xPath) {
        return toMElement(XPathToolkit.selectElement(this, xPath));
    }

    /**
     * Retornas todos os elementos com um nome específico. Uma forma mais rápida
     * (e talvez mais prática de pegar todos os filhos) é:<br>
     * <xmp>MElementResult e = raiz.selectElements(xPAth); while( e.next) {
     * //faz algo com e..... } </xmp>
     *
     * @param xPath dos elementos a serem retornados. Se for null retorna todos
     *              os MElement imediantamente filhos.
     * @return a lista com os elementos ou um array de tamanho zero
     */
    @Nonnull
    public final MElement[] getElements(@Nullable String xPath) {
        return selectElements(xPath).getAll();
    }

    /**
     * Método utilizado para colocar apenas o conteúdo de um elemento dentro de
     * outro elemento. O elemento, portanto, não é copiado, apenas todos os
     * elementos dentro dele.
     *
     * @param no a ter seus filhos copiados
     */
    public final void copyConteudo(Element no) {
        MElementWrapper.copyElement(this, no);
    }

    /**
     * Método utilizado para colocar um elemento e todo o seu conteúdo dentro de
     * outro elemento, podendo ser usado um outro nome ao invés do nome do
     * elemento sendo copiado. Para manter o nome do elemento original, passar
     * <code>novoNome</code> igual a <code>null</code>.
     *
     * @param no       Element a ser inserido
     * @param newName se diferente de null
     * @return Elemento copia crido debaixo do atual.
     */
    public final MElement copy(Element no, String newName) {
        return toMElement(MElementWrapper.copyElement(this, no, newName));
    }

    /**
     * Gera o path completo (em xpath) do elemento a partir de seu raiz. Pode
     * ser utilizado para recuperar o elemento via gerElemento no raiz. Se for
     * um elemento repetido, então adiciona um índice (formato [N]).
     *
     * @return string xPath do elemento.
     */
    public final String getFullPath() {
        return XPathToolkit.getFullPath(this);
    }

    /**
     * Escreve o XML organizado as sub-tags em níveis (acresenta espaço). Um
     * parse do string gerada por esse método pode não gera o mesmo xml devido a
     * inclusão de formatação (utilize o método print()).
     *
     * @param out saída destino.
     */
    public final void printTabulado(OutputStream out) {
        PrintWriter pw = getWriterFactory().newPrintWriter(out);
        xmlWriter.printDocumentIndentado(pw, this, true);
        pw.flush();
    }

    /**
     * Escreve o XML organizado as sub-tags em níveis (acresenta espaço). Um
     * parse do string gerada por esse método pode não gera o mesmo xml devido a
     * inclusão de formatação (utilize o método print()).
     *
     * @param out saída destino.
     */
    public final void printTabulado(PrintWriter out) {
        xmlWriter.printDocumentIndentado(out, this, true);
    }

    /**
     * Escreve para a saida padrão (System.out) o XML organizado as sub-tags em
     * níveis (acresenta espaço).
     */
    public final void printTabulado() {
        printTabulado(System.out);
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML. Para
     * impressões mais legíveis utilize printTabulado().
     *
     * @param out saída destino
     */
    public final void print(OutputStream out) {
        PrintWriter pw = getWriterFactory().newPrintWriter(out);
        xmlWriter.printDocument(pw, this, true);
        pw.flush();
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML. Para
     * impressões mais legíveis utilize printTabulado().
     *
     * @param out saída destino
     */
    public final void print(PrintWriter out) {
        xmlWriter.printDocument(out, this, true);
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML. Para
     * impressões mais legíveis utilize printTabulado().
     *
     * @param out         saída destino
     * @param printHeader Se true, adiciona string de indentificação de arquivo
     *                    XML. Se false, depois não será possível fazer parse do resultado
     *                    sem informaçoes complementares (header).
     */
    public final void print(PrintWriter out, boolean printHeader) {
        xmlWriter.printDocument(out, this, printHeader);
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML. Para
     * impressões mais legíveis utilize printTabulado().
     *
     * @param out               saída destino
     * @param printHeader       Se true, adiciona string de indentificação de arquivo
     *                          XML. Se false, depois não será possível fazer parse do resultado
     *                          sem informaçoes complementares (header).
     * @param converteEspeciais se verdadeiro converte os caracteres '<' '>' e '&' para
     *                          seus respectivos escapes.
     */
    public final void print(PrintWriter out, boolean printHeader, boolean converteEspeciais) {
        xmlWriter.printDocument(out, this, printHeader, converteEspeciais);
    }

    /**
     * Retorna o Element que está antes do atual mas no mesmo nível.
     *
     * @return null se o atual já for o primeiro Element da lista.
     */
    public final MElement getIrmaoAnterior() {
        return findPreviousElement(getPreviousSibling(), null);
    }

    /**
     * Retorna o Element que está antes do atual mas no mesmo nível e que possui
     * o mesmo nome do elemento atual.
     *
     * @return null se o atual já for o primeiro Element da lista com o nome.
     */
    public final MElement getGemeoAnterior() {
        return findPreviousElement(getPreviousSibling(), getNodeName());
    }

    /**
     * Retorna o próximo Element que está no mesmo nível do elemento atual.
     *
     * @return null se esse for o último elemento.
     */
    public final MElement getProximoIrmao() {
        return procurarProximoElement(getNextSibling(), null);
    }

    /**
     * Retorna o próximo Element que está no mesmo nível do elemento atual e que
     * possui o mesmo nome do Element atual.
     *
     * @return null se esse for o último elemento com o nome.
     */
    public final MElement getProximoGemeo() {
        return procurarProximoElement(getNextSibling(), getNodeName());
    }

    /**
     * Retorna o primeiro Element filho do atual.
     *
     * @return null se não houve nenhum nó filho do tipo Element
     */
    @Nullable
    public final MElement getPrimeiroFilho() {
        return procurarProximoElement(getFirstChild(), null);
    }

    /**
     * Retorna o primeiro Element filho do atual com um nome específico.
     *
     * @param name do Element filho a ser encontrado.
     * @return null se não houve nenhum nó filho do tipo Element
     */
    public final MElement getPrimeiroFilho(String name) {
        if (name == null) {
            throw new IllegalArgumentException("O nome não pode ser null");
        }
        return procurarProximoElement(getFirstChild(), name);
    }

    /**
     * Retorna o ultimo Element filho do atual.
     *
     * @return null se não houve nenhum nó filho do tipo Element
     */
    public final MElement getUltimoFilho() {
        return findPreviousElement(getLastChild(), null);
    }

    /**
     * Procura pelo node do tipo Element anterior (incluindo o nó informado).
     *
     * @param no2  Ponto de partida da pesquisa
     * @param name Nome do Element a ser retornado. Se for null retorna o
     *             primeiro a ser encontrado.
     * @return Um Element ou null se não encontrar.
     */
    private MElement findPreviousElement(Node no2, String name) {
        for (Node current = no2; current != null; current = current.getPreviousSibling()) {
            if (XmlUtil.isNodeTypeElement(current, name)) {
                return toMElement(current);
            }
        }
        return null;
    }

    /**
     * Procura pelo proximo node do tipo Element (incluindo o nó informado).
     *
     * @param no   Ponto de partida da pesquisa
     * @param name Nome do Element a ser retornado. Se for null retorna o
     *             primeiro a ser encontrado.
     * @return Um Element ou null se não encontrar.
     */
    private MElement procurarProximoElement(Node no, String name) {
        return toMElement(XmlUtil.nextSiblingOfTypeElement(no, name));
    }

    /**
     * Gera o XML to elemento conforme o funcionamento do método printTabulado
     * (utilizar preferencialment printTabulado). Existe como conveniência
     * quando não houver um PrintWriter ou PrintStream disponível.
     *
     * @return o XML com um tag por linha e alinhado conforme o nível
     */
    @Override
    public String toString() {
        StringPrintWriter spw = getWriterFactory().newStringPrinWriter();
        printTabulado(spw);
        spw.flush();
        return spw.toString();
    }

    /**
     * Gera o XML do elemento conforme o funcionamento do método print (utilizar
     * preferencialment printTabulado). Existe como conveniência quando não
     * houver um PrintWriter ou PrintStream disponível.
     *
     * @return a String que feito parse, retorna o mesmo conteudo
     */
    public final String toStringExato() {
        StringPrintWriter spw = getWriterFactory().newStringPrinWriter();
        print(spw, true, true);
        spw.flush();
        return spw.toString();
    }

    /**
     * Gera o XML do elemento conforme o funcionamento do método print (utilizar
     * preferencialment printTabulado). Existe como conveniência quando não
     * houver um PrintWriter ou PrintStream disponível.
     *
     * @param printHeader Indica se será adiciona o identificado inicial de
     *                    arquivo XML. Se for false, não será possível fazer parse do
     *                    resultado sem a adição de informações complementares.
     * @return a String que feito parse, retorna o mesmo conteudo
     */
    public final String toStringExato(boolean printHeader) {
        StringPrintWriter spw = getWriterFactory().newStringPrinWriter();
        print(spw, printHeader, true);
        spw.flush();
        return spw.toString();
    }

    /**
     * Gera o XML do elemento conforme o funcionamento do método print.
     *
     * @return a byte array que feito parse, retorna o mesmo conteudo
     */
    public final byte[] toByteArray() {
        StringPrintWriter spw = getWriterFactory().newStringPrinWriter();
        print(spw);
        spw.flush();
        return spw.toByteArray();
    }

    public String toJSONString() {
        StringPrintWriter spw = getWriterFactory().newStringPrinWriter();
        jsonWriter.printDocument(spw, this, true);
        spw.flush();
        return spw.toString();
    }


}
