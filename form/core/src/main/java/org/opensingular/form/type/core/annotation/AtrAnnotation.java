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

package org.opensingular.form.type.core.annotation;

import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.document.RefType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Decorates an Instance as annotated enabling access to its anotations.
 *
 * @author Fabricio Buzeto
 */
public class AtrAnnotation extends STranslatorForAttribute {

    public static enum DefaultAnnotationClassifier implements AnnotationClassifier {
        DEFAULT_ANNOTATION;
    }

    public AtrAnnotation() {
    }

    public AtrAnnotation(SAttributeEnabled alvo) {
        super(alvo);
    }

    /**
     * Marks this type as annotated
     *
     * @return this
     */
    public <T extends Enum & AnnotationClassifier> AtrAnnotation setAnnotated() {
        setAnnotated(DefaultAnnotationClassifier.DEFAULT_ANNOTATION);
        return this;
    }

    /**
     * Sets the label for this annotation
     *
     * @return this
     */
    public AtrAnnotation label(String label) {
        setAttributeValue(SPackageBasic.ATR_ANNOTATION_LABEL, label);
        return this;
    }

    /**
     * @return true if type is annotated
     */
    public boolean isAnnotated() {
        List<String> list = getAttributeValue(SPackageBasic.ATR_ANNOTATED);
        return list != null && !list.isEmpty();
    }

    /**
     * MArca o tipo como anotado definindo o tipo da anotação
     *
     * @return this
     */
    public <T extends Enum & AnnotationClassifier> AtrAnnotation setAnnotated(T... classifiersParam) {
        List<String> classifiers = getAttributeValue(SPackageBasic.ATR_ANNOTATED);
        if (classifiers == null) {
            classifiers = new ArrayList<>();
        }
        for (T classifier : classifiersParam) {
            if (!classifiers.contains(classifier.name())) {
                classifiers.add(classifier.name());
            }
        }
        setAttributeValue(SPackageBasic.ATR_ANNOTATED, classifiers);
        return this;
    }

    /**
     * @return the label set, if any
     */
    public String label() {
        return getAttributeValue(SPackageBasic.ATR_ANNOTATION_LABEL);
    }

    /**
     * @param valor Text value of the annotation.
     * @return this
     */
    public AtrAnnotation text(String valor) {
        annotation().setText(valor);
        return this;
    }

    /**
     * @return Text value of the annotation.
     */
    public String text() {
        return annotation().getText();
    }

    /**
     * @param isApproved Informs if the annotation approves the content of the instance or not.
     * @return this
     */
    public AtrAnnotation approved(Boolean isApproved) {
        annotation().setApproved(isApproved);
        return this;
    }

    /**
     * @return Informs if the annotation approves the content of the instance or not.
     */
    public Boolean approved() {
        return annotation().getApproved();
    }

    /**
     * @return Current annotation if this instance, if none is present one is created.
     */
    public <T extends Enum<T> & AnnotationClassifier> SIAnnotation annotation() {
        return annotation(DefaultAnnotationClassifier.DEFAULT_ANNOTATION);
    }

    public <T extends Enum<T> & AnnotationClassifier> SIAnnotation annotation(T classifier) {
        createAttributeIfNeeded(classifier);
        return target().getDocument().annotation(target().getId(), classifier);
    }

    /**
     * @return True if this SIinstance is an annotated type and if the anotation has any value.
     */
    public boolean hasAnnotation() {
        List<SIAnnotation> atrList = target().getDocument().annotationsAnyClassifier(target().getId());
        return atrList != null && hasValue(atrList);
    }

    private boolean hasValue(List<SIAnnotation> atrList) {
        boolean truth = false;
        for (SIAnnotation atr : atrList) {
            truth |= StringUtils.isNotBlank(atr.getText()) || atr.getApproved() != null;
        }
        return truth;
    }

    private <T extends Enum<T> & AnnotationClassifier> void createAttributeIfNeeded(T classifier) {
        if (target().getDocument().annotation(target().getId(), classifier) == null) {
            newAnnotation(classifier);
        }
    }

