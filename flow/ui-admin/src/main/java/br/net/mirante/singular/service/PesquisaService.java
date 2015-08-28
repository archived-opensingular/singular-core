package br.net.mirante.singular.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.dao.PesquisaDAO;

@Service
@Transactional(readOnly = true)
public class PesquisaService {

    @Inject
    private PesquisaDAO pesquisaDAO;

    public List<Map<String, String>> retrieveMeanTimeByProcess() {
        return pesquisaDAO.retrieveMeanTimeByProcess();
    }
}
