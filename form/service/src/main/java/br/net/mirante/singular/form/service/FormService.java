/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.internal.xml.MElement;
import br.net.mirante.singular.form.io.MformPersistenciaXML;
import br.net.mirante.singular.form.persistence.*;
import br.net.mirante.singular.form.persistence.dao.FormAnnotationVersionDAO;
import br.net.mirante.singular.form.persistence.dao.FormDAO;
import br.net.mirante.singular.form.persistence.dao.FormVersionDAO;
import br.net.mirante.singular.form.persistence.entity.FormAnnotationVersionEntity;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.persistence.entity.FormVersionEntity;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
public class FormService extends AbstractBasicFormPersistence<SInstance, FormKeyLong> implements IFormService {

    private final FormDAO                  formDAO;
    private final FormVersionDAO           formVersionDAO;
    private final FormAnnotationVersionDAO formAnnotationVersionDAO;

    @Inject
    public FormService(FormDAO formDAO, FormVersionDAO formVersionDAO, FormAnnotationVersionDAO formAnnotationVersionDAO) {
        super(FormKeyLong.class);
        Assert.notNull(formDAO);
        Assert.notNull(formVersionDAO);
        Assert.notNull(formAnnotationVersionDAO);
        this.formDAO = formDAO;
        this.formVersionDAO = formVersionDAO;
        this.formAnnotationVersionDAO = formAnnotationVersionDAO;
    }

    @Override
    public SInstance loadSInstance(FormKey key, RefType refType, SDocumentFactory documentFactory) {
        final FormEntity entity   = loadFormEntity(key);
        final SInstance  instance = MformPersistenciaXML.fromXML(refType, entity.getCurrentFormVersionEntity().getXml(), documentFactory);
        MformPersistenciaXML.annotationLoadFromXml(instance, loadCurrentXmlAnnotationOrEmpty(entity));
        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);
        return instance;
    }

    @Override
    protected FormKeyLong insertInternal(SInstance instance) {
        final FormEntity entity = saveNewFormEntity();
        saveOrUpdateFormVersion(instance, entity, new FormVersionEntity());
        return new FormKeyLong(entity.getCod());
    }

    private FormEntity saveNewFormEntity() {
        final FormEntity entity = new FormEntity();
        formDAO.saveOrUpdate(entity);
        return entity;
    }

    @Deprecated
    private void saveOrUpdateFormVersion(final SInstance instance, final FormEntity entity, final FormVersionEntity formVersionEntity) {
        formVersionEntity.setFormEntity(entity);
        formVersionEntity.setXml(extractContent(instance));
        formVersionDAO.saveOrUpdate(formVersionEntity);
        entity.setCurrentFormVersionEntity(formVersionEntity);
        saveOrUpdateFormAnnotationVersion(instance, entity.getCurrentFormVersionEntity());
    }

    @Deprecated
    private void saveOrUpdateFormAnnotationVersion(SInstance instance, FormVersionEntity formVersionEntity) {
        saveOrUpdateFormAnnotationVersion(extractAnnotations(instance), formVersionEntity);
    }

    @Deprecated
    private void saveOrUpdateFormAnnotationVersion(String xmlAnnotation, FormVersionEntity formVersionEntity) {
        if (xmlAnnotation != null) {
            final FormAnnotationVersionEntity formAnnotationVersionEntity = formVersionEntity.getLatestFormAnnotationVersionEntity().orElse(new FormAnnotationVersionEntity());
            formAnnotationVersionEntity.setFormVersionEntity(formVersionEntity);
            formAnnotationVersionEntity.setXml(xmlAnnotation);
            formAnnotationVersionDAO.saveOrUpdate(formAnnotationVersionEntity);
        }
    }

    @Deprecated
    private String loadCurrentXmlAnnotationOrEmpty(FormEntity formEntity) {
        return formEntity.getCurrentFormVersionEntity().getLatestFormAnnotationVersionEntity().map(FormAnnotationVersionEntity::getXml).orElse(StringUtils.EMPTY);
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

    private String extractAnnotations(SInstance instance) {
        AtrAnnotation annotatedInstance = instance.as(AtrAnnotation::new);
        return extractContent(annotatedInstance.persistentAnnotations());
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
        //TODO: FORM_ANNOTATION_VERSION
        return null;
    }

}