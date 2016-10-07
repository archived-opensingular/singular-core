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

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.io.MformPersistenciaXML;
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
import org.opensingular.form.persistence.AbstractBasicFormPersistence;
import org.opensingular.form.persistence.AnnotationKey;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormKeyLong;
import org.opensingular.form.persistence.SPackageFormPersistence;
import org.opensingular.form.persistence.SingularFormPersistenceException;
import org.opensingular.form.type.core.annotation.AtrAnnotation;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
public class FormService extends AbstractBasicFormPersistence<SInstance, FormKeyLong> implements IFormService {

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

    private final Boolean KEEP_ANNOTATIONS = true;

    public FormService() {
        super(FormKeyLong.class);
    }

    @Override
    public FormKey insert(SInstance instance, Integer inclusionActor) {
        return super.insert(instance, inclusionActor);
    }

    @Override
    public FormKey insertOrUpdate(SInstance instance, Integer inclusionActor) {
        return super.insertOrUpdate(instance, inclusionActor);
    }

    private SInstance internalLoadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory, FormVersionEntity formVersionEntity) {
        final SInstance instance = MformPersistenciaXML.fromXML(refType, formVersionEntity.getXml(), documentFactory);
        loadCurrentXmlAnnotationOrEmpty(instance, formVersionEntity);
        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);
        return instance;
    }

    @Override
    public SInstance loadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory) {
        final FormEntity entity = loadFormEntity(key);
        return internalLoadSInstance(key, refType, documentFactory, entity.getCurrentFormVersionEntity());
    }


    @Override
    public SInstance loadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory, Long versionId) {
        final FormVersionEntity formVersionEntity = loadFormVersionEntity(versionId);
        return internalLoadSInstance(key, refType, documentFactory, formVersionEntity);
    }

    @Override
    protected FormKeyLong insertInternal(SInstance instance, Integer inclusionActor) {
        final FormEntity entity = saveNewFormEntity(instance);
        saveOrUpdateFormVersion(instance, entity, new FormVersionEntity(), inclusionActor, KEEP_ANNOTATIONS);
        return new FormKeyLong(entity.getCod());
    }

    private FormEntity saveNewFormEntity(SInstance instance) {
        final FormEntity entity = new FormEntity();
        entity.setFormType(getOrCreateNewFormTypeEntity(instance.getType().getName()));
        formDAO.saveOrUpdate(entity);
        return entity;
    }

    private FormTypeEntity getOrCreateNewFormTypeEntity(final String typeAbbreviation) {
        FormTypeEntity formTypeEntity = formTypeDAO.findFormTypeByAbbreviation(typeAbbreviation);
        if (formTypeEntity == null) {
            formTypeEntity = new FormTypeEntity();
            formTypeEntity.setAbbreviation(typeAbbreviation);
            formTypeEntity.setCacheVersionNumber(1L);//TODO VINICIUS.NUNES
            formTypeDAO.saveOrUpdate(formTypeEntity);
        }
        return formTypeEntity;
    }

    private void saveOrUpdateFormVersion(final SInstance instance, final FormEntity entity, final FormVersionEntity formVersionEntity, Integer inclusionActor, boolean keepAnnotations) {
        formVersionEntity.setFormEntity(entity);
        formVersionEntity.setXml(extractContent(instance));
        formVersionEntity.setInclusionActor(inclusionActor);
        formVersionDAO.saveOrUpdate(formVersionEntity);
        entity.setCurrentFormVersionEntity(formVersionEntity);
        if (keepAnnotations) {
            saveOrUpdateFormAnnotation(instance, formVersionEntity);
        }
        formDAO.saveOrUpdate(entity);
    }

    private void saveOrUpdateFormAnnotation(SInstance instance, FormVersionEntity formVersionEntity) {
        Map<String, String> classifiedAnnotationsXML = extractAnnotations(instance);
        Map<String, FormAnnotationEntity> classifiedAnnotationsEntities = Optional.ofNullable(formVersionEntity.getFormAnnotations())
                .orElse(new ArrayList<>(0))
                .stream()
                .collect(Collectors.toMap(FormAnnotationEntity::getClassifier, f -> f));
        for (Map.Entry<String, String> entry : classifiedAnnotationsXML.entrySet()) {
            saveOrUpdateFormAnnotation(entry.getKey(), entry.getValue(), formVersionEntity, classifiedAnnotationsEntities.get(entry.getKey()));
        }
        formVersionDAO.saveOrUpdate(formVersionEntity);
    }

    private void saveOrUpdateFormAnnotation(String classifier, String xml, FormVersionEntity formVersionEntity, FormAnnotationEntity formAnnotationEntity) {
        if (formAnnotationEntity == null) {
            saveNewFormAnnotation(classifier, xml, formVersionEntity);
        } else {
            formAnnotationEntity.getAnnotationCurrentVersion().setXml(xml);
        }
    }

    private void saveNewFormAnnotation(String classifier, String xml, FormVersionEntity formVersionEntity) {
        FormAnnotationEntity formAnnotationEntity = new FormAnnotationEntity();
        formAnnotationEntity.setCod(new FormAnnotationPK());
        formAnnotationEntity.getCod().setClassifier(classifier);
        formAnnotationEntity.getCod().setFormVersionEntity(formVersionEntity);
        formAnnotationDAO.save(formAnnotationEntity);
        saveOrUpdateFormAnnotationVersion(xml, formAnnotationEntity, new FormAnnotationVersionEntity());
        formVersionEntity.getFormAnnotations().add(formAnnotationEntity);
    }

    private void saveOrUpdateFormAnnotationVersion(String xml, FormAnnotationEntity formAnnotationEntity, FormAnnotationVersionEntity formAnnotationVersionEntity) {
        formAnnotationVersionEntity.setFormAnnotationEntity(formAnnotationEntity);
        formAnnotationVersionEntity.setInclusionDate(formAnnotationVersionEntity.getInclusionDate() == null ? new Date() : formAnnotationVersionEntity.getInclusionDate());
        formAnnotationVersionEntity.setInclusionActor(1);
        formAnnotationVersionEntity.setXml(xml);
        formAnnotationVersionDAO.saveOrUpdate(formAnnotationVersionEntity);
        formAnnotationEntity.setAnnotationCurrentVersion(formAnnotationVersionEntity);
        formAnnotationDAO.save(formAnnotationEntity);
    }

    private void loadCurrentXmlAnnotationOrEmpty(SInstance instance, FormVersionEntity formVersionEntity) {
        instance.as(AtrAnnotation::new).clear();
        for (FormAnnotationEntity formAnnotationEntity : Optional.ofNullable(formVersionEntity).map(FormVersionEntity::getFormAnnotations).orElse(new ArrayList<>(0))) {
            MformPersistenciaXML.annotationLoadFromXml(instance,
                    Optional
                            .ofNullable(formAnnotationEntity.getAnnotationCurrentVersion())
                            .map(FormAnnotationVersionEntity::getXml)
                            .orElse(StringUtils.EMPTY));
        }
    }

    @Override
    public FormEntity loadFormEntity(FormKey key) {
        final FormEntity entity = formDAO.find(checkKey(key, null, "a chave não fosse nula").longValue());
        if (entity == null) {
            throw addInfo(new SingularFormPersistenceException("Form não encontrado")).add("key", key);
        } else {
            return entity;
        }
    }

    @Override
    public FormVersionEntity loadFormVersionEntity(Long versionId) {
        return formVersionDAO.find(versionId);
    }

    @Override
    protected void updateInternal(FormKeyLong key, SInstance instance, Integer inclusionActor) {
        updateInternal(loadFormEntity(key), instance, inclusionActor);
    }

    protected void updateInternal(FormEntity entity, SInstance instance, Integer inclusionActor) {
        saveOrUpdateFormVersion(instance, entity, entity.getCurrentFormVersionEntity(), inclusionActor, KEEP_ANNOTATIONS);
        formDAO.saveOrUpdate(entity);
    }

    @Override
    protected void deleteInternal(FormKeyLong key) {
        throw new RuntimeException("Metodo nao implementado");
    }

    /**
     * Extrai as anotações de maneira classificada e separa os xmls por classificador
     *
     * @param instance
     * @return
     */
    private Map<String, String> extractAnnotations(SInstance instance) {
        AtrAnnotation       annotatedInstance = instance.as(AtrAnnotation::new);
        Map<String, String> mapClassifierXml  = new HashMap<>();
        for (Map.Entry<String, SIList<SIAnnotation>> entry : annotatedInstance.persistentAnnotationsClassified().entrySet()) {
            mapClassifierXml.put(entry.getKey(), extractContent(entry.getValue()));
        }
        return mapClassifierXml;
    }

    public String extractContent(SInstance instance) {
        if (instance == null) {
            return null;
        }
        instance.getDocument().persistFiles();
        final MElement mElement = MformPersistenciaXML.toXML(instance);
        if (mElement != null) {
            return mElement.toStringExato();
        } else {
            return "";
        }
    }

    @Override
    public FormKey newVersion(SInstance instance, Integer inclusionActor) {
        return super.newVersion(instance, inclusionActor);
    }

    @Override
    public FormKey newVersion(SInstance instance, Integer inclusionActor, boolean keepAnnotations) {
        FormKey    formKey    = readKeyAttribute(instance, null);
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
}