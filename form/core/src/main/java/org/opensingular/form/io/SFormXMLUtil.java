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
import org.opensingular.form.ICompositeType;
import org.opensingular.form.InternalAccess;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstances;
import org.opensingular.form.SType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.basic.AtrXML;
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
import java.util.Optional;
import java.util.Set;

/**
 * Métodos utilitários para converter instancias e anotaçãoes para e de XML.
 *
 * @author Daniel C. Bordin
 */
public final class SFormXMLUtil {

    public static final String ID_ATTRIBUTE      = "id";
    public static final String LAST_ID_ATTRIBUTE = "lastId";

    private SFormXMLUtil() {
    }

    /**
     * Cria uma instância não passível de serialização para do tipo com o
     * conteúdo persistido no XML informado.
     */
    @Nonnull
    public static <T extends SInstance> T fromXML(@Nonnull SType<T> type, @Nullable String xmlString) {
        return fromXMLInterno(type.newInstance(), parseXml(xmlString));
    }

    /**
     * Cria uma instância não passível de serialização para do tipo com o
     * conteúdo persistido no XML informado.
     */
    @Nonnull
    public static <T extends SInstance> T fromXML(@Nonnull SType<T> type, @Nullable MElement xml) {
        return fromXMLInterno(type.newInstance(), xml);
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
        SInstance instance = documentFactory.createInstance(refType, false);
        return (T) fromXMLInterno(instance, xml);
    }

    @Nonnull
    public static <T extends SInstance> void fromXML(@Nonnull T instance, @Nullable String xml) {
        fromXML(instance, xml, false);
    }

    /**
     *
     * @param instance
     * *  {@see SFormXMLUtil#fromXML(? extends SInstance, String)}
     * @param xml
     *  {@see SFormXMLUtil#fromXML(? extends SInstance, String)}
     * @param generateFormIds
     *  if true new ids will be generated for the entire form, used to read an instance from an raw xml not managed
     *  by Singular Form
     * @param <T>
     */
    @Nonnull
    public static <T extends SInstance> void fromXML(@Nonnull T instance, @Nullable String xml, boolean generateFormIds) {
        boolean restoreMode = !generateFormIds;
        fromXMLInterno(instance, parseXml(xml), restoreMode);
    }

    private static <T extends SInstance> T fromXMLInterno(@Nonnull T newInstance, @Nullable MElement xml) {
        return fromXMLInterno(newInstance, xml, true);
    }

    /**
     * Preenche a instância criada com o xml fornecido.
     */
    @Nonnull
    private static <T extends SInstance> T fromXMLInterno(@Nonnull T newInstance, @Nullable MElement xml, @Nonnull boolean restoreMode) {
        Integer lastId = 0;
        if (xml != null) {
            lastId = xml.getInteger("@" + LAST_ID_ATTRIBUTE);
        }

        // Colocar em modo de não geraçao de IDs
        if (restoreMode) {
            newInstance.getDocument().initRestoreMode();
        }
        fromXML(newInstance, xml);

        int maxId = verifyIds(newInstance, new HashSet<>());
        if (lastId == null) {
            newInstance.getDocument().setLastId(maxId);
        } else {
            newInstance.getDocument().setLastId(lastId);
        }
        if (restoreMode) {
            newInstance.getDocument().finishRestoreMode();
        }
        return newInstance;
    }

    private static int verifyIds(@Nonnull SInstance instance, @Nonnull Set<Integer> ids) {
        Integer id = instance.getId();
        if (ids.contains(id)) {
            throw new SingularFormException("A instance tem ID repetido (igual a outra instância) id=" + id, instance);
        }
        if (instance instanceof ICompositeInstance) {
            int max = id;
            for (SInstance child : ((ICompositeInstance) instance).getChildren()) {
                max = Math.max(max, verifyIds(child, ids));
            }
            return max;
        }
        return id;
    }

    private static void fromXML(@Nonnull SInstance instance, @Nullable MElement xml) {
        if (xml == null)
            return; // Não precisa fazer nada
        instance.clearInstance();
        readAttributes(instance, xml);
        if (instance instanceof SISimple) {
            fromXMLSISImple((SISimple<?>) instance, xml);
        } else if (instance instanceof SIComposite) {
            fromXMLSIComposite((SIComposite) instance, xml);
        } else if (instance instanceof SIList) {
            fromXMLSIList((SIList<?>) instance, xml);
        } else {
            throw new SingularFormException(
                    "Conversão não implementando para a classe " + instance.getClass().getName(), instance);
        }
    }

    private static void fromXMLSISImple(@Nonnull SISimple<?> instance, @Nullable MElement xml) {
        if (xml != null) {
            STypeSimple<?, ?> type = instance.getType();
            instance.setValue(type.fromStringPersistence(xml.getTextContent()));
        }
    }

