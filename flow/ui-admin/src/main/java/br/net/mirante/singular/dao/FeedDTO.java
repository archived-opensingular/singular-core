package br.net.mirante.singular.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.util.wicket.resource.FeedIcon;

public class FeedDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String descricao;
	private String tempoAtraso;
	private FeedIcon feedIconColor;

	public FeedDTO(String descricao, String tempoAtraso, FeedIcon feedIconColor) {
		this.descricao = descricao;
		this.tempoAtraso = tempoAtraso;
		this.feedIconColor = feedIconColor;
	}
	
	public static List<FeedDTO> populaTemporario(){
		List<FeedDTO> feeds = new ArrayList<>();
		feeds.add(new FeedDTO("Deve ser refeito", "2 dias", FeedIcon.success));
		feeds.add(new FeedDTO("Mirante 00", "1 mês", FeedIcon.padrao));
		feeds.add(new FeedDTO("Feed mais 2", "5 horas", FeedIcon.info));
		feeds.add(new FeedDTO("Novo feed", "2 semanas", FeedIcon.warning));
		feeds.add(new FeedDTO("Mais um feed", "24 horas", FeedIcon.danger));
		
		return feeds;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public FeedIcon getFeedIconColor() {
		return feedIconColor;
	}

	public void setFeedIconColor(FeedIcon feedIconColor) {
		this.feedIconColor = feedIconColor;
	}

	public String getTempoAtraso() {
		return tempoAtraso;
	}

	public void setTempoAtraso(String tempoAtraso) {
		this.tempoAtraso = tempoAtraso;
	}
	public String getIconSymbol(){
		return "fa fa-bell-o"; // METODO para mostrar um icone, e deixar aparecendo, deve ser posto como condicao do tempo, e deve ir pra camada de apresentacao
	}
}
