package br.net.mirante.singular.server.core.service;


import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.core.persistence.dao.flow.GrupoProcessoDAO;
import br.net.mirante.singular.server.core.persistence.dao.form.PeticaoDAO;
import br.net.mirante.singular.server.core.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.core.persistence.entity.form.Peticao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class PetitionService {

    @Inject
    private PeticaoDAO peticaoDAO;

    @Inject
    private GrupoProcessoDAO grupoProcessoDAO;

    @Transactional
    public void delete(PeticaoDTO peticao) {
        peticaoDAO.delete(peticaoDAO.find(peticao.getCod()));
    }

    @Transactional
    public long countQuickSearch(QuickFilter filtro, String siglaProcesso) {
        return countQuickSearch(filtro, Collections.singletonList(siglaProcesso));
    }

    @Transactional
    public Long countQuickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        return peticaoDAO.countQuickSearch(filtro, siglasProcesso);
    }

    @Transactional
    public List<PeticaoDTO> quickSearch(QuickFilter filtro, String siglaProcesso) {
        return quickSearch(filtro, Collections.singletonList(siglaProcesso));
    }

    @Transactional
    public List<PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        return peticaoDAO.quickSearch(filtro, siglasProcesso);
    }

    @Transactional
    public void saveOrUpdate(Peticao peticao) {
//        peticao.setEditionDate(new Date());
        peticaoDAO.saveOrUpdate(peticao);
    }

    @Transactional
    public void send(Peticao peticao) {
        iniciarProcessoFlow(peticao);
        criarProcessoAnvisa(peticao);
        saveOrUpdate(peticao);
    }

    private void iniciarProcessoFlow(Peticao peticao) {
        ProcessDefinition<?> processDefinition = Flow.getProcessDefinitionWith(peticao.getProcessType());
        ProcessInstance processInstance = processDefinition.newInstance();
        processInstance.setDescription(peticao.getDescription());
        processInstance.start();
        peticao.setProcessInstanceEntity((ProcessInstanceEntity) processInstance.getEntity());
    }

    private void criarProcessoAnvisa(Peticao peticao) {
    }

    @Transactional
    public Peticao find(Long cod) {
        return peticaoDAO.find(cod);
    }

    @Transactional
    public Peticao findByProcessCod(Integer cod) {
        return peticaoDAO.findByProcessCod(cod);
    }

    @Transactional
    public List<ProcessGroupEntity> listarTodosGruposProcesso() {
        return grupoProcessoDAO.listarTodosGruposProcesso();
    }

    @Transactional
    public ProcessGroupEntity findByProcessGroupName(String name) {
        return grupoProcessoDAO.findByName(name);
    }

}
