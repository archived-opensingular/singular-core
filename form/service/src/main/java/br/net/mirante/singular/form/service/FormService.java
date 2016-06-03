/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import javax.inject.Inject;
import javax.transaction.Transactional;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.internal.xml.MElement;
import br.net.mirante.singular.form.io.MformPersistenciaXML;
import br.net.mirante.singular.form.persistence.dao.FormDAO;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;

@Transactional
public class FormService implements IFormService{

    @Inject
    private FormDAO formDAO;
    
    @Override
    public SInstance loadFormInstance(Long cod, RefType refType, SDocumentFactory documentFactory) {
        FormEntity entity = formDAO.find(cod);
        if(entity == null){
            return null;
        }
        SInstance instance = MformPersistenciaXML.fromXML(refType, entity.getXml(), documentFactory);
        
        MformPersistenciaXML.annotationLoadFromXml(instance, entity.getXmlAnnotations());
        
        return instance;
    }
    
    @Override
    public FormDTO findForm(Long cod) {
        FormEntity entity = formDAO.find(cod);
        if(entity == null){
            return null;
        }
        FormDTO dto = new FormDTO();
        dto.setCod(entity.getCod());
        return dto;
    }

    @Override
    public void saveOrUpdateForm(FormDTO form, SInstance instance) {
        if(form != null){
            if(form.getCod() == null){
                form.setCod(saveForm(instance).getCod());
            } else {
                updateForm(form, instance);
            }
        }
    }
    
    @Override
    public FormDTO saveForm(SInstance instance) {
        FormEntity entity = new FormEntity();
        entity.setXml(extractContent(instance));
        entity.setXmlAnnotations(extractAnnotations(instance));
        formDAO.saveOrUpdate(entity);
        
        FormDTO form = new FormDTO();
        form.setCod(entity.getCod());
        
        return form;
    }

    @Override
    public void updateForm(FormDTO form, SInstance instance) {
        FormEntity entity = formDAO.find(form.getCod());
        if(entity == null){
            throw new SingularFormException("Form não encontrado cod="+form.getCod());
        }
        entity.setXml(extractContent(instance));
        entity.setXmlAnnotations(extractAnnotations(instance));
        formDAO.saveOrUpdate(entity);
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
