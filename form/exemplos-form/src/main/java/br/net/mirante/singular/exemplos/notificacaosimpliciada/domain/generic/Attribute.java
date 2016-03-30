package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.generic;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@XmlRootElement(name = "attribute", namespace = "http://www.anvisa.gov.br/reg-med/schema/beans")
@XmlType(name = "attribute")
public class Attribute implements Serializable, Comparable<Attribute> {

	/**
	 * Serial code version <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1277235576749673929L;

	/**
	 * Nome do atributo
	 */
	private String name;

	/**
	 * Valor do atributo
	 */
	private Object value;

	/**
	 * Order do atributo. Deve ser maior ou igual a 1. Default: 1
	 */
	private Integer order = 1;

	/**
	 * Informa se o atributo é requerido. Default: {@link Boolean#FALSE}
	 */
	private Boolean required = Boolean.FALSE;

	public Attribute(String name, Object value) {
		this(name);
		this.value = value;
	}

	public Attribute(String name, Integer order) {
		this(name, order, Boolean.FALSE);
	}

	public Attribute(String name, Integer order, Boolean required) {
		this(name);
		this.order = order;
		this.required = required;
	}

	public Attribute() {
		super();
	}

	public Attribute(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Boolean getRequired() {
		return required;
	}

	public Boolean isRequired() {
		return this.getRequired();
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj)
				|| (obj instanceof Attribute && this.getName() != null && this
						.getName().equals(((Attribute) obj).getName()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.getName() != null ? this.getName().hashCode() * 17 : super
				.hashCode();
	}

	@Override
	public int compareTo(final Attribute anotherAttribute) {
		final int thisVal = this.getOrder();
		final int anotherVal = anotherAttribute.getOrder();
		return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
}