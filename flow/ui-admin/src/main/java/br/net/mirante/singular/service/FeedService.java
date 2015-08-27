package br.net.mirante.singular.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.FeedDTO;

@Service
public class FeedService {
	public List<FeedDTO> retrieveFeed(){
		return FeedDTO.populaTemporario(); //TODO colocar para fazer a consulta no banco
	}
}
