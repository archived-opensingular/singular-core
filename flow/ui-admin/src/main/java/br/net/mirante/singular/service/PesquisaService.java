package br.net.mirante.singular.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

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
                .map(o -> new PesquisaDTO((Long) o[0], (String) o[1], (String) o[2], (Long) o[3]))
                .collect(Collectors.toList());
    }

    @Transactional
    public int countAll() {
        return pesquisaDAO.countAll();
    }
}