    private static void fromXMLSIList(@Nonnull SIList<?> list, @Nullable MElement xml) {
        if (xml != null) {
            String childrenName = list.getType().getElementsType().getNameSimple();
            for (MElement xmlChild = xml.getPrimeiroFilho(); xmlChild != null; xmlChild = xmlChild.getProximoIrmao()) {
                if (childrenName.equals(xmlChild.getTagName())) {
                    fromXML(list.addNew(), xmlChild);
                } else {
                    InternalAccess.INTERNAL.addUnreadInfo(list, xmlChild);
                }
            }
        }
    }

    private static void fromXMLSIComposite(@Nonnull SIComposite instc, @Nullable MElement xml) {
        if (xml == null) {
            return;
        }
        for (MElement xmlChild = xml.getPrimeiroFilho(); xmlChild != null; xmlChild = xmlChild.getProximoIrmao()) {
            Optional<SInstance> instcField = instc.getFieldOpt(xmlChild.getTagName());
            if (instcField.isPresent()) {
                fromXML(instcField.get(), xmlChild);
            } else {
                InternalAccess.INTERNAL.addUnreadInfo(instc, xmlChild);
            }
        }
    }

    private static void readAttributes(SInstance instance, MElement xml) {
        NamedNodeMap attributes = xml.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr at = (Attr) attributes.item(i);
                if (at.getName().equals(ID_ATTRIBUTE)) {
                    instance.setId(Integer.valueOf(at.getValue()));
                } else if (!at.getName().equals(LAST_ID_ATTRIBUTE)) {
                    InternalAccess.INTERNAL.setAttributeValueSavingForLatter(instance, at.getName(), at.getValue());
                }
            }
        }
    }

    /**
     * Gera uma string XML representando a instância de forma apropriada para persitência permanente (ex: para
     * armazenamento em banco de dados). Já trata escapes de caracteres especiais dentro dos valores.
     *
     * @return Se a instância não conter nenhum valor, então retorna um resultado null no Optional
     */
    @Nonnull
    public static Optional<String> toStringXML(@Nonnull SInstance instance) {
        return toXML(instance).map(MElement::toStringExato);
    }

    /**
     * Gera uma string XML representando a instância de forma apropriada para persitência permanente (ex: para
     * armazenamento em banco de dados). Já trata escapes de caracteres especiais dentro dos valores.
     *
     * @return Se a instância não conter nenhum valor, então retorna um XML com apenas o nome do tipo da instância.
     */
    @Nonnull
    public static String toStringXMLOrEmptyXML(@Nonnull SInstance instance) {
        return toXMLOrEmptyXML(instance).toStringExato();
    }

    /**
     * Gera um XML representando a instância de forma apropriada para persitência permanente (ex: para armazenamento em
     * banco de dados).
     *
     * @return Se a instância não conter nenhum valor, então retorna um resultado null no Optional
     */
    @Nonnull
    public static Optional<MElement> toXML(@Nonnull SInstance instance) {
        return Optional.ofNullable(createDefaultBuilder().toXML(instance));
    }

    /**
     * Gera uma string XML representando a instância de forma apropriada para persitência permanente (ex: para
     * armazenamento em banco de dados).
     *
     * @return Se a instância não conter nenhum valor, então retorna um XML com apenas o nome do tipo da instância.
     */
    @Nonnull
    public static MElement toXMLOrEmptyXML(@Nonnull SInstance instance) {
        return createDefaultBuilder().withReturnNullXML(false).toXML(instance);
    }

    /**
     * Cria uma configuração default para a geração de XML.
     */
    private static PersistenceBuilderXML createDefaultBuilder() {
        return new PersistenceBuilderXML().withPersistNull(false);
    }

    /**
     * Gera uma string XML representando os dados da instância e o atributos de runtime para persistência temporária
     * (provavelemnte temporariamente durante a tela de edição).
     */
    @Nonnull
    public static MElement toXMLPreservingRuntimeEdition(@Nonnull SInstance instance) {
        return new PersistenceBuilderXML().withPersistNull(true).withPersistAttributes(true).withReturnNullXML(false)
                .toXML(instance);
    }

    private static boolean hasKeepNodePredicatedInAnyChildren(@Nonnull SType<?> type) {
        if (type.as(AtrXML::new).isKeepNodePredicateConfigured()) {
            return true;
        }
        if (type instanceof ICompositeType) {
            for (SType<?> field : ((ICompositeType) type).getContainedTypes()) {
                if (!field.isRecursiveReference() && hasKeepNodePredicatedInAnyChildren(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void initSubFieldsIfNeeded(@Nonnull SInstance instance) {
        if (hasKeepNodePredicatedInAnyChildren(instance.getType())) {
            //Forces all sub fields in all sub composites to be created just by walking through then
            SInstances.streamDescendants(instance, true).forEach(si -> si.getId());
        }
    }

    @Nullable
    static MElement toXML(MElement parent, String parentName, @Nonnull SInstance instance,
                          @Nonnull PersistenceBuilderXML builder) {

        MDocument         xmlDocument = (parent == null) ? MDocument.newInstance() : parent.getMDocument();
        ConfXMLGeneration conf        = new ConfXMLGeneration(builder, xmlDocument);

        initSubFieldsIfNeeded(instance);

        MElement xmlResult = toXML(conf, instance);
        if (xmlResult == null) {
            if (builder.isReturnNullXML()) {
                return parent;
            }
            xmlResult = conf.createMElement(instance);
        }
        if (parentName != null) {
            MElement newElement = xmlDocument.createMElement(parentName);
            newElement.addElement(xmlResult);
            xmlResult = newElement;
        }
        if (parent != null) {
            parent.addElement(xmlResult);
            return parent;
        }
        xmlDocument.setRoot(xmlResult);
        if (builder.isPersistId()) {
            xmlResult.setAttribute(LAST_ID_ATTRIBUTE, Integer.toString(instance.getDocument().getLastId()));
        }

        return xmlResult;
    }

    public static MElement parseXml(String xmlString) {
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

    /**
     * Gera um XML representando as anotações se existirem.
     */
    @Nonnull
    public static Optional<String> annotationToXmlString(@Nonnull SInstance instance) {
        return annotationToXml(instance).map(MElement::toStringExato);
    }

    /**
     * Gera um XML representando as anotações se existirem.
     */
    @Nonnull
    public static Optional<MElement> annotationToXml(@Nonnull SInstance instance) {
        return annotationToXml(instance, null);
    }

    /**
     * Gera um XML representando as anotações se existirem.
     */
    @Nonnull
    public static Optional<MElement> annotationToXml(@Nonnull SInstance instance, @Nullable String classifier) {
        return annotationToXml(instance.getDocument(), classifier);
    }

    /**
     * Gera um XML representando as anotações se existirem.
     */
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

    /**
     * Gera o xml para instance e para seus dados interno.
     */
    private static MElement toXML(ConfXMLGeneration conf, SInstance instance) {
        MElement newElement = null;
        if (instance instanceof SISimple<?>) {
            SISimple<?> iSimples     = (SISimple<?>) instance;
            String      sPersistence = iSimples.toStringPersistence();
            if (sPersistence != null) {
                newElement = conf.createMElementWithValue(instance, sPersistence);
            } else if (conf.isPersistNull() || instance.as(AtrXML::new).getKeepNodePredicate().test(instance)) {
                newElement = conf.createMElement(instance);
            }
        } else if (instance instanceof ICompositeInstance) {
            if (instance.as(AtrXML::new).getKeepNodePredicate().test(instance)) {
                newElement = conf.createMElement(instance);
            }
            newElement = toXMLChildren(conf, instance, newElement, ((ICompositeInstance) instance).getChildren());
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
        List<MElement> unreadInfo = InternalAccess.INTERNAL.getUnreadInfo(instance);
        MElement       result     = newElement;
        if (!unreadInfo.isEmpty()) {
            if (result == null) {
                result = conf.createMElement(instance);
            }
            for (MElement extra : unreadInfo) {
                result.copy(extra, null);
            }
        }
        return result;
    }

    private static final class ConfXMLGeneration {

        private final MDocument             xmlDocument;
        private final PersistenceBuilderXML builder;

        public ConfXMLGeneration(PersistenceBuilderXML builder, MDocument xmlDocument) {
            this.builder = builder;
            this.xmlDocument = xmlDocument;
        }

        public boolean isPersistNull() {
            return builder.isPersistNull();
        }

        public MElement createMElement(SInstance instance) {
            return complement(instance, xmlDocument.createMElement(instance.getType().getNameSimple()));
        }

        public MElement createMElementWithValue(SInstance instance, String persistenceValue) {
            return complement(instance, xmlDocument.createMElementWithValue(instance.getType().getNameSimple(), persistenceValue));
        }

        private MElement complement(SInstance instance, MElement element) {
            Integer id = instance.getId();
            if (builder.isPersistId()) {
                element.setAttribute(ID_ATTRIBUTE, id.toString());
            }
            if (builder.isPersistAttributes()) {
                for (SInstance atr : instance.getAttributes()) {
                    String name = atr.getAttributeInstanceInfo().getName();
                    if (atr instanceof SISimple) {
                        String sPersistence = ((SISimple<?>) atr).toStringPersistence();
                        element.setAttribute(name, sPersistence);
                    } else {
                        throw new SingularFormException("Não implementada a persitência de atributos compostos: " + name,
                                instance);
                    }
                }
            }
            return element;
        }
    }
}
