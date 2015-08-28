package br.net.mirante.singular.service;

import br.net.mirante.singular.dao.PesquisaDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Period;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class PesquisaService {

    @Inject
    private PesquisaDAO pesquisaDAO;

    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period) {
        return pesquisaDAO.retrieveMeanTimeByProcess(period);
    }
}
