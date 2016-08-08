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

import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
public class FormService extends AbstractBasicFormPersistence<SInstance, FormKeyLong> implements IFormService {

    @Inject
    private FormDAO formDAO;

    @Inject
    private FormVersionDAO formVersionDAO;

    @Inject
    private FormAnnotationVersionDAO formAnnotationVersionDAO;

    public FormService() {
        super(FormKeyLong.class);
    }

    @Override
    public SInstance loadFormInstance(FormKey key, RefType refType, SDocumentFactory documentFactory) {

        final FormEntity entity = formDAO.find(checkKey(key, null, "a chave não fosse nula").longValue());

        if (entity == null) {
            return null;
        }

        final SInstance instance = MformPersistenciaXML.fromXML(refType, entity.getCurrentFormVersionEntity().getXml(), documentFactory);
        MformPersistenciaXML.annotationLoadFromXml(instance, loadCurrentXmlAnnotationOrEmpty(entity));
        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);

        return instance;
    }

    @Override
    protected FormKeyLong insertInternal(SInstance instance) {

        final FormEntity        entity            = new FormEntity();
        final FormVersionEntity formVersionEntity = new FormVersionEntity();

        formVersionEntity.setFormEntity(entity);
        entity.setCurrentFormVersionEntity(formVersionEntity);

        formVersionEntity.setXml(extractContent(instance));

        formDAO.saveOrUpdate(entity);
        formVersionDAO.saveOrUpdate(formVersionEntity);

        saveOrUpdateAnnotations(instance, formVersionEntity);

        return new FormKeyLong(entity.getCod());
    }

    @Deprecated
    private void saveOrUpdateAnnotations(SInstance instance, FormVersionEntity formVersionEntity) {
        saveOrUpdateAnnotations(extractAnnotations(instance), formVersionEntity);
    }

    @Deprecated
    private void saveOrUpdateAnnotations(String xmlAnnotation, FormVersionEntity formVersionEntity) {
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
    protected void updateInternal(FormKeyLong key, SInstance instance) {
        final FormEntity entity = formDAO.find(key.longValue());

        if (entity == null) {
            throw addInfo(new SingularFormPersistenceException("Form não encontrado")).add("key", key);
        }

        //TODO
        entity.getCurrentFormVersionEntity().setXml(extractContent(instance));
        saveOrUpdateAnnotations(instance, entity.getCurrentFormVersionEntity());

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
}
