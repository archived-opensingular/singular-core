package br.net.mirante.singular.ui.util.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
 * @author Daniel C. Bordin - www.miranteinfo.com
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
     * @param valor a ser utilizado toda vez que pedir o recurso.
     */
    public void addInputSource(String systemId, String valor) {
        InputSource is = new InputSource(systemId);
        is.setCharacterStream(new StringReader(valor));
        addInputSource(systemId, is);
    }

    /**
     * Método de conveniência em relaçao ao método
     * {@link #addInputSource(String, InputSource)}que recebe uma InputStream.
     *
     * @param systemId Nome do recurso (no XML a ser lido) quer será
     * interceptado.
     * @param valor a ser utilizado toda vez que pedir o recurso.
     */
    public void addInputSource(String systemId, InputStream valor) {
        InputSource is = new InputSource(systemId);
        is.setByteStream(valor);
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
     * @param nomeRecurso Arquivo em relação ao pacote da classe de referência.
     * Inciando com '/' indica para começa do raiz dos pacotes.
     * @see java.lang.Class#getResourceAsStream
     */
    public void addInputSource(String systemId, Class<?> ref, String nomeRecurso) {
        InputStream in = ref.getResourceAsStream(nomeRecurso);
        if (in == null) {
            throw new RuntimeException("Nao foi encontrado o recurso '"
                    + nomeRecurso
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
     *
     * @param xml -
     * @return DOM resultante do parse.
     *
     * @throws SAXException -
     * @throws IOException -
     */
    public MElement parseComResolver(String xml) throws SAXException, IOException {
        InputSource is = new InputSource(new StringReader(xml));
        return MElement.toMElement(parseToElement(is, true, true, getResolver()));
    }

    /**
     * Para o parse do xml com validação e verificação de namespace e utilizando
     * os mapeamento de InputSource efetuados.
     *
     * @param xml -
     * @return DOM resultante do parse.
     *
     * @throws SAXException -
     * @throws IOException -
     */
    public MElement parseComResolver(byte[] xml) throws SAXException, IOException {
        InputSource is = new InputSource(new ByteArrayInputStream(xml));
        return MElement.toMElement(parseToElement(is, true, true, getResolver()));
    }

    /**
     * Para o parse do xml com validação e verificação de namespace e utilizando
     * os mapeamento de InputSource efetuados.
     *
     * @param in -
     * @return DOM resultante do parse.
     *
     * @throws SAXException -
     * @throws IOException -
     */
    public MElement parseComResolver(InputStream in) throws SAXException, IOException {
        return MElement.toMElement(parseToElement(new InputSource(in), true, true, getResolver()));
    }

    private final class MEntityResolver implements EntityResolver {

        private final Map<String, InputSource> mapeamentoSourceLocal_ = new HashMap<>();

        public void addSource(String systemId, InputSource source) {
            mapeamentoSourceLocal_.put(systemId, source);
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
                IOException {
            Object o = mapeamentoSourceLocal_.get(systemId);
            if (o != null) {
                return (InputSource) o;
            }
            if (entityResolver_ != null) {
                return entityResolver_.resolveEntity(publicId, systemId);
            }
            return null;
        }
    }

    /**
     * Faz um parse (leitura de um XML) a partir de uma string. Já faz a leitura
     * do xml com namespaceAware ativo.
     *
     * @param xml -
     * @return O MElement representado na stream
     *
     * @throws SAXException Se ocorrer um erro de parse. Se houver mais de um
     * erro no parse, então todos são retornado no texto da mensagem.
     * @throws IOException Se ocorrer um erro na leitura da stream
     */
    public static MElement parse(String xml) throws SAXException, IOException {
        //Apesar deprecated a linha abaixo é mais efeciente do que a linha
        //comentada a seguir e apresenta as mesmas limitações.
        StringReader in = new StringReader(xml);
        //ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
        return MElement.toMElement(parseToElement(new InputSource(in), true, false, null));
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
     *
     * @throws SAXException Se ocorrer um erro de parse. Se houver mais de um
     * erro no parse, então todos são retornado no texto da mensagem.
     * @throws IOException Se ocorrer um erro na leitura da stream
     */
    public static MElement parse(InputStream in, boolean namespaceAware, boolean validating)
            throws SAXException, IOException {
        return MElement.toMElement(parseToElement(new InputSource(in), namespaceAware, validating,
                null));
    }

    /**
     * Faz parse de uma InputStream.
     */
    static Element parseToElement(InputSource in, boolean namespaceAware, boolean validating,
            EntityResolver entityResolver) throws SAXException,
            IOException {

        DocumentBuilderFactory factory = MElementWrapper.getDocumentBuilderFactory(namespaceAware,
                validating);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (Exception e) {
            //      } catch(javax.xml.parsers.ParserConfigurationException e) {
            throw new RuntimeException("Não instanciou o parser XML: ", e);
        }
        if (entityResolver != null) {
            builder.setEntityResolver(entityResolver);
        }
        ErrorHandlerMElement eHandler = new ErrorHandlerMElement();
        builder.setErrorHandler(eHandler);
        Element result = builder.parse(in).getDocumentElement();
        if (eHandler.hasErros()) {
            throw new SAXException(eHandler.getErros());
        }
        return result;
    }
}