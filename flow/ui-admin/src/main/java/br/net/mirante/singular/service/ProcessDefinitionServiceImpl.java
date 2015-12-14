package br.net.mirante.singular.service;

import java.util.Base64;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.net.mirante.singular.dao.DefinitionDAO;
import br.net.mirante.singular.dao.InstanceDAO;
import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.InstanceDTO;
import br.net.mirante.singular.dto.MetaDataDTO;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.renderer.FlowRendererFactory;

@Service("processDefinitionService")
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Inject
    private DefinitionDAO definitionDAO;

    @Inject
    private InstanceDAO instanceDAO;

    @Value("#{singularAdmin['retrieve.process.diagram.restful.url']}")
    private String retrieveProcessDiagramRestURL;

    @Override
    @Transactional
    public DefinitionDTO retrieveById(Integer processDefinitionCod) {
        return definitionDAO.retrieveById(processDefinitionCod);
    }

    @Override
    @Transactional
    public List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc) {
        return definitionDAO.retrieveAll(first, size, orderByProperty, asc);
    }

    @Override
    @Transactional
    public int countAll() {
        return definitionDAO.countAll();
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

    @Override
    public byte[] retrieveProcessDiagramFromRestURL(String sigla) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(retrieveProcessDiagramRestURL).queryParam("sigla", sigla);
        String encodedImage = restTemplate.getForObject(uriComponentsBuilder.build().encode().toUri(), String.class);
        return Base64.getDecoder().decode(encodedImage);
    }

    @Override
    public byte[] retrieveProcessDiagram(String sigla) {
        ProcessDefinition<?> definicao = Flow.getProcessDefinitionWith(sigla);
        if (definicao != null) {
            return FlowRendererFactory.generateImageFor(definicao);
        }
        return null;
    }
}
