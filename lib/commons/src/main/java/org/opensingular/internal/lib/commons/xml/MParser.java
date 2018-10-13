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

import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Faz o parse de um XML, já validando contra um dtd. Para os casos mais
 * simples, sem DTD ,existem métodos estáticos como {@link #parse(String)}.
 * <p/>Para os casos em que for necessário validar o XML contra um DTD e for
 * necessário informar aonde o arquivo de dtd se encontar em vez de simplesmente
 * deixar a carga remota do arquivo, pode ser instanciado uma parse e informado
 * todos as localização de arquivos mediante os métodos {@link #addInputSource}.
 * <p>
 * <b>Exemplo de uso</b>:<br>
 * <p>
 * <xmp>
 * String sDTD = ....
 * String sXML = ....
 * MParser p = new MParser();
 * // Informa referência para DTD em uma String
 * p.addInputSource("http://eee/teste-dtd-existe.dtd", sDTD);
 * // Informa referência para DTD em um arquivo no pacote da classe chamadora
 * p.addInputSource("http://eee/teste-dtd-existe2.dtd", getClass(), "teste.dtd");
 * MElement raiz = p.parseComResolver(sXML);
 * </xmp>
 *
 * @author Daniel C. Bordin
 */
public final class MParser {

    /**
     * Informa um outro EntityResolver a ser usado em conjunto com o próximo.
     */
    private EntityResolver entityResolver_;

    /**
     * Lista de trocas de refencias por um conteudo local.
     */
    private MEntityResolver sourcesLocais_;

    /**
     * Informa um entity resolver a ser usado na lozalização de recurso ao
     * fazer o parser (em geral na busca de arquivos DTD). Primeiro usará o
     * mapeamentos para recursos locais (informado via addInputSource()),
     * caso não encontre, então usao o entityResolver informado.
     */
    public void setEntityResolver(EntityResolver entityResolver) {
        entityResolver_ = entityResolver;
    }

    /**
     * Adiciona um mapeamento de um id (em geral um dtd) para um local
     * específico de modo a não utilizar a resolução default do parse. Por
     * exemplo, pode-se adicionar um arquivo local em substituição para URL
     * (http://www.acme.com/test.dtd) e assim evitar que o servidor tente
     * carregar diretamente desse endereço ou apresente um erro de recurso não
     * encontrado.
     *
     * @param systemId Nome do recurso (no XML a ser lido) quer será
     * interceptado.
     * @param source Local de onde deverá ler a informação.
     * @see org.xml.sax.EntityResolver
     * @see javax.xml.parsers.DocumentBuilder#setEntityResolver
     */
    public void addInputSource(String systemId, InputSource source) {
        if (sourcesLocais_ == null) {
            sourcesLocais_ = new MEntityResolver();
        }
        sourcesLocais_.addSource(systemId, source);
    }

    /**
     * Método de conveniência em relaçao ao método
     * {@link #addInputSource(String, InputSource)}que recebe uma String.
     *
     * @param systemId Nome do recurso (no XML a ser lido) quer será
     * interceptado.
     * @param value a ser utilizado toda vez que pedir o recurso.
     */
    public void addInputSource(String systemId, String value) {
        InputSource is = new InputSource(systemId);
        is.setCharacterStream(new StringReader(value));
        addInputSource(systemId, is);
    }

    /**
     * Método de conveniência em relaçao ao método
     * {@link #addInputSource(String, InputSource)}que recebe uma InputStream.
     *
     * @param systemId Nome do recurso (no XML a ser lido) quer será
     * interceptado.
     * @param value a ser utilizado toda vez que pedir o recurso.
     */
    public void addInputSource(String systemId, InputStream value) {
        InputSource is = new InputSource(systemId);
        is.setByteStream(value);
        addInputSource(systemId, is);
    }

    /**
     * Método de conveniência em relaçao ao método
     * {@link #addInputSource(String, InputSource)}que busca um arquivo que
     * está em um classpath e de acordo com a posição do recurso em relação a
     * classe informada. <br/>Por exemplo, addInputSource("a", B.class,
     * "teste.dtd") busca o dtd no mesmo pacote da classe B.
     *
     * @param systemId Nome do recurso (no XML a ser lido) quer será
     * interceptado.
     * @param ref Classe a partir do qual será feia a busca pelo recurso.
     * @param resourceName Arquivo em relação ao pacote da classe de referência.
     * Inciando com '/' indica para começa do raiz dos pacotes.
     * @see java.lang.Class#getResourceAsStream
     */
    public void addInputSource(String systemId, Class<?> ref, String resourceName) {
        InputStream in = ref.getResourceAsStream(resourceName);
        if (in == null) {
            throw new SingularException("Nao foi encontrado o recurso '"
                    + resourceName
                    + "' tendo por base a classe "
                    + ref.getName());
        }
        addInputSource(systemId, in);
    }

    private EntityResolver getResolver() {
        if (sourcesLocais_ != null) {
            return sourcesLocais_;
        }
        return entityResolver_;
    }

    /**
     * Para o parse do xml com validação e verificação de namespace e utilizando
     * os mapeamento de InputSource efetuados.
     */
    @Nonnull
    public MElement parseComResolver(@Nonnull String xml) throws SAXException, IOException {
        InputSource is = new InputSource(new StringReader(xml));
        return parseToElement(is, true, true, getResolver());
    }

    /**
     * Para o parse do xml com validação e verificação de namespace e utilizando
     * os mapeamento de InputSource efetuados.
     */
    @Nonnull
    public MElement parseComResolver(@Nonnull byte[] xml) throws SAXException, IOException {
        InputSource is = new InputSource(new ByteArrayInputStream(xml));
        return parseToElement(is, true, true, getResolver());
    }

    /**
     * Para o parse do xml com validação e verificação de namespace e utilizando
     * os mapeamento de InputSource efetuados.
     */
    @Nonnull
    public MElement parseComResolver(@Nonnull InputStream in) throws SAXException, IOException {
        return parseToElement(new InputSource(in), true, true, getResolver());
    }

    private final class MEntityResolver implements EntityResolver {

        private final Map<String, InputSource> mapeamentoSourceLocal_ = new HashMap<>();

        void addSource(String systemId, InputSource source) {
            mapeamentoSourceLocal_.put(systemId, source);
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
                IOException {
            InputSource o = mapeamentoSourceLocal_.get(systemId);
            if (o != null) {
                return o;
            }
            if (entityResolver_ != null) {
                return entityResolver_.resolveEntity(publicId, systemId);
            }
            return null;
        }
    }

    /**
     * Faz um parse (leitura de um XML) a partir de uma string. Já faz a leitura do xml com namespaceAware ativo.
     */
    @Nonnull
    public static MElement parse(String xml) throws SAXException, IOException {
        return parse(new StringReader(xml));
    }

    /**
     * Faz um parse (leitura de um XML) a partir de uma stream.
     */
    @Nonnull
    public static MElement parse(@Nonnull Reader in) throws SAXException, IOException {
        return parseToElement(new InputSource(in), true, false, null);
    }

    /**
     * Faz um parse (leitura de um XML) a partir de uma stream.
     */
    @Nonnull
    public static MElement parse(@Nonnull byte[] content) throws SAXException, IOException {
        return parse(new ByteArrayInputStream(content));
    }

    /**
     * Faz um parse (leitura de um XML) a partir de uma stream.
     */
    @Nonnull
    public static MElement parse(@Nonnull InputStream in) throws SAXException, IOException {
        return parse(in, true, false);
    }

    /**
     * Faz um parse (leitura de um XML) a partir de uma stream.
     *
     * @param in Fonte de Leitura do XML
     * @param namespaceAware Indica se o parse levará em consideração o a
     * existência de namespace na leitura
     * @param validating Indica se o parse procurará e utilizará a definição da
     * estrutura do XML (tipicamente um DTD ou Schema).
     * @return O MElement representado na stream
     */
    @Nonnull
    public static MElement parse(@Nonnull InputStream in, boolean namespaceAware, boolean validating)
            throws SAXException, IOException {
        return parseToElement(new InputSource(in), namespaceAware, validating, null);
    }

    /**
     * Faz parse de uma InputStream.
     */
    @Nonnull
    private static MElement parseToElement(@Nonnull InputSource in, boolean namespaceAware, boolean validating,
            @Nullable EntityResolver entityResolver) throws SAXException, IOException {

        DocumentBuilderFactory factory = MElementWrapper.getDocumentBuilderFactory(namespaceAware,
                validating);
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (Exception e){
            throw SingularException.rethrow(e.getMessage(), e);
        }

        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (Exception e) {
            //      } catch(javax.xml.parsers.ParserConfigurationException e) {
            throw SingularException.rethrow("Não instanciou o parser XML: ", e);
        }
        if (entityResolver != null) {
            builder.setEntityResolver(entityResolver);
        }
        ErrorHandlerMElement eHandler = new ErrorHandlerMElement();
        builder.setErrorHandler(eHandler);
        Element result = builder.parse(in).getDocumentElement();
        result.normalize();
        if (eHandler.hasErros()) {
            throw new SAXException(eHandler.getErros());
        }
        return MElement.toMElementNotNull(result);
    }
}