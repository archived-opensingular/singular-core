package br.net.mirante.singular.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.FeedDTO;

@Service
public class FeedService {

    public List<FeedDTO> retrieveFeedTemporario() {
        return FeedDTO.populaTemporario(); //TODO colocar para fazer a consulta no banco
    }

    @Transactional
    public List<FeedDTO> retrieveFeed() {
        return new ArrayList<>();
    }
}
