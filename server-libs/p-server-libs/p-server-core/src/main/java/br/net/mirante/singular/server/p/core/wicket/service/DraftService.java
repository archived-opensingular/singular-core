package br.net.mirante.singular.server.p.core.wicket.service;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.persistence.DraftPersistence;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.service.FormService;
import br.net.mirante.singular.server.commons.persistence.entity.form.DraftEntity;
import br.net.mirante.singular.server.p.core.wicket.dao.DraftDao;
import br.net.mirante.singular.support.persistence.enums.SimNao;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

@Service
public class DraftService implements DraftPersistence {

    private final DraftDao    draftDao;
    private final FormService formService;

    @Inject
    public DraftService(DraftDao draftDao, FormService formService) {
        this.draftDao = draftDao;
        this.formService = formService;
    }

    @Override
    public Long insert(SInstance instance) {
        final DraftEntity draftEntity = new DraftEntity();
        draftEntity.setStartDate(new Date());
        draftEntity.setPeticionado(SimNao.NAO);
        update(instance, draftEntity);
        return draftEntity.getCod();
    }

    @Override
    public void update(SInstance instance, Long draftCod) {
        update(instance, draftDao.find(draftCod));
    }

    private void update(SInstance instance, DraftEntity draftEntity) {
        final FormKey formKey;
        if (draftEntity.getFormVersionEntity() != null && draftEntity.getFormVersionEntity().getFormEntity() != null) {
            formKey = formService.insert(instance);
        } else {
            formService.update(instance);
            formKey = formService.keyFromObject(draftEntity.getFormVersionEntity().getFormEntity().getCod());
        }
        draftEntity.setEditionDate(new Date());
        draftEntity.setFormVersionEntity(formService.loadFormEntity(formKey).getCurrentFormVersionEntity());
        draftDao.saveOrUpdate(draftEntity);
    }

}