/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.persistence.service;

import javax.inject.Inject;
import javax.transaction.Transactional;

import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.persistence.dao.FormDAO;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.service.IPersistenceService;
import br.net.mirante.singular.form.service.dto.FormDTO;

@Transactional
public class PersistenceService implements IPersistenceService{

    @Inject
    private FormDAO formDAO;
    
    @Override
    public FormDTO find(Long cod) {
        FormEntity entity = formDAO.find(cod);
        if(entity == null){
            return null;
        }
        FormDTO dto = new FormDTO();
        dto.setCod(entity.getCod());
        dto.setXml(entity.getXml());
        return dto;
    }

    @Override
    public FormDTO saveOrUpdate(FormDTO form) {
        if(form.getCod() == null){
            return save(form);
        }
        update(form);
        return form;
    }
    
    @Override
    public FormDTO save(FormDTO form) {
        FormEntity entity = new FormEntity();
        entity.setXml(form.getXml());
        formDAO.saveOrUpdate(entity);
        
        form.setCod(entity.getCod());
        return form;
    }

    @Override
    public void update(FormDTO form) {
        FormEntity entity = formDAO.find(form.getCod());
        if(entity == null){
            throw new SingularFormException("Form não encontrado cod="+form.getCod());
        }
        entity.setXml(form.getXml());
        formDAO.saveOrUpdate(entity);
    }

}
