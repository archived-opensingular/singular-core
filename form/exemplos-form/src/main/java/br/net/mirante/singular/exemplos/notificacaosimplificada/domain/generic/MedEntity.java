package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic;

import java.io.Serializable;

public interface MedEntity<ID extends Serializable> extends Serializable {

	ID getId();
	
	void setId(ID id);
}