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

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.InternalAccess;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.core.annotation.DocumentAnnotations;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.internal.lib.commons.xml.MDocument;
import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Métodos utilitários para converter instancias e anotaçãoes para e de XML.
 *
 * @author Daniel C. Bordin
 */
public final class SFormXMLUtil {

    public static final String ATRIBUTO_ID = "id";
    public static final String ATRIBUTO_LAST_ID = "lastId";
    private static InternalAccess internalAccess;

    private SFormXMLUtil() {}

    /**
     * Cria uma instância não passível de serialização para do tipo com o
     * conteúdo persistido no XML informado.
     */
    @Nonnull
    public static <T extends SInstance> T fromXML(@Nonnull SType<T> tipo, @Nullable String xmlString) {
        return fromXMLInterno(tipo.newInstance(), parseXml(xmlString));
    }

    /**
     * Cria uma instância não passível de serialização para do tipo com o
     * conteúdo persistido no XML informado.
     */
    @Nonnull
    public static <T extends SInstance> T fromXML(@Nonnull SType<T> tipo, @Nullable MElement xml) {
        return fromXMLInterno(tipo.newInstance(), xml);
    }

    /**
     * Cria uma instância passível de serialização para o tipo referenciado e a
     * factory de documento informada.
     */
    @Nonnull
    public static <T extends SInstance> T fromXML(@Nonnull RefType refType, @Nullable String xmlString,
            @Nonnull SDocumentFactory documentFactory) {
        return fromXML(refType, parseXml(xmlString), documentFactory);
    }

    /**
     * Cria uma instância passível de serialização para o tipo referenciado e a
     * factory de documento informada.
     */
    @Nonnull
    public static <T extends SInstance> T fromXML(@Nonnull RefType refType, @Nullable MElement xml,
            @Nonnull SDocumentFactory documentFactory) {
        SInstance novo = documentFactory.createInstance(refType, false);
        return (T) fromXMLInterno(novo, xml);
    }

