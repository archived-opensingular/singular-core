package br.net.mirante.singular.server.commons.service;


import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.server.commons.persistence.dao.flow.GrupoProcessoDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.PeticaoDAO;

import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.persistence.entity.form.Peticao;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;

import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Transactional
public class PetitionService {

    @Inject
    private PeticaoDAO peticaoDAO;

    @Inject
    private GrupoProcessoDAO grupoProcessoDAO;


    public void delete(PeticaoDTO peticao) {
        peticaoDAO.delete(peticaoDAO.find(peticao.getCod()));
    }


    public long countQuickSearch(QuickFilter filtro, String siglaProcesso) {
        return countQuickSearch(filtro, Collections.singletonList(siglaProcesso));
    }


    public Long countQuickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        return peticaoDAO.countQuickSearch(filtro, siglasProcesso);
    }


    public List<? extends PeticaoDTO> quickSearch(QuickFilter filtro, String siglaProcesso) {
        return quickSearch(filtro, Collections.singletonList(siglaProcesso));
    }


    public List<? extends PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        return peticaoDAO.quickSearch(filtro, siglasProcesso);
    }


    public void saveOrUpdate(Peticao peticao) {
        peticaoDAO.saveOrUpdate(peticao);
    }

    public void send(Peticao peticao) {
        iniciarProcessoFlow(peticao);
        saveOrUpdate(peticao);
    }

    private void iniciarProcessoFlow(Peticao peticao) {
        ProcessDefinition<?> processDefinition = Flow.getProcessDefinitionWith(peticao.getProcessType());
        ProcessInstance processInstance = processDefinition.newInstance();
        processInstance.setDescription(peticao.getDescription());
        processInstance.start();
        peticao.setProcessInstanceEntity((ProcessInstanceEntity) processInstance.getEntity());
    }


    public Peticao find(Long cod) {
        return peticaoDAO.find(cod);
    }


    public Peticao findByProcessCod(Integer cod) {
        return peticaoDAO.findByProcessCod(cod);
    }


    public List<ProcessGroupEntity> listarTodosGruposProcesso() {
        return grupoProcessoDAO.listarTodosGruposProcesso();
    }


    public ProcessGroupEntity findByProcessGroupName(String name) {
        return grupoProcessoDAO.findByName(name);
    }

}
