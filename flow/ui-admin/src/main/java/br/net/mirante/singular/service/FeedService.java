package br.net.mirante.singular.service;

import java.util.List;

import br.net.mirante.singular.flow.core.dto.IFeedDTO;

public interface FeedService {

    List<IFeedDTO> retrieveFeed();
}
