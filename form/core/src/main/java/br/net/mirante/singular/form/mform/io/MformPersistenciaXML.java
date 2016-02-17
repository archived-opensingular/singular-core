package br.net.mirante.singular.form.mform.io;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.STypeAnnotationList;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.util.xml.MDocument;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.util.xml.MParser;

/**
 * Métodos utilitários para converter instancias e anotaçãoes para e de XML.
 *
 * @author Daniel C. Bordin
 */
public class MformPersistenciaXML {

    public static final String ATRIBUTO_ID = "id";
    public static final String ATRIBUTO_LAST_ID = "lastId";

    /**
     * Cria uma instância para do tipo informado com o conteúdo persistido no
     * XML informado.
     */
    public static <T extends SInstance> T fromXML(SType<T> tipo, String xmlString) {
        return fromXML(tipo, parseXml(xmlString), null);
    }

    /**
     * Cria uma instância para do tipo informado com o conteúdo persistido no
     * XML informado.
     */
    public static <T extends SInstance> T fromXML(SType<T> tipo, String xmlString, SDocumentFactory documentFactory) {
        return fromXML(tipo, parseXml(xmlString), documentFactory);
    }

    /**
     * Cria uma instância para do tipo informado com o conteúdo persistido no
     * XML informado.
     */
    public static <T extends SInstance> T fromXML(SType<T> tipo, MElement xml) {
        return fromXML(tipo, xml, null);
    }

    /**
     * Cria uma instância para do tipo informado com o conteúdo persistido no
     * XML informado.
     */
    public static <T extends SInstance> T fromXML(SType<T> tipo, MElement xml, SDocumentFactory documentFactory) {
        T novo = documentFactory == null ? tipo.novaInstancia() : documentFactory.createInstance(tipo);
        Integer lastId = 0;
        if(xml !=  null) {  lastId = xml.getInteger("@" + ATRIBUTO_LAST_ID); }

        // Colocar em modo de não geraçao de IDs
        novo.getDocument().setLastId(-1);
        fromXML(novo, xml);

        int maxId = verificarIds(novo, new HashSet<>());
        if (lastId == null) {
            novo.getDocument().setLastId(maxId);
        } else {
            novo.getDocument().setLastId(lastId);
        }

        return novo;
    }

    private static int verificarIds(SInstance instancia, Set<Integer> ids) {
        Integer id = instancia.getId();
        if (ids.contains(id)) {
            throw new SingularFormException(
                    "A instancia " + instancia.getPathFull() + " tem um ID repetido (igual a outra instância) id=" + id);
        }
        if (instancia instanceof ICompositeInstance) {
            int max = id;
            for (SInstance filho : ((ICompositeInstance) instancia).getChildren()) {
                max = Math.max(max, verificarIds(filho, ids));
            }
            return max;
        }
        return id;
    }

    private static void fromXML(SInstance instancia, MElement xml) {
        if (xml == null)
            return; // Não precisa fazer nada
        lerAtributos(instancia, xml);
        if (instancia instanceof SISimple) {
            SISimple<?> instanciaS = (SISimple<?>) instancia;
            STypeSimple<?, ?> tipos = instanciaS.getMTipo();
            instancia.setValue(tipos.fromStringPersistencia(xml.getTextContent()));
        } else if (instancia instanceof SIComposite) {
            SIComposite instc = (SIComposite) instancia;
            for (SType<?> campo : instc.getMTipo().getFields()) {
                MElement xmlFilho = xml.getElement(campo.getSimpleName());
                if (xmlFilho != null) {
                    fromXML(instc.getCampo(campo.getSimpleName()), xmlFilho);
                }
            }
        } else if (instancia instanceof SList) {
            SList<?> lista = (SList<?>) instancia;
            String nomeFilhos = lista.getMTipo().getTipoElementos().getSimpleName();
            for (MElement xmlFilho : xml.getElements(nomeFilhos)) {
                SInstance filho = lista.addNovo();
                fromXML(filho, xmlFilho);
            }
        } else {
            throw new UnsupportedOperationException(instancia.getClass().getName());
        }
    }

