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

package org.opensingular.form.io;

import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.internal.xml.MDocument;
import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.internal.xml.MParser;
import org.opensingular.form.type.core.annotation.STypeAnnotationList;
import org.opensingular.form.document.RefType;
import org.opensingular.form.type.core.annotation.AtrAnnotation;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            throw new SingularFormException("A instance tem ID repetido (igual a outra instância) id=" + id, instancia);
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

    private static void fromXML(SInstance instance, MElement xml) {
        if (xml == null)
            return; // Não precisa fazer nada
        instance.clearInstance();
        lerAtributos(instance, xml);
        if (instance instanceof SISimple) {
            SISimple<?> instanceSimple = (SISimple<?>) instance;
            STypeSimple<?, ?> type = instanceSimple.getType();
            instance.setValue(type.fromStringPersistence(xml.getTextContent()));
        } else if (instance instanceof SIComposite) {
            SIComposite instc = (SIComposite) instance;
            for(MElement xmlChild = xml.getPrimeiroFilho(); xmlChild != null; xmlChild = xmlChild.getProximoIrmao()) {
                Optional<SInstance> instcField = instc.getFieldOpt(xmlChild.getTagName());
                if (instcField.isPresent()) {
                    fromXML(instcField.get(), xmlChild);
                } else {
                    InternalAccess.internal(instance).addUnreadInfo(xmlChild);
                }
            }
        } else if (instance instanceof SIList) {
            SIList<?> list = (SIList<?>) instance;
            String childrenName = list.getType().getElementsType().getNameSimple();
            for(MElement xmlChild = xml.getPrimeiroFilho(); xmlChild != null; xmlChild = xmlChild.getProximoIrmao()) {
                if(childrenName.equals(xmlChild.getTagName())) {
                    fromXML(list.addNew(), xmlChild);
                } else {
                    InternalAccess.internal(instance).addUnreadInfo(xmlChild);
                }
            }
        } else {
            throw new SingularFormException(
                    "Conversão não implementando para a classe " + instance.getClass().getName(), instance);
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
     * Carrega na instance informada as anotação contidas no xml, fazendo
     * parser do mesmo antes.
     *
     * @param xmlString
     *            Se nulo ou em branco, não faz carga
     */
    public static void annotationLoadFromXml(SInstance instance, String xmlString) {
        annotationLoadFromXml(instance, parseXml(xmlString));
    }

    /**
     * Carrega na instance informada as anotação contidas no xml, fazendo
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
        return annotationToXml(instance).map(MElement::toStringExato);
    }

    /** Gera um XML representando as anotações se existirem. */
    public static Optional<MElement> annotationToXml(SInstance instance) {
        return annotationToXml(instance, null);
    }

    /** Gera um XML representando as anotações se existirem. */
    public static Optional<MElement> annotationToXml(SInstance instance, String classifier) {
        AtrAnnotation annotatedInstance = instance.asAtrAnnotation();
        if (annotatedInstance.hasAnnotation()) {
            if (classifier != null) {
                return Optional.of(MformPersistenciaXML.toXML(annotatedInstance.persistentAnnotationsClassified(classifier)));
            } else {
                return Optional.of((MformPersistenciaXML.toXML(annotatedInstance.persistentAnnotations())));
            }
        }
        return Optional.empty();
    }

    /** Gera o xml para instance e para seus dados interno. */
    private static MElement toXML(ConfXMLGeneration conf, SInstance instance) {
        MElement newElement = null;
        if (instance instanceof SISimple<?>) {
            SISimple<?> iSimples = (SISimple<?>) instance;
            String sPersistence = iSimples.toStringPersistence();
            if (sPersistence != null) {
                newElement = conf.createMElementComValor(instance, sPersistence);
            } else if (conf.isPersistirNull()) {
                newElement = conf.createMElement(instance);
            }
        } else if (instance instanceof SIComposite) {
            for (SInstance child : ((SIComposite) instance).getFields()) {
                MElement xmlChild = toXML(conf, child);
                if (xmlChild != null) {
                    if (newElement == null) {
                        newElement = conf.createMElement(instance);
                    }
                    newElement.appendChild(xmlChild);
                }
            }
        } else if (instance instanceof SIList) {
            for (SInstance child : (SIList<?>) instance) {
                MElement xmlChild = toXML(conf, child);
                if (xmlChild != null) {
                    if (newElement == null) {
                        newElement = conf.createMElement(instance);
                    }
                    newElement.appendChild(xmlChild);
                }
            }
        } else {
            throw new SingularFormException("Instancia da classe " + instance.getClass().getName() + " não suportada",
                    instance);
        }
        //Verifica se há alguma informação lida anteriormente que deva ser grava novamente
        List<MElement> unreadInfo = InternalAccess.internal(instance).getUnreadInfo();
        if (! unreadInfo.isEmpty()) {
            if (newElement == null) {
                newElement = conf.createMElement(instance);
            }
            for(MElement extra : unreadInfo) {
                newElement.copy(extra, null);
            }
        }
        return newElement;
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
