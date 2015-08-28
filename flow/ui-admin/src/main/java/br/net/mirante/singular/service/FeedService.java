package br.net.mirante.singular.service;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.DemandaDAO;
import br.net.mirante.singular.dao.FeedDTO;

@Service
public class FeedService {
	
	@Inject
	private DemandaDAO demandaDAO;
	
	public List<FeedDTO> retrieveFeedTemporario(){
		return FeedDTO.populaTemporario(); //TODO colocar para fazer a consulta no banco
	}
	
	@Transactional
	public void retrieveFeed(){
		demandaDAO.retrieveAll();
	}
}