    private static void lerAtributos(SInstance instancia, MElement xml) {
        NamedNodeMap atributos = xml.getAttributes();
        if (atributos != null) {
            for (int i = 0; i < atributos.getLength(); i++) {
                Attr at = (Attr) atributos.item(i);
                if (at.getName().equals(ATRIBUTO_ID)) {
                    instancia.setId(Integer.parseInt(at.getValue()));
                } else if (!at.getName().equals(ATRIBUTO_LAST_ID)) {
                    instancia.setValorAtributo(at.getName(), at.getValue());
                }
            }
        }
    }

    /**
     * Gera uma string XML representando a instância de forma apropriada para
     * persitência. Já trata escapes de caracteres especiais dentro dos valores.
     */
    public static Optional<String> toStringXML(SInstance instancia) {
        MElement xml = toXML(instancia);
        return xml == null ? Optional.empty() : Optional.of(xml.toStringExato());
    }

    public static MElement toXML(SInstance instancia) {
        return new PersistenceBuilderXML().withPersistNull(false).toXML(instancia);
    }

    public static MElement toXMLPreservingRuntimeEdition(SInstance instancia) {
        return new PersistenceBuilderXML().withPersistNull(true).withPersistAttributes(true).toXML(instancia);
    }

    final static MElement toXML(MElement pai, String nomePai, SInstance instancia, PersistenceBuilderXML builder) {

        MDocument xmlDocument = (pai == null) ? MDocument.newInstance() : pai.getMDocument();
        ConfXMLGeneration conf = new ConfXMLGeneration(builder, xmlDocument);

        MElement xmlResultado = toXML(conf, instancia);
        if (xmlResultado == null) {
            return pai;
        } else if (nomePai != null) {
            MElement novo = xmlDocument.createMElement(nomePai);
            novo.addElement(xmlResultado);
            xmlResultado = novo;
        }
        if (pai != null) {
            pai.addElement(xmlResultado);
            return pai;
        }
        xmlDocument.setRaiz(xmlResultado);
        if (builder.isPersistId()) {
            xmlResultado.setAttribute(ATRIBUTO_LAST_ID, Integer.toString(instancia.getDocument().getLastId()));
        }

        return xmlResultado;
    }

    private static MElement parseXml(String xmlString) {
        try {
            if (StringUtils.isBlank(xmlString)) {
                return null;
            }
            return MParser.parse(xmlString);
        } catch (Exception e) {
            throw new SingularFormException("Erro fazendo parde do xml", e);
        }
    }

    /**
     * Carrega na instancia informada as anotação contidas no xml, fazendo
     * parser do mesmo antes.
     *
     * @param xmlString
     *            Se nulo ou em branco, não faz carga
     */
    public static void annotationLoadFromXml(SInstance instance, String xmlString) {
        annotationLoadFromXml(instance, parseXml(xmlString));
    }

    /**
     * Carrega na instancia informada as anotação contidas no xml, fazendo
     * parser do mesmo antes.
     *
     * @param xmlAnnotations
     *            Se nulo, não faz carga
     */
    public static void annotationLoadFromXml(SInstance instance, MElement xmlAnnotations) {
        if (xmlAnnotations == null) {
            return;
        }
        SList<SIAnnotation> iAnnotations = annotationFromXml(instance.getDictionary(), xmlAnnotations);
        instance.as(AtrAnnotation::new).loadAnnotations(iAnnotations);
    }

    /**
     * Recupera as anotações gravas em um XML para o contexto do dicionário
     * informado.
     */
    private static SList<SIAnnotation> annotationFromXml(SDictionary dictionary, MElement xmlAnnotations) {
        STypeAnnotationList tipoAnnotation = dictionary.getType(STypeAnnotationList.class);
        return (SList<SIAnnotation>) MformPersistenciaXML.fromXML(tipoAnnotation, xmlAnnotations);
    }

