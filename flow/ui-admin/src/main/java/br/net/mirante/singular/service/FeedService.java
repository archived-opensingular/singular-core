package br.net.mirante.singular.service;

import java.math.BigDecimal;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.FeedDTO;
import br.net.mirante.singular.dao.InstanceDAO;

@Service
@Singleton
public class FeedService {

    @Inject
    private InstanceDAO instanceDao;

    @Inject
    private PesquisaService pesquisaService;

    @Cacheable("retrieveFeed")
    @Transactional
    public List<FeedDTO> retrieveFeed() {

        List<FeedDTO> result = new ArrayList<>();
        List<Map<String, String>> medias = pesquisaService.retrieveMeanTimeByProcess(Period.ofYears(-1));
        for (Map<String, String> mediaPorProcesso : medias) {
            String sigla = mediaPorProcesso.get("SIGLA");
            BigDecimal media = new BigDecimal(mediaPorProcesso.get("MEAN"));
            List<Map<String, String>> instancias = instanceDao.retrieveAllDelayedBySigla(sigla, media);

            for (Map<String, String> instancia : instancias) {
                result.add(new FeedDTO(mediaPorProcesso.get("NOME"), instancia.get("DESCRICAO"), new BigDecimal(instancia.get("DIAS")), media));
            }
        }

        result.sort((f1, f2) -> f2.getTempoDecorrido().subtract(f2.getMedia()).compareTo(
                                f1.getTempoDecorrido().subtract(f1  .getMedia()))
        );

        return result;
    }
}
