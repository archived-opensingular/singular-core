package br.net.mirante.singular.service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.net.mirante.singular.dao.PesquisaDAO;
import br.net.mirante.singular.dao.PesquisaDTO;

@Service
public class PesquisaService {

    @Inject
    private PesquisaDAO pesquisaDAO;

    @Transactional
    public List<PesquisaDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc) {
        List<Object[]> results = pesquisaDAO.retrieveAll(first, size, orderByProperty, asc);
        return results.stream()
                .map(o -> new PesquisaDTO((Long) o[0], (String) o[1], (String) o[2], (String) o[3]))
                .collect(Collectors.toList());
    }

    @Transactional
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
}