    /** Gera um XML representando as anotações se existirem. */
    public static Optional<String> annotationToXmlString(SInstance instance) {
        return annotationToXml(instance).map(xml -> xml.toStringExato());
    }

    /** Gera um XML representando as anotações se existirem. */
    public static Optional<MElement> annotationToXml(SInstance instance) {
        AtrAnnotation annotatedInstance = instance.as(AtrAnnotation::new);
        List<SIAnnotation> allAnnotations = annotatedInstance.allAnnotations();
        if (!allAnnotations.isEmpty()) {
            return Optional.of(MformPersistenciaXML.toXML(annotatedInstance.persistentAnnotations()));
        }
        return Optional.empty();
    }

    private static MElement toXML(ConfXMLGeneration conf, SInstance instancia) {
        if (instancia instanceof SISimple<?>) {
            SISimple<?> iSimples = (SISimple<?>) instancia;
            String sPersistencia = iSimples.toStringPersistencia();
            if (sPersistencia != null) {
                return conf.createMElementComValor(instancia, sPersistencia);
            } else if (conf.isPersistirNull()) {
                return conf.createMElement(instancia);
            }
            return null;
        } else if (instancia instanceof SIComposite) {
            MElement registro = null;
            for (SInstance filho : ((SIComposite) instancia).getCampos()) {
                MElement xmlFilho = toXML(conf, filho);
                if (xmlFilho != null) {
                    if (registro == null) {
                        registro = conf.createMElement(instancia);
                    }
                    registro.appendChild(xmlFilho);
                }
            }
            return registro;
        } else if (instancia instanceof SList) {
            SList<?> lista = (SList<?>) instancia;
            if (lista.isEmpty()) {
                return null;
            }
            MElement xmlLista = null;
            for (SInstance filho : lista) {
                MElement xmlFilho = toXML(conf, filho);
                if (xmlFilho != null) {
                    if (xmlLista == null) {
                        xmlLista = conf.createMElement(instancia);
                    }
                    xmlLista.appendChild(xmlFilho);
                }
            }
            return xmlLista;
        } else {
            throw new SingularFormException("Instancia da classe " + instancia.getClass().getName() + " não suportada", instancia);
        }
    }

    private static final class ConfXMLGeneration {

        private final MDocument xmlDocument;
        private final PersistenceBuilderXML builder;

        public ConfXMLGeneration(PersistenceBuilderXML builder, MDocument xmlDocument) {
            this.builder = builder;
            this.xmlDocument = xmlDocument;
        }

        public boolean isPersistirNull() {
            return builder.isPersistNull();
        }

        public MElement createMElement(SInstance instancia) {
            return complement(instancia, xmlDocument.createMElement(instancia.getMTipo().getSimpleName()));
        }

        public MElement createMElementComValor(SInstance instancia, String valorPersistencia) {
            return complement(instancia, xmlDocument.createMElementComValor(instancia.getMTipo().getSimpleName(), valorPersistencia));
        }

        private MElement complement(SInstance instancia, MElement element) {
            if (builder.isPersistId() && instancia.getId() != null) {
                element.setAttribute(ATRIBUTO_ID, instancia.getId().toString());
            }
            if (builder.isPersistAttributes()) {
                for (Entry<String, SInstance> atr : instancia.getAtributos().entrySet()) {
                    if (atr.getValue() instanceof SISimple) {
                        String sPersistencia = ((SISimple<?>) atr.getValue()).toStringPersistencia();
                        element.setAttribute(atr.getKey(), sPersistencia);
                    } else {
                        throw new SingularFormException("Não implementada a persitência de atributos compostos: " + atr.getKey(),
                                instancia);
                    }
                }
            }
            return element;
        }
    }
}
