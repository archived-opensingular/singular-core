package br.net.mirante.singular.service;

import br.net.mirante.singular.dao.FeedDTO;
import br.net.mirante.singular.dao.InstanceDAO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Singleton
public class FeedService {


    private List<FeedDTO> result;

    @Inject
    private InstanceDAO instanceDao;

    @Inject
    private PesquisaService pesquisaService;

    @Transactional
    public List<FeedDTO> retrieveFeed() {
        if (result == null) {
            result = new ArrayList<>();
            List<Map<String, String>> medias = pesquisaService.retrieveMeanTimeByProcess(Period.ofYears(-1));
            for (Map<String, String> mediaPorProcesso : medias) {
                String sigla = mediaPorProcesso.get("SIGLA");
                BigDecimal media = new BigDecimal(mediaPorProcesso.get("MEAN"));
                List<Map<String, String>> instancias = instanceDao.retrieveAllDelayedBySigla(sigla, media);

                for (Map<String, String> instancia : instancias) {
                    result.add(new FeedDTO(mediaPorProcesso.get("NOME"), instancia.get("DESCRICAO"), new BigDecimal(instancia.get("DIAS")), media));
                }
            }
        }
        return result;
    }
}
