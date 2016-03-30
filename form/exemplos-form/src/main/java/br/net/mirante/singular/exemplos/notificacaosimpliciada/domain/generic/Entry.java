package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.generic;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "entry", namespace = "http://www.anvisa.gov.br/reg-med/schema/beans")
@XmlType(name = "entry")
public class Entry implements Serializable {

	/**
	 * Serial version <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2110575684642855510L;

	@XmlElement(name = "entity-type", required = true, nillable=false)	
	private Class<?> entityType;

	@XmlElement(name = "attributes")
	private Attributes entryAttributes;

	public Class<?> getEntityType() {
		return entityType;
	}

	/**
	 * Entidade manipulada tratada pelo caso de uso. Normalmente os casos de
	 * usos do tipo CRUD (Manter <xxxx>) tratam de uma única entidade por vez.
	 */
	public void setEntityType(Class<?> entityType) {
		this.entityType = entityType;
	}


	public Attributes getEntryAttributes() {
		return entryAttributes;
	}

	public void setEntryAttributes(Attributes entryAttributes) {
		this.entryAttributes = entryAttributes;
	}

	public void add(Attribute attribute) {
		if (entryAttributes == null){
			this.entryAttributes = new Attributes();
		}
		this.getEntryAttributes().add(attribute);
	}
	
	public void remove(Attribute attribute) {
		if (entryAttributes != null){			
			this.getEntryAttributes().getAttributes().remove(attribute);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Attribute> getAttributes() {
		return this.getEntryAttributes() != null ? this.getEntryAttributes()
				.getAttributes() : Collections.EMPTY_LIST;
	}

	public Attribute getAttribute(String name){
		return this.getAttributes().get(this.getAttributes().indexOf(new Attribute(name)));
	}
}