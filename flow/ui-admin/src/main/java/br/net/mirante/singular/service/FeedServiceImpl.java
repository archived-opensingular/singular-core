package br.net.mirante.singular.service;

import java.math.BigDecimal;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.InstanceDAO;
import br.net.mirante.singular.dto.FeedDTO;

@Singleton
@Service("feedService")
public class FeedServiceImpl implements FeedService {

    @Inject
    private InstanceDAO instanceDao;

    @Inject
    private PesquisaService pesquisaService;

    @Override
    @Transactional
    @Cacheable(value = "retrieveFeed", cacheManager = "cacheManager")
    public List<FeedDTO> retrieveFeed(String processCode, Set<String> processCodeWithAccess) {
        List<FeedDTO> result = new ArrayList<>();
        List<Map<String, String>> medias = pesquisaService.retrieveMeanTimeByProcess(Period.ofYears(-1), processCode, processCodeWithAccess);
        for (Map<String, String> mediaPorProcesso : medias) {
            String sigla = mediaPorProcesso.get("SIGLA");
            BigDecimal media = new BigDecimal(mediaPorProcesso.get("MEAN"));
            List<Map<String, String>> instancias = instanceDao.retrieveAllDelayedBySigla(sigla, media);
            result.addAll(instancias.stream()
                    .map(instancia -> new FeedDTO(mediaPorProcesso.get("NOME"), instancia.get("DESCRICAO"),
                            new BigDecimal(instancia.get("DIAS")), media)).collect(Collectors.toList()));
            if (result.size() >= InstanceDAO.MAX_FEED_SIZE) {
                break;
            }
        }

        result.sort((f1, f2) -> f2.getTempoDecorrido().subtract(f2.getMedia()).compareTo(
                        f1.getTempoDecorrido().subtract(f1.getMedia()))
        );

        return result;
    }
}