    private <T extends Enum<T> & AnnotationClassifier> SIAnnotation newAnnotation(T classifier) {
        isValidClassifier(classifier);
        SIAnnotation a = target().getDocument().newAnnotation();
        a.setTarget(target());
        a.setClassifier(classifier.name());
        return a;
    }

    private <T extends Enum<T> & AnnotationClassifier> void isValidClassifier(T classifier) {
        List<String> classifiers = getAttributeValue(SPackageBasic.ATR_ANNOTATED);
        if (classifiers == null || !classifiers.contains(classifier.name())) {
            if (!DefaultAnnotationClassifier.DEFAULT_ANNOTATION.equals(classifier)) {
                throw new SingularFormException(
                        String.format(
                                "Classificador de anotação desconhecido para o tipo: %s. Certifique-se que a tipo foi marcado como setAnnotated(%s) ",
                                ((SInstance) this.getTarget()).getType().getName(), classifier.name()));

            }

        }
    }

    /**
     * @return All annotations on this instance and its children.
     */
    public List<SIAnnotation> allAnnotations() {
        SIList<SIAnnotation> sList = persistentAnnotations();
        if (sList == null) return newArrayList();
        return sList.getValues();
    }

    /**
     * Loads a collection of annotations onte this instance and its children.
     * The <code>targetId</code> field of the annotation denotes which field that annotation
     * is referring to.
     * Se a intenção é recarregar as anotações é preciso chamar o método {@link AtrAnnotation#clear()} antes
     *
     * @param annotations to be loaded into the instance.
     */
    public void loadAnnotations(SIList<SIAnnotation> annotations) {
        SDocument document = target().getDocument();
        Map<Integer,SInstance> instancesById = new HashMap<>();
        SInstances.streamDescendants(document.getRoot(), true).forEach(i -> instancesById.put(i.getId(),i));

        for(SIAnnotation annotation : annotations) {
            SIAnnotation newAnnotation = document.newAnnotation();
            Value.copyValues(annotation, newAnnotation);
            correctReference(newAnnotation, document, instancesById);
        }
    }

    /**
     * Tenta recria a instancia de acordo com o path original caso a anotação aponte para uma instância que não exista.
     */
    private void correctReference(SIAnnotation newAnnotation, SDocument document, Map<Integer, SInstance> instancesById) {
        if(! instancesById.containsKey(newAnnotation.getTargetId()) && newAnnotation.getTargetPath() != null) {
            try {
                String[] path = StringUtils.split(newAnnotation.getTargetPath(), '/');
                SInstance instance = findByXPathAndId(document, instancesById, path, path.length - 1);
                if (instance != null) {
                    newAnnotation.setTarget(instance);
                }
            } catch (Exception e) {
                throw new SingularFormException("Erro lendo path da anotação: " + newAnnotation.getTargetPath(), e);
            }
        }
    }

    /**
     * Tentar localizar ou recriar a instancia com base no path para o qual a instância apontava. Pode ser que a
     * instância não foi salva pois o conteudo estava vazio, mas a anotação continua sendo valida assim mesmo.
     * Espera trabalhar em cima de um path no formato "order[@id=1]/address[@id=4]/street[@id=5]".
     * @return Retorna null senão for possível localizar.
     */
    private SInstance findByXPathAndId(SDocument document, Map<Integer, SInstance> instancesById, String[] path, int index) {
        int pos = path[index].lastIndexOf('=');
        if (pos <= 0) {
            throw new SingularFormException("Trecho path inválido: '" + path[index] + "'");
        }
        Integer id = Integer.valueOf(path[index].substring(pos+1,path[index].length()-1));
        SInstance instance = instancesById.get(id);
        if (instance != null) {
            return instance;
        }
        pos = path[index].indexOf('[');
        if (pos <= 0) {
            throw new SingularFormException("Trecho path inválido: '" + path[index] + "'");
        }
        String name = path[index].substring(0, pos);

        if (index == 0) {
            if(document.getRoot().getName().equals(name)) {
                return document.getRoot();
            }
        } else {
            SInstance parent = findByXPathAndId(document, instancesById, path, index-1);
            if (parent != null && parent instanceof SIComposite) {
                return ((SIComposite) parent).getField(name);
            }
        }
        return null;
    }

