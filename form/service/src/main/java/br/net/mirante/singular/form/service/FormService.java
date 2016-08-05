/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SingularFormException;
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

        FormEntity entity = formDAO.find(checkKey(key, null, "a chave não fosse nula").longValue());

        if (entity == null) {
            return null;
        }

        SInstance instance = MformPersistenciaXML.fromXML(refType, entity.getCurrentFormVersionEntity().getXml(), documentFactory);

        MformPersistenciaXML.annotationLoadFromXml(instance, entity.getCurrentFormVersionEntity().getLatestFormAnnotationVersionEntity().map(FormAnnotationVersionEntity::getXml).orElse(StringUtils.EMPTY));

        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);
        return instance;
    }

    @Override
    protected FormKeyLong insertInternal(SInstance instance) {

        final FormEntity                  entity                      = new FormEntity();
        final FormVersionEntity           formVersionEntity           = new FormVersionEntity();
        final FormAnnotationVersionEntity formAnnotationVersionEntity = new FormAnnotationVersionEntity();

        formVersionEntity.setFormEntity(entity);
        formAnnotationVersionEntity.setFormVersionEntity(formVersionEntity);
        entity.setCurrentFormVersionEntity(formVersionEntity);

        formVersionEntity.setXml(extractContent(instance));
        formAnnotationVersionEntity.setXml(extractAnnotations(instance));

        formDAO.saveOrUpdate(entity);
        formVersionDAO.saveOrUpdate(formVersionEntity);
        formAnnotationVersionDAO.saveOrUpdate(formAnnotationVersionEntity);

        return new FormKeyLong(entity.getCod());
    }

    @Override
    protected void updateInternal(FormKeyLong key, SInstance instance) {
        final FormEntity entity = formDAO.find(key.longValue());

        if (entity == null) {
            throw addInfo(new SingularFormPersistenceException("Form não encontrado")).add("key", key);
        }

        entity.getCurrentFormVersionEntity().setXml(extractContent(instance));
        entity.getCurrentFormVersionEntity().getLatestFormAnnotationVersionEntity().ifPresent(formAnnotationVersionEntity -> {
            formAnnotationVersionEntity.setXml(extractAnnotations(instance));
            formAnnotationVersionDAO.saveOrUpdate(formAnnotationVersionEntity);
        });
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
