package br.net.mirante.singular.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.net.mirante.singular.dao.DefinitionDAO;
import br.net.mirante.singular.dao.DefinitionDTO;
import br.net.mirante.singular.dao.InstanceDAO;
import br.net.mirante.singular.dao.InstanceDTO;
import br.net.mirante.singular.dao.MetaDataDTO;

@Service("processDefinitionService")
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Inject
    private DefinitionDAO definitionDAO;

    @Inject
    private InstanceDAO instanceDAO;

    @Value("#{admin['retrieve.process.diagram.restful.url']}")
    private String retrieveProcessDiagramRestURL;

    @Override
    @Transactional
    public DefinitionDTO retrieveById(Long id) {
        Object[] result = definitionDAO.retrieveById(id);
        return new DefinitionDTO((Long) result[0], (String) result[1], (String) result[2], null, null, null, null);
    }

    @Override
    @Transactional
    public List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc) {
        List<Object[]> results = definitionDAO.retrieveAll(first, size, orderByProperty, asc);
        return results.stream()
                .map(o -> new DefinitionDTO((Long) o[0], (String) o[1], (String) o[2], (String) o[3],
                        (Long) o[4], (Long) o[5], (Long) o[6]))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int countAll() {
        return definitionDAO.countAll();
    }

    @Override
    @Transactional
    public List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Long id) {
        List<Object[]> results = instanceDAO.retrieveAll(first, size, orderByProperty, asc, id);
        return results.stream()
                .map(o -> new InstanceDTO((Long) o[0], (String) o[1], (Long) o[2], (Date) o[3],
                        (Long) o[4], (Date) o[5], (String) o[6]))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int countAll(Long id) {
        return instanceDAO.countAll(id);
    }

    @Override
    public byte[] retrieveProcessDiagram(String sigla) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(retrieveProcessDiagramRestURL).queryParam("sigla", sigla);
        String encodedImage = restTemplate.getForObject(uriComponentsBuilder.build().encode().toUri(), String.class);
        return Base64.getDecoder().decode(encodedImage);
    }

    @Override
    public List<MetaDataDTO> retrieveMetaData(Long id) {
        List<MetaDataDTO> metadatas = new ArrayList<>();
        MetaDataDTO metadata = new MetaDataDTO();
        metadata.setTask("Tarefa 1");
        metadata.setType("Tipo 1");
        metadata.setExecutor("Executor 1");
        metadata.setTransactions(new ArrayList<>());
        MetaDataDTO.TransactionDTO transaction = metadata.new TransactionDTO();
        transaction.setSource("1");
        transaction.setTarget("2");
        transaction.setParameters(new ArrayList<>());
        MetaDataDTO.ParameterDTO parameter = metadata.new ParameterDTO();
        parameter.setName("Par 1");
        parameter.setRequired(false);
        transaction.getParameters().add(parameter);
        metadata.getTransactions().add(transaction);
        transaction = metadata.new TransactionDTO();
        transaction.setSource("2");
        transaction.setTarget("3");
        transaction.setParameters(new ArrayList<>());
        parameter = metadata.new ParameterDTO();
        parameter.setName("Par 2");
        parameter.setRequired(true);
        transaction.getParameters().add(parameter);
        parameter = metadata.new ParameterDTO();
        parameter.setName("Par 3");
        parameter.setRequired(false);
        transaction.getParameters().add(parameter);
        metadata.getTransactions().add(transaction);
        metadatas.add(metadata);
        return metadatas;
    }
}