    /**
     * @return A ready to persist object containing all annotations from this instance and its children.
     */
    public SIList<SIAnnotation> persistentAnnotationsClassified(String classifier) {
        return persistentAnnotationsClassified().get(classifier);
    }

    /**
     * @return A ready to persist object containing all annotations from this instance and its children
     * mapped by its classifier
     */
    @SuppressWarnings("unchecked")
    public Map<String, SIList<SIAnnotation>> persistentAnnotationsClassified() {
        Map<String, SIList<SIAnnotation>> classifiedAnnotations = new HashMap<>();
        SIList<SIAnnotation> annotationsSIList = ((SInstance) getTarget()).getDocument().annotations();
        if (annotationsSIList != null) {
            Iterator<SIAnnotation> it = annotationsSIList.iterator();
            while (it.hasNext()) {
                SIAnnotation annotation = it.next();
                SIList<SIAnnotation> list = classifiedAnnotations.get(annotation.getClassifier());
                if (list == null) {
                    list = newAnnotationListFromExisting(annotationsSIList);
                    classifiedAnnotations.put(annotation.getClassifier(), list);
                }
                list.addNew(a -> Value.hydrate(a, Value.dehydrate(annotation)));
            }
        }
        return classifiedAnnotations;
    }

    public SIList<SIAnnotation> persistentAnnotations() {
        return ((SInstance) getTarget()).getDocument().annotations();
    }

    /**
     * Cria uma nova lista de anotações utilizando as configurações de registry e document
     * da lista passada por parâmetro.
     * Essa método tem por objetivo evitar que a nova lista criada fique sem os serviços
     * locais e service registry já configurados na lista original
     *
     * @param annotationList
     * @return
     */
    private SIList<SIAnnotation> newAnnotationListFromExisting(SIList<SIAnnotation> annotationList) {
        RefType refTypeAnnotation = annotationList.getDocument().getRootRefType().get().createSubReference(STypeAnnotationList.class);
        if (annotationList.getDocument().getDocumentFactoryRef() != null) {
            return (SIList<SIAnnotation>) annotationList.getDocument().getDocumentFactoryRef().get().createInstance(refTypeAnnotation);
        }
        return (SIList<SIAnnotation>) SDocumentFactory.empty().createInstance(refTypeAnnotation);
    }


    private SIList<SIAnnotation> newAnnotationList() {
        return (SIList<SIAnnotation>) annotationListType().newInstance();
    }

    private STypeAnnotationList annotationListType() {
        return dictionary().getType(STypeAnnotationList.class);
    }

    private SDictionary dictionary() {
        return target().getType().getDictionary();
    }

    private SInstance target() {
        return (SInstance) getTarget();
    }

    /**
     * Limpa todas a anotações no documento atual
     */
    public void clear() {
        Optional.ofNullable(persistentAnnotations()).ifPresent(SIList::clearInstance);
    }

    public boolean hasAnnotationOnTree() {
        if (hasAnnotation()) return true;
        if (target() instanceof SIComposite) {
            return hasAnnotationsOnChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnnotationsOnChildren(SIComposite parent) {
        for (SInstance si : parent.getAllFields()) {
            if (si.asAtrAnnotation().hasAnnotationOnTree()) return true;
        }
        return false;
    }

    public boolean hasAnyRefusal() {
        if (hasAnnotation() && !annotation().getApproved()) {
            return true;
        }
        if (target() instanceof SIComposite) {
            return hasAnyRefusalOnChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnyRefusalOnChildren(SIComposite parent) {
        for (SInstance si : parent.getAllFields()) {
            if (si.asAtrAnnotation().hasAnyRefusal()) return true;
        }
        return false;
    }

    public boolean isOrHasAnnotatedChild() {
        if (isAnnotated()) return true;
        if (target() instanceof SIComposite) {
            return hasAnnotatedChildren((SIComposite) target());
        }
        return false;
    }

    private boolean hasAnnotatedChildren(SIComposite parent) {
        for (SInstance si : parent.getAllFields()) {
            if (si.asAtrAnnotation().isOrHasAnnotatedChild()) return true;
        }
        return false;
    }
}
