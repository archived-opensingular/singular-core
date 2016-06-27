/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.io;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import br.net.mirante.singular.form.ICompositeInstance;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SISimple;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.internal.xml.MDocument;
import br.net.mirante.singular.form.internal.xml.MElement;
import br.net.mirante.singular.form.internal.xml.MParser;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.type.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.type.core.annotation.STypeAnnotationList;

/**
 * Métodos utilitários para converter instancias e anotaçãoes para e de XML.
 *
 * @author Daniel C. Bordin
 */
public class MformPersistenciaXML {

    public static final String ATRIBUTO_ID = "id";
    public static final String ATRIBUTO_LAST_ID = "lastId";

    /**
     * Cria uma instância não passível de serialização para do tipo com o
     * conteúdo persistido no XML informado.
     */
    public static <T extends SInstance> T fromXML(SType<T> tipo, String xmlString) {
        return fromXMLInterno(tipo.newInstance(), parseXml(xmlString));
    }

    /**
     * Cria uma instância não passível de serialização para do tipo com o
     * conteúdo persistido no XML informado.
     */
    public static <T extends SInstance> T fromXML(SType<T> tipo, MElement xml) {
        return fromXMLInterno(tipo.newInstance(), xml);
    }

    /**
     * Cria uma instância passível de serialização para o tipo referenciado e a
     * factory de documento informada.
     */
    public static <T extends SInstance> T fromXML(RefType refType, String xmlString, SDocumentFactory documentFactory) {
        return fromXML(refType, parseXml(xmlString), documentFactory);
    }

    /**
     * Cria uma instância passível de serialização para o tipo referenciado e a
     * factory de documento informada.
     */
    public static <T extends SInstance> T fromXML(RefType refType, MElement xml, SDocumentFactory documentFactory) {
        SInstance novo = documentFactory.createInstance(refType, false);
        return (T) fromXMLInterno(novo, xml);
    }

    /** Preenche a instância criada com o xml fornecido. */
    private static <T extends SInstance> T fromXMLInterno(T novo, MElement xml) {
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
        if (id == null) {
            throw new SingularFormException("O ID da instância está null", instancia);
        } else if (ids.contains(id)) {
            throw new SingularFormException("A instancia tem ID repetido (igual a outra instância) id=" + id, instancia);
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
        instancia.clearInstance();
        lerAtributos(instancia, xml);
        if (instancia instanceof SISimple) {
            SISimple<?>       instanciaS = (SISimple<?>) instancia;
            STypeSimple<?, ?> tipos      = instanciaS.getType();
            instancia.setValue(tipos.fromStringPersistence(xml.getTextContent()));
        } else if (instancia instanceof SIComposite) {
            SIComposite instc = (SIComposite) instancia;
            for (SType<?> campo : instc.getType().getFields()) {
                MElement xmlFilho = xml.getElement(campo.getNameSimple());
                if (xmlFilho != null) {
                    fromXML(instc.getField(campo.getNameSimple()), xmlFilho);
                }
            }
        } else if (instancia instanceof SIList) {
            SIList<?> lista = (SIList<?>) instancia;
            String nomeFilhos = lista.getType().getElementsType().getNameSimple();
            for (MElement xmlFilho : xml.getElements(nomeFilhos)) {
                SInstance filho = lista.addNew();
                fromXML(filho, xmlFilho);
            }
        } else {
            throw new SingularFormException(
                    "Conversão não implementando para a classe " + instancia.getClass().getName(), instancia);
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
                    instancia.setAttributeValue(at.getName(), at.getValue());
                }
            }
        }
    }

    /**
     * Gera uma string XML representando a instância de forma apropriada para
     * persitência permanente (ex: para armazenamento em banco de dados). Já
     * trata escapes de caracteres especiais dentro dos valores.
     */
    public static Optional<String> toStringXML(SInstance instancia) {
        MElement xml = toXML(instancia);
        return xml == null ? Optional.empty() : Optional.of(xml.toStringExato());
    }

    public static MElement toXML(SInstance instancia) {
        return new PersistenceBuilderXML().withPersistNull(false).toXML(instancia);
    }

    /**
     * Gera uma string XML representando os dados da instância e o atributos de
     * runtime para persistência temporária (provavelemnte temporariamente
     * durante a tela de edição).
     */
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

    final static MElement parseXml(String xmlString) {
        try {
            if (StringUtils.isBlank(xmlString)) {
                return null;
            }
            return MParser.parse(xmlString);
        } catch (Exception e) {
            throw new SingularFormException("Erro lendo xml (parse)", e);
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

        SDocument document      = instance.getDocument();
        RefType   refAnnotation = document.getRootRefType().get().createSubReference(STypeAnnotationList.class);
        SIList<SIAnnotation> iAnnotations = (SIList<SIAnnotation>) MformPersistenciaXML.fromXML(refAnnotation, xmlAnnotations,
                document.getDocumentFactoryRef().get());

        instance.asAtrAnnotation().loadAnnotations(iAnnotations);
    }

    /** Gera um XML representando as anotações se existirem. */
    public static Optional<String> annotationToXmlString(SInstance instance) {
        return annotationToXml(instance).map(xml -> xml.toStringExato());
    }

    /** Gera um XML representando as anotações se existirem. */
    public static Optional<MElement> annotationToXml(SInstance instance) {
        AtrAnnotation      annotatedInstance = instance.asAtrAnnotation();
        List<SIAnnotation> allAnnotations    = annotatedInstance.allAnnotations();
        if (!allAnnotations.isEmpty()) {
            return Optional.of(MformPersistenciaXML.toXML(annotatedInstance.persistentAnnotations()));
        }
        return Optional.empty();
    }

    private static MElement toXML(ConfXMLGeneration conf, SInstance instancia) {
        if (instancia instanceof SISimple<?>) {
            SISimple<?> iSimples = (SISimple<?>) instancia;
            String sPersistencia = iSimples.toStringPersistence();
            if (sPersistencia != null) {
                return conf.createMElementComValor(instancia, sPersistencia);
            } else if (conf.isPersistirNull()) {
                return conf.createMElement(instancia);
            }
            return null;
        } else if (instancia instanceof SIComposite) {
            MElement registro = null;
            for (SInstance filho : ((SIComposite) instancia).getFields()) {
                MElement xmlFilho = toXML(conf, filho);
                if (xmlFilho != null) {
                    if (registro == null) {
                        registro = conf.createMElement(instancia);
                    }
                    registro.appendChild(xmlFilho);
                }
            }
            return registro;
        } else if (instancia instanceof SIList) {
            SIList<?> lista = (SIList<?>) instancia;
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
            return complement(instancia, xmlDocument.createMElement(instancia.getType().getNameSimple()));
        }

        public MElement createMElementComValor(SInstance instancia, String valorPersistencia) {
            return complement(instancia, xmlDocument.createMElementComValor(instancia.getType().getNameSimple(), valorPersistencia));
        }

        private MElement complement(SInstance instancia, MElement element) {
            if (builder.isPersistId() && instancia.getId() != null) {
                element.setAttribute(ATRIBUTO_ID, instancia.getId().toString());
            }
            if (builder.isPersistAttributes()) {
                for (SInstance atr : instancia.getAttributes()) {
                    String name = atr.getAttributeInstanceInfo().getName();
                    if (atr instanceof SISimple) {
                        String sPersistence = ((SISimple<?>) atr).toStringPersistence();
                        element.setAttribute(name, sPersistence);
                    } else {
                        throw new SingularFormException("Não implementada a persitência de atributos compostos: " + name,
                                instancia);
                    }
                }
            }
            return element;
        }
    }
}
