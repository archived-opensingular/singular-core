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
package org.opensingular.form.service;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.form.persistence.AnnotationKey;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormKeyLong;
import org.opensingular.form.persistence.FormKeyManager;
import org.opensingular.form.persistence.SingularFormPersistenceException;
import org.opensingular.form.persistence.dao.FormAnnotationDAO;
import org.opensingular.form.persistence.dao.FormAnnotationVersionDAO;
import org.opensingular.form.persistence.dao.FormDAO;
import org.opensingular.form.persistence.dao.FormTypeDAO;
import org.opensingular.form.persistence.dao.FormVersionDAO;
import org.opensingular.form.persistence.entity.FormAnnotationEntity;
import org.opensingular.form.persistence.entity.FormAnnotationPK;
import org.opensingular.form.persistence.entity.FormAnnotationVersionEntity;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormTypeEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.annotation.DocumentAnnotations;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.lib.commons.lambda.IConsumer;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class FormService implements IFormService {

    private final static boolean KEEP_ANNOTATIONS = true;

    private final FormKeyManager<? extends FormKey> formKeyManager;

    @Inject
    private FormDAO formDAO;

    @Inject
    private FormVersionDAO formVersionDAO;

    @Inject
    private FormAnnotationDAO formAnnotationDAO;

    @Inject
    private FormAnnotationVersionDAO formAnnotationVersionDAO;

    @Inject
    private FormTypeDAO formTypeDAO;

    @Inject
    private FormFieldService formFieldService;

    public FormService() {
        this.formKeyManager = new FormKeyManager<>(FormKeyLong.class, e -> addInfo(e));
    }

    /** Retornar o manipulador de chave usado por essa implementação para ler e converte FormKey. */
    @Nonnull
    private final FormKeyManager<FormKey> getFormKeyManager() {
        return (FormKeyManager<FormKey>) formKeyManager;
    }

    @Nonnull
    public FormKey keyFromObject(@Nonnull Object objectValueToBeConverted) {
        return formKeyManager.keyFromObject(objectValueToBeConverted);
    }

    @Override
    public void update(@Nonnull SInstance instance, Integer inclusionActor) {
        FormKey key = getFormKeyManager().readFormKeyOrException(instance);
        updateInternal(loadFormEntity(key), instance, inclusionActor);
    }

    @Override
    @Nonnull
    public FormKey insertOrUpdate(@Nonnull SInstance instance, Integer inclusionActor) {
        Optional<FormKey> key = getFormKeyManager().readFormKeyOptional(instance);
        FormKey updatedKey;
        if (key.isPresent()) {
            updateInternal(loadFormEntity(key.get()), instance, inclusionActor);
            updatedKey = key.get();
        } else {
            updatedKey = insertImpl(instance, inclusionActor);
        }

        FormEntity formEntity = loadFormEntity(updatedKey);
        formFieldService.saveFields(instance, formEntity.getFormType(), formEntity.getCurrentFormVersionEntity());

        return updatedKey;
    }

    @Override
    @Nonnull
    public FormKey insert(@Nonnull SInstance instance, Integer inclusionActor) {
        if (instance == null) {
            throw addInfo(new SingularFormPersistenceException("O parâmetro instance está null")).add(this);
        }

        FormKey formKey = insertImpl(instance, inclusionActor);
        FormEntity formEntity = loadFormEntity(formKey);

        formFieldService.saveFields(instance, formEntity.getFormType(), formEntity.getCurrentFormVersionEntity());
        return formKey;
    }

    @Nonnull
    private FormKey insertImpl(@Nonnull SInstance instance, Integer inclusionActor) {
        FormKey key = insertInternal(instance, inclusionActor);
        getFormKeyManager().validKeyOrException(key, instance,
                "Era esperado que o insert interno gerasse uma FormKey, mas retornou null");
        FormKey.set(instance, key);
        return key;
    }

    @Override
    public final boolean isPersistent(@Nonnull SInstance instance) {
        return getFormKeyManager().isPersistent(instance);
    }

    /**
     * Método chamado para adicionar informção do serviço de persistência à exception. Pode ser ser sobreescito para
     * acrescimo de maiores informações.
     */
    @Nonnull
    private SingularFormPersistenceException addInfo(@Nonnull SingularFormPersistenceException exception) {
        exception.add("persitence", toString());
        return exception;
    }

    @Nonnull
    private SInstance internalLoadSInstance(@Nonnull FormKey key, @Nonnull RefType refType, @Nonnull SDocumentFactory documentFactory,
                                            @Nonnull FormVersionEntity formVersionEntity) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(refType);
        Objects.requireNonNull(documentFactory);
        Objects.requireNonNull(formVersionEntity);

        final SInstance instance     = SFormXMLUtil.fromXML(refType, formVersionEntity.getXml(), documentFactory);
        final IConsumer loadListener = instance.getAttributeValue(SPackageBasic.ATR_LOAD_LISTENER);

        loadCurrentXmlAnnotationOrEmpty(instance.getDocument(), formVersionEntity);
        FormKey.set(instance, key);

        if (loadListener != null) {
            loadListener.accept(instance);
        }

        return instance;
    }

    @Override
    @Nonnull
    public SInstance newTransientSInstance(@Nonnull FormKey key, @Nonnull RefType refType, @Nonnull SDocumentFactory documentFactory) {
        final SInstance instance = loadSInstance(key, refType, documentFactory);
        FormKey.set(instance, null);
        return instance;
    }

    @Override
    @Nonnull
    public SInstance newTransientSInstance(@Nonnull FormKey key, @Nonnull RefType refType, @Nonnull SDocumentFactory documentFactory, @Nonnull Long versionId) {
        SInstance instance = loadSInstance(key, refType, documentFactory, versionId);
        FormKey.set(instance, null);
        return instance;
    }

    @Override
    @Nonnull
    public SInstance newTransientSInstance(@Nonnull FormKey key, @Nonnull RefType refType, @Nonnull SDocumentFactory documentFactory, boolean keepAnnotations) {
        SInstance instance = newTransientSInstance(key, refType, documentFactory);
        instance.getDocument().getDocumentAnnotations().clear();
        return instance;
    }

    @Override
    @Nonnull
    public SInstance newTransientSInstance(@Nonnull FormKey key, @Nonnull RefType refType, @Nonnull SDocumentFactory documentFactory, @Nonnull Long versionId, boolean keepAnnotations) {
        SInstance instance = newTransientSInstance(key, refType, documentFactory, versionId);
        instance.getDocument().getDocumentAnnotations().clear();
        return instance;
    }

    @Override
    @Nonnull
    public SInstance loadSInstance(@Nonnull FormKey key, @Nonnull RefType refType, @Nonnull SDocumentFactory documentFactory) {
        final FormEntity entity = loadFormEntity(key);
        return internalLoadSInstance(key, refType, documentFactory, entity.getCurrentFormVersionEntity());
    }


    @Override
    @Nonnull
    public SInstance loadSInstance(@Nonnull FormKey key, @Nonnull RefType refType, @Nonnull SDocumentFactory documentFactory, @Nonnull Long versionId) {
        Objects.requireNonNull(versionId);
        FormVersionEntity formVersionEntity = loadFormVersionEntity(versionId);
        return internalLoadSInstance(key, refType, documentFactory, formVersionEntity);
    }

    private FormKeyLong insertInternal(@Nonnull SInstance instance, Integer inclusionActor) {
        FormEntity entity = saveNewFormEntity(instance);
        saveOrUpdateFormVersion(instance, entity, new FormVersionEntity(), inclusionActor, KEEP_ANNOTATIONS);
        return new FormKeyLong(entity.getCod());
    }

    @Nonnull
    private FormEntity saveNewFormEntity(@Nonnull SInstance instance) {
        final FormEntity entity = new FormEntity();
        entity.setFormType(getOrCreateNewFormTypeEntity(instance.getType()));
        formDAO.saveOrUpdate(entity);
        return entity;
    }

    private FormTypeEntity getOrCreateNewFormTypeEntity(final SType<?> type) {
        String name = type.getName();
        FormTypeEntity formTypeEntity = formTypeDAO.findFormTypeByAbbreviation(name);
        if (formTypeEntity == null) {
            formTypeEntity = new FormTypeEntity();
            formTypeEntity.setAbbreviation(name);
            formTypeEntity.setLabel(SFormUtil.getTypeLabel(type.getClass())
                    .orElse(SFormUtil.getTypeSimpleName((Class<? extends SType<?>>) type.getClass())));
            formTypeEntity.setCacheVersionNumber(1L);//TODO VINICIUS.NUNES
            formTypeDAO.saveOrUpdate(formTypeEntity);
        }
        return formTypeEntity;
    }

    private void saveOrUpdateFormVersion(@Nonnull SInstance instance, @Nonnull FormEntity entity,
            @Nonnull FormVersionEntity formVersionEntity, Integer inclusionActor, boolean keepAnnotations) {
        formVersionEntity.setFormEntity(entity);
        formVersionEntity.setXml(extractContent(instance));
        formVersionEntity.setInclusionActor(inclusionActor);
        formVersionDAO.saveOrUpdate(formVersionEntity);
        entity.setCurrentFormVersionEntity(formVersionEntity);
        if (keepAnnotations) {
            saveOrUpdateFormAnnotation(instance, formVersionEntity, inclusionActor);
        }
        formDAO.saveOrUpdate(entity);
    }

    private void saveOrUpdateFormAnnotation(@Nonnull SInstance instance, @Nonnull FormVersionEntity formVersionEntity, Integer inclusionActor) {
        Map<String, String> classifiedAnnotationsXML = extractAnnotations(instance);
        Map<String, FormAnnotationEntity> classifiedAnnotationsEntities = Optional.ofNullable(formVersionEntity.getFormAnnotations())
                .orElse(new ArrayList<>(0))
                .stream()
                .collect(Collectors.toMap(FormAnnotationEntity::getClassifier, f -> f));
        for (Map.Entry<String, String> entry : classifiedAnnotationsXML.entrySet()) {
            saveOrUpdateFormAnnotation(entry.getKey(), entry.getValue(), formVersionEntity, classifiedAnnotationsEntities.get(entry.getKey()), inclusionActor);
        }
        formVersionDAO.saveOrUpdate(formVersionEntity);
    }

    private void saveOrUpdateFormAnnotation(String classifier, String xml, FormVersionEntity formVersionEntity, FormAnnotationEntity formAnnotationEntity, Integer inclusionActor) {
        if (formAnnotationEntity == null) {
            saveNewFormAnnotation(classifier, xml, formVersionEntity, inclusionActor);
        } else {
            formAnnotationEntity.getAnnotationCurrentVersion().setXml(xml);
        }
    }

    private void saveNewFormAnnotation(String classifier, String xml, FormVersionEntity formVersionEntity, Integer inclusionActor) {
        FormAnnotationEntity formAnnotationEntity = new FormAnnotationEntity();
        formAnnotationEntity.setCod(new FormAnnotationPK());
        formAnnotationEntity.getCod().setClassifier(classifier);
        formAnnotationEntity.getCod().setFormVersionEntity(formVersionEntity);
        formAnnotationDAO.save(formAnnotationEntity);
        saveOrUpdateFormAnnotationVersion(xml, formAnnotationEntity, new FormAnnotationVersionEntity(), inclusionActor);
        formVersionEntity.getFormAnnotations().add(formAnnotationEntity);
    }

    private void saveOrUpdateFormAnnotationVersion(String xml, FormAnnotationEntity formAnnotationEntity, FormAnnotationVersionEntity formAnnotationVersionEntity, Integer inclusionActor) {
        formAnnotationVersionEntity.setFormAnnotationEntity(formAnnotationEntity);
        formAnnotationVersionEntity.setInclusionDate(formAnnotationVersionEntity.getInclusionDate() == null ? new Date() : formAnnotationVersionEntity.getInclusionDate());
        formAnnotationVersionEntity.setInclusionActor(inclusionActor);
        formAnnotationVersionEntity.setXml(xml);
        formAnnotationVersionDAO.saveOrUpdate(formAnnotationVersionEntity);
        formAnnotationEntity.setAnnotationCurrentVersion(formAnnotationVersionEntity);
        formAnnotationDAO.save(formAnnotationEntity);
    }

    private void loadCurrentXmlAnnotationOrEmpty(SDocument document, FormVersionEntity formVersionEntity) {
        document.getDocumentAnnotations().clear();
        for (FormAnnotationEntity formAnnotationEntity : Optional.ofNullable(formVersionEntity).map(FormVersionEntity::getFormAnnotations).orElse(Collections.emptyList())) {
            SFormXMLUtil.annotationLoadFromXml(document,
                    Optional
                            .ofNullable(formAnnotationEntity.getAnnotationCurrentVersion())
                            .map(FormAnnotationVersionEntity::getXml)
                            .orElse(StringUtils.EMPTY));
        }
    }

    @Override
    @Nonnull
    public FormEntity loadFormEntity(@Nonnull FormKey key) {
        return formDAO.findOrException(((FormKeyLong) formKeyManager.validKeyOrException(key)).longValue());
    }

    @Override
    @Nonnull
    public FormVersionEntity loadFormVersionEntity(@Nonnull Long versionId) {
        return formVersionDAO.findOrException(Objects.requireNonNull(versionId));
    }

    private void updateInternal(@Nonnull FormEntity entity, @Nonnull SInstance instance, Integer inclusionActor) {
        saveOrUpdateFormVersion(instance, entity, entity.getCurrentFormVersionEntity(), inclusionActor, KEEP_ANNOTATIONS);
        formDAO.saveOrUpdate(entity);
    }

    /**
     * Extrai as anotações de maneira classificada e separa os xmls por classificador
     *
     * @param instance
     * @return
     */
    @Nonnull
    private Map<String, String> extractAnnotations(@Nonnull SInstance instance) {
        DocumentAnnotations documentAnnotations = instance.getDocument().getDocumentAnnotations();
        Map<String, String> mapClassifierXml  = new HashMap<>();
        for (Map.Entry<String, SIList<SIAnnotation>> entry : documentAnnotations.persistentAnnotationsClassified().entrySet()) {
            mapClassifierXml.put(entry.getKey(), extractContent(entry.getValue()));
        }
        return mapClassifierXml;
    }

    @Nonnull
    private String extractContent(@Nonnull SInstance instance) {
        return SFormXMLUtil.toStringXMLOrEmptyXML(instance);
    }

    public FormKey newVersion(@Nonnull SInstance instance, Integer inclusionActor){
        return newVersion(instance,inclusionActor, true);
    }

    @Override
    @Nonnull
    public FormKey newVersion(@Nonnull SInstance instance, Integer inclusionActor, boolean keepAnnotations) {
        FormKey    formKey    = getFormKeyManager().readFormKeyOrException(instance);
        FormEntity formEntity = loadFormEntity(formKey);
        saveOrUpdateFormVersion(instance, formEntity, new FormVersionEntity(), inclusionActor, keepAnnotations);
        return formKey;
    }

    @Override
    public AnnotationKey insertAnnotation(AnnotationKey annotationKey, SIAnnotation instance) {
        throw new NotImplementedException("Não implementado");
    }

    @Override
    public void deleteAnnotation(AnnotationKey annotationKey) {
        throw new NotImplementedException("Não implementado");
    }

    @Override
    public void updateAnnotation(AnnotationKey annotationKey, SIAnnotation instance) {
        throw new NotImplementedException("Não implementado");
    }

    @Override
    public AnnotationKey insertOrUpdateAnnotation(AnnotationKey annotationKey, SIAnnotation instance) {
        throw new NotImplementedException("Não implementado");
    }

    @Override
    public AnnotationKey newAnnotationVersion(AnnotationKey key, SIAnnotation instance) {
        throw new NotImplementedException("Não implementado");
    }

    @Override
    public AnnotationKey keyFromClassifier(FormKey formKey, String classifier) {
        throw new NotImplementedException("Não implementado");
    }

    @Override
    @Nonnull
    public Optional<FormVersionEntity> findCurrentFormVersion(@Nonnull SDocument document) {
        return findFormEntity(document).map(FormEntity::getCurrentFormVersionEntity);
    }

    @Override
    @Nonnull
    public Optional<FormEntity> findFormEntity(@Nonnull SDocument document) {
        return FormKey.fromOpt(document).map(key -> loadFormEntity(key));
    }
}