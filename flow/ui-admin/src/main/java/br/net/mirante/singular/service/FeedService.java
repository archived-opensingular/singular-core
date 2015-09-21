package br.net.mirante.singular.service;

import java.util.List;

import br.net.mirante.singular.dao.FeedDTO;

public interface FeedService {

    List<FeedDTO> retrieveFeed(String processCode);
}