    /** Preenche a instância criada com o xml fornecido. */
    @Nonnull
    private static <T extends SInstance> T fromXMLInterno(@Nonnull T novo, @Nullable MElement xml) {
        Integer lastId = 0;
        if (xml != null) {
            lastId = xml.getInteger("@" + ATRIBUTO_LAST_ID);
        }

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

    private static void fromXML(@Nonnull SInstance instance, @Nullable MElement xml) {
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
                    getInternalAccess().addUnreadInfo(instance, xmlChild);
                }
            }
        } else if (instance instanceof SIList) {
            SIList<?> list = (SIList<?>) instance;
            String childrenName = list.getType().getElementsType().getNameSimple();
            for(MElement xmlChild = xml.getPrimeiroFilho(); xmlChild != null; xmlChild = xmlChild.getProximoIrmao()) {
                if(childrenName.equals(xmlChild.getTagName())) {
                    fromXML(list.addNew(), xmlChild);
                } else {
                    getInternalAccess().addUnreadInfo(instance, xmlChild);
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
                    instancia.setId(Integer.valueOf(at.getValue()));
                } else if (!at.getName().equals(ATRIBUTO_LAST_ID)) {
                    getInternalAccess().setAttributeValueSavingForLatter(instancia, at.getName(), at.getValue());
                }
            }
        }
    }

    /**
     * Gera uma string XML representando a instância de forma apropriada para persitência permanente (ex: para
     * armazenamento em banco de dados). Já trata escapes de caracteres especiais dentro dos valores.
     * @return Se a instância não conter nenhum valor, então retorna um resultado null no Optional
     */
    @Nonnull
    public static Optional<String> toStringXML(@Nonnull SInstance instance) {
        return toXML(instance).map(MElement::toStringExato);
    }

    /**
     * Gera uma string XML representando a instância de forma apropriada para persitência permanente (ex: para
     * armazenamento em banco de dados). Já trata escapes de caracteres especiais dentro dos valores.
     * @return Se a instância não conter nenhum valor, então retorna um XML com apenas o nome do tipo da instância.
     */
    @Nonnull
    public static String toStringXMLOrEmptyXML(@Nonnull SInstance instance) {
        return toXMLOrEmptyXML(instance).toStringExato();
    }

    /**
     * Gera um XML representando a instância de forma apropriada para persitência permanente (ex: para armazenamento em
     * banco de dados).
     * @return Se a instância não conter nenhum valor, então retorna um resultado null no Optional
     */
    @Nonnull
    public static Optional<MElement> toXML(@Nonnull SInstance instancia) {
        return Optional.ofNullable(createDefaultBuilder().toXML(instancia));
    }

    /**
     * Gera uma string XML representando a instância de forma apropriada para persitência permanente (ex: para
     * armazenamento em banco de dados).
     * @return Se a instância não conter nenhum valor, então retorna um XML com apenas o nome do tipo da instância.
     */
    @Nonnull
    public static MElement toXMLOrEmptyXML(@Nonnull SInstance instancia) {
        return createDefaultBuilder().withReturnNullXML(false).toXML(instancia);
    }

    /** Cria uma configuração default para a geração de XML. */
    private static PersistenceBuilderXML createDefaultBuilder() {
        return new PersistenceBuilderXML().withPersistNull(false);
    }

    /**
     * Gera uma string XML representando os dados da instância e o atributos de runtime para persistência temporária
     * (provavelemnte temporariamente durante a tela de edição).
     */
    @Nonnull
    public static MElement toXMLPreservingRuntimeEdition(@Nonnull SInstance instancia) {
        return new PersistenceBuilderXML().withPersistNull(true).withPersistAttributes(true).withReturnNullXML(false)
                .toXML(instancia);
    }

    @Nullable
    static MElement toXML(MElement pai, String nomePai, @Nonnull SInstance instancia,
            @Nonnull PersistenceBuilderXML builder) {

        MDocument xmlDocument = (pai == null) ? MDocument.newInstance() : pai.getMDocument();
        ConfXMLGeneration conf = new ConfXMLGeneration(builder, xmlDocument);

        MElement xmlResultado = toXML(conf, instancia);
        if (xmlResultado == null) {
            if (builder.isReturnNullXML()) {
                return pai;
            }
            xmlResultado = conf.createMElement(instancia);
        }
        if (nomePai != null) {
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

    static MElement parseXml(String xmlString) {
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
     * @param xmlString Se nulo ou em branco, não faz carga
     */
    @Deprecated
    public static void annotationLoadFromXml(SInstance instance, String xmlString) {
        annotationLoadFromXml(instance.getDocument(), xmlString);
    }

    /**
     * Carrega na instance informada as anotação contidas no xml, fazendo
     * parser do mesmo antes.
     *
     * @param xmlAnnotations Se nulo, não faz carga
     */
    @Deprecated
    public static void annotationLoadFromXml(SInstance instance, MElement xmlAnnotations) {
        annotationLoadFromXml(instance.getDocument(), xmlAnnotations);
    }

    /**
     * Carrega na instance informada as anotação contidas no xml, fazendo
     * parser do mesmo antes.
     *
     * @param xmlString Se nulo ou em branco, não faz carga
     */
    public static void annotationLoadFromXml(@Nonnull SDocument document, @Nullable String xmlString) {
        annotationLoadFromXml(document, parseXml(xmlString));
    }

    /**
     * Carrega na instance informada as anotação contidas no xml, fazendo
     * parser do mesmo antes.
     *
     * @param xmlAnnotations Se nulo, não faz carga
     */
    public static void annotationLoadFromXml(@Nonnull SDocument document, @Nullable MElement xmlAnnotations) {
        if (xmlAnnotations == null) {
            return;
        }
        SIList<SIAnnotation> iAnnotations = DocumentAnnotations.newAnnotationList(document, false);
        fromXMLInterno(iAnnotations, xmlAnnotations);
        document.getDocumentAnnotations().loadAnnotations(iAnnotations);
    }

    /** Gera um XML representando as anotações se existirem. */
    @Nonnull
    public static Optional<String> annotationToXmlString(@Nonnull SInstance instance) {
        return annotationToXml(instance).map(MElement::toStringExato);
    }

    /** Gera um XML representando as anotações se existirem. */
    @Nonnull
    public static Optional<MElement> annotationToXml(@Nonnull SInstance instance) {
        return annotationToXml(instance, null);
    }

    /** Gera um XML representando as anotações se existirem. */
    @Nonnull
    public static Optional<MElement> annotationToXml(@Nonnull SInstance instance, @Nullable String classifier) {
        return annotationToXml(instance.getDocument(), classifier);
    }

    /** Gera um XML representando as anotações se existirem. */
    @Nonnull
    public static Optional<MElement> annotationToXml(@Nonnull SDocument document, @Nullable String classifier) {
        DocumentAnnotations documentAnnotations = document.getDocumentAnnotations();
        if (documentAnnotations.hasAnnotations()) {
            if (classifier != null) {
                return toXML(documentAnnotations.persistentAnnotationsClassified(classifier));
            } else {
                return toXML(documentAnnotations.getAnnotations());
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
            newElement = toXMLChildren(conf, instance, newElement, ((SIComposite) instance).getFields());
        } else if (instance instanceof SIList) {
            newElement = toXMLChildren(conf, instance, newElement, ((SIList<?>) instance).getValues());
        } else {
            throw new SingularFormException("Instancia da classe " + instance.getClass().getName() + " não suportada",
                    instance);
        }
        //Verifica se há alguma informação lida anteriormente que deva ser grava novamente
        newElement = toXMLOldElementWithoutType(conf, instance, newElement);

        return newElement;
    }

    /**
     * Gera no XML a os elemento filhos (senão existirem).
     */
    private static MElement toXMLChildren(ConfXMLGeneration conf, SInstance instance, MElement newElement,
            List<? extends SInstance> children) {
        MElement result = newElement;
        for (SInstance child : children) {
            MElement xmlChild = toXML(conf, child);
            if (xmlChild != null) {
                if (result == null) {
                    result = conf.createMElement(instance);
                }
                result.appendChild(xmlChild);
            }
        }
        return result;
    }

    /**
     * Escreve para o XML os elemento que foram lidos do XML anterior e foram preservados apesar de não terem um type
     * correspondente. Ou seja, mantêm campo "fantasmas" entre leituras e gravações.
     */
    private static MElement toXMLOldElementWithoutType(ConfXMLGeneration conf, SInstance instance,
            MElement newElement) {
        List<MElement> unreadInfo = getInternalAccess().getUnreadInfo(instance);
        MElement result = newElement;
        if (! unreadInfo.isEmpty()) {
            if (result == null) {
                result = conf.createMElement(instance);
            }
            for(MElement extra : unreadInfo) {
                result.copy(extra, null);
            }
        }
        return result;
    }

    /** Garante a carga do objeto a chamada internas da API. */
    @Nonnull
    private static final InternalAccess getInternalAccess() {
        if (internalAccess == null) {
            InternalAccess.load();
            return Objects.requireNonNull(internalAccess);
        }
        return internalAccess;
    }

    /** Recebe o objeto que viabiliza executar chamadas internas da API (chamadas a métodos não públicos). */
    public static final void setInternalAccess(@Nonnull InternalAccess internalAccess) {
        SFormXMLUtil.internalAccess = internalAccess;
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
            Integer id = instancia.getId();
            if (builder.isPersistId()) {
                element.setAttribute(ATRIBUTO_ID, id.toString());
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
