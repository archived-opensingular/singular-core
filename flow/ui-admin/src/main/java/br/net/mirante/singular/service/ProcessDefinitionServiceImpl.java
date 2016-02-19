package br.net.mirante.singular.service;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.DefinitionDAO;
import br.net.mirante.singular.dao.InstanceDAO;
import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.InstanceDTO;
import br.net.mirante.singular.dto.MetaDataDTO;

@Service("processDefinitionService")
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Inject
    private DefinitionDAO definitionDAO;

    @Inject
    private InstanceDAO instanceDAO;

    @Override
    @Transactional
    public DefinitionDTO retrieveById(Integer processDefinitionCod) {
        return definitionDAO.retrieveById(processDefinitionCod);
    }

    @Cacheable(value = "retrieveProcessDefinitionByKey", cacheManager = "cacheManager")
    @Override
    @Transactional
    public DefinitionDTO retrieveByKey(String processDefinitionKey) {
        return definitionDAO.retrieveByKey(processDefinitionKey);
    }
    
    @Override
    @Transactional
    public List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess) {
        return definitionDAO.retrieveAll(first, size, orderByProperty, asc, processCodeWithAccess);
    }

    @Override
    @Transactional
    public int countAll(Set<String> processCodeWithAccess) {
        return definitionDAO.countAll(processCodeWithAccess);
    }

    @Override
    @Transactional
    public List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod) {
        return instanceDAO.retrieveAll(first, size, orderByProperty, asc, processDefinitionCod);
    }

    @Override
    @Transactional
    public int countAll(Integer processDefinitionCod) {
        return instanceDAO.countAll(processDefinitionCod);
    }

    @Override
    @Transactional
    public List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod) {
        return definitionDAO.retrieveMetaData(processDefinitionCod);
    }

}
