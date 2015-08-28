package br.net.mirante.singular.service;

import br.net.mirante.singular.dao.PesquisaDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class PesquisaService {

    @Inject
    private PesquisaDAO pesquisaDAO;

    public int countAll() {
        return pesquisaDAO.countAll();
    }

    public byte[] retrieveProcessDiagram(String sigla) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl("http://localhost:8080/alocpro/rest/diagram")
                        .queryParam("sigla", sigla);
        String encodedImage = restTemplate.getForObject(uriComponentsBuilder.build().encode().toUri(), String.class);
        return Base64.getDecoder().decode(encodedImage);
    }

    public List<Map<String, String>> retrieveMeanTimeByProcess() {
        return pesquisaDAO.retrieveMeanTimeByProcess();
    }
}
