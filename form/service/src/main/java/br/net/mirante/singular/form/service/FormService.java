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
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.FormKeyLong;
import br.net.mirante.singular.form.persistence.SPackageFormPersistence;
import br.net.mirante.singular.form.persistence.dao.FormDAO;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
public class FormService implements IFormService{

    @Inject
    private FormDAO formDAO;

    public FormKey keyFromObject(Object objectValueToBeConverted) {
        return FormKeyLong.convertToKey(objectValueToBeConverted);
    }

    @Override
    public SInstance loadFormInstance(FormKey key, RefType refType, SDocumentFactory documentFactory) {
        FormEntity entity = formDAO.find(getCod(key));
        if(entity == null){
            return null;
        }
        SInstance instance = MformPersistenciaXML.fromXML(refType, entity.getXml(), documentFactory);
        
        MformPersistenciaXML.annotationLoadFromXml(instance, entity.getXmlAnnotations());

        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);
        return instance;
    }
    
    @Override
    public FormKey insertOrUpdate(SInstance instance) {
        FormKey key = getKey(instance);
        if (key == null) {
            key = insert(instance);
            instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);
        } else {
            update(instance);
        }
        return key;
    }
    
    @Override
    public FormKey insert(SInstance instance) {
        FormEntity entity = new FormEntity();
        entity.setXml(extractContent(instance));
        entity.setXmlAnnotations(extractAnnotations(instance));
        formDAO.saveOrUpdate(entity);
        return new FormKeyLong(entity.getCod());
    }

    @Override
    public void update(SInstance instance) {
        FormKey key = getKey(instance);
        FormEntity entity = formDAO.find(getCod(key));
        if(entity == null){
            throw new SingularFormException("Form não encontrado key="+key);
        }
        entity.setXml(extractContent(instance));
        entity.setXmlAnnotations(extractAnnotations(instance));
        formDAO.saveOrUpdate(entity);
    }

    private FormKey getKey(SInstance instance) {
        return instance.getAttributeValue(SPackageFormPersistence.ATR_FORM_KEY);
    }

    private Long getCod(FormKey key) {
        return ((FormKeyLong) key).getValue();
    }

    private String extractAnnotations(SInstance instance){
        AtrAnnotation annotatedInstance = instance.as(AtrAnnotation::new);
        return extractContent(annotatedInstance.persistentAnnotations());
    }

    private String extractContent(SInstance instance){
        if(instance == null){
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
