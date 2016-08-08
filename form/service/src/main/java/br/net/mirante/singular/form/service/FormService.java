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
import br.net.mirante.singular.form.persistence.dao.FormDAO;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
public class FormService extends AbstractBasicFormPersistence<SInstance, FormKeyLong> implements IFormService{

    @Inject
    private FormDAO formDAO;

    public FormService() {
        super(FormKeyLong.class);
    }

    @Override
    public SInstance loadFormInstance(FormKey key, RefType refType, SDocumentFactory documentFactory) {
        FormEntity entity = formDAO.find(checkKey(key, null,"a chave não fosse nula").longValue());
        if(entity == null){
            return null;
        }
        SInstance instance = MformPersistenciaXML.fromXML(refType, entity.getXml(), documentFactory);
        
        MformPersistenciaXML.annotationLoadFromXml(instance, entity.getXmlAnnotations());

        instance.setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, key);
        return instance;
    }
    
    @Override
    protected FormKeyLong insertInternal(SInstance instance) {
        FormEntity entity = new FormEntity();
        entity.setXml(extractContent(instance));
        entity.setXmlAnnotations(extractAnnotations(instance));
        formDAO.saveOrUpdate(entity);
        return new FormKeyLong(entity.getCod());
    }

    @Override
    protected void updateInternal(FormKeyLong key, SInstance instance) {
        FormEntity entity = formDAO.find(key.longValue());
        if(entity == null){
            throw addInfo(new SingularFormPersistenceException("Form não encontrado")).add("key", key);
        }
        entity.setXml(extractContent(instance));
        entity.setXmlAnnotations(extractAnnotations(instance));
        formDAO.saveOrUpdate(entity);
    }

    @Override
    protected void deleteInternal(FormKeyLong key) {
        throw new RuntimeException("Metodo nao implementado");
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

    @Override
    public FormKey newVersion(SInstance instance, boolean keepAnnotations) {
        //TODO: FORM_ANNOTATION_VERSION
        return null;
    }
}
