/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.internal.xml.MElement;
import br.net.mirante.singular.form.io.MformPersistenciaXML;
import br.net.mirante.singular.form.persistence.AbstractBasicFormPersistence;
import br.net.mirante.singular.form.persistence.AnnotationKey;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.FormKeyLong;
import br.net.mirante.singular.form.persistence.SPackageFormPersistence;
import br.net.mirante.singular.form.persistence.SingularFormPersistenceException;
import br.net.mirante.singular.form.persistence.dao.FormAnnotationDAO;
import br.net.mirante.singular.form.persistence.dao.FormAnnotationVersionDAO;
import br.net.mirante.singular.form.persistence.dao.FormDAO;
import br.net.mirante.singular.form.persistence.dao.FormVersionDAO;
import br.net.mirante.singular.form.persistence.entity.*;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.type.core.annotation.SIAnnotation;
import br.net.mirante.singular.support.persistence.GenericDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class FormService extends AbstractBasicFormPersistence<SInstance, FormKeyLong> implements IFormService {

    private final FormDAO                  formDAO;
    private final FormVersionDAO           formVersionDAO;
    private final FormAnnotationDAO        formAnnotationDAO;
    private final FormAnnotationVersionDAO formAnnotationVersionDAO;
    private final GenericDAO               genericDAO;

    @Inject
    public FormService(FormDAO formDAO,
                       FormVersionDAO formVersionDAO,
                       FormAnnotationDAO formAnnotationDAO,
                       FormAnnotationVersionDAO formAnnotationVersionDAO,
                       GenericDAO genericDAO) {
        super(FormKeyLong.class);
        Assert.notNull(formDAO);
        Assert.notNull(formVersionDAO);
        Assert.notNull(formAnnotationDAO);
        Assert.notNull(formAnnotationVersionDAO);
        Assert.notNull(genericDAO);
        this.formDAO = formDAO;
        this.formVersionDAO = formVersionDAO;
        this.formAnnotationVersionDAO = formAnnotationVersionDAO;
        this.formAnnotationDAO = formAnnotationDAO;
        this.genericDAO = genericDAO;
    }

    @Override
    public SInstance loadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory) {
        final FormEntity entity   = loadFormEntity(key);
        final SInstance  instance = MformPersistenciaXML.fromXML(refType, entity.getCurrentFormVersionEntity().getXml(), documentFactory);
        loadCurrentXmlAnnotationOrEmpty(instance, entity);
        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);
        return instance;
    }

    @Override
    protected FormKeyLong insertInternal(SInstance instance) {
        final FormEntity entity = saveNewFormEntity(instance);
        saveOrUpdateFormVersion(instance, entity, new FormVersionEntity());
        return new FormKeyLong(entity.getCod());
    }

    private FormEntity saveNewFormEntity(SInstance instance) {
        final FormEntity entity = new FormEntity();
        entity.setFormType(getOrCreateNewFormTypeEntity(instance.getType().getName()));
        formDAO.saveOrUpdate(entity);
        return entity;
    }

    private FormTypeEntity getOrCreateNewFormTypeEntity(final String typeAbbreviation) {
        FormTypeEntity formTypeEntity = genericDAO.findByUniqueProperty(FormTypeEntity.class, "abbreviation", typeAbbreviation);
        if(formTypeEntity == null){
            formTypeEntity = new FormTypeEntity();
            formTypeEntity.setAbbreviation(typeAbbreviation);
            formTypeEntity.setCacheVersionNumber(1L);//TODO VINICIUS.NUNES
            genericDAO.saveOrUpdate(formTypeEntity);
        }
        return formTypeEntity;
    }

    private void saveOrUpdateFormVersion(final SInstance instance, final FormEntity entity, final FormVersionEntity formVersionEntity) {
        formVersionEntity.setFormEntity(entity);
        formVersionEntity.setXml(extractContent(instance));
        formVersionDAO.saveOrUpdate(formVersionEntity);
        entity.setCurrentFormVersionEntity(formVersionEntity);
        saveOrUpdateFormAnnotation(instance, formVersionEntity);

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

    }

    private void saveOrUpdateFormAnnotationVersion(String xml, FormAnnotationEntity formAnnotationEntity, FormAnnotationVersionEntity formAnnotationVersionEntity) {
        formAnnotationVersionEntity.setFormAnnotationEntity(formAnnotationEntity);
        formAnnotationVersionEntity.setInclusionDate(formAnnotationVersionEntity.getInclusionDate() == null ? new Date() : formAnnotationVersionEntity.getInclusionDate());
        formAnnotationVersionEntity.setInclusionActor(1);
        formAnnotationVersionEntity.setXml(xml);
        formAnnotationVersionDAO.saveOrUpdate(formAnnotationVersionEntity);
    }


    private void loadCurrentXmlAnnotationOrEmpty(SInstance instance, FormEntity formEntity) {
        for (FormAnnotationEntity formAnnotationEntity : formEntity.getCurrentFormVersionEntity().getFormAnnotations()) {
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
    protected void updateInternal(FormKeyLong key, SInstance instance) {
        updateInternal(loadFormEntity(key), instance);
    }

    protected void updateInternal(FormEntity entity, SInstance instance) {
        saveOrUpdateFormVersion(instance, entity, entity.getCurrentFormVersionEntity());
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

    private String extractContent(SInstance instance) {
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
    public FormKey newVersion(SInstance instance, boolean keepAnnotations) {
        FormKey formKey = readKeyAttribute(instance, null);
        FormEntity formEntity = loadFormEntity(formKey);
        saveOrUpdateFormVersion(instance, formEntity, new FormVersionEntity());
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