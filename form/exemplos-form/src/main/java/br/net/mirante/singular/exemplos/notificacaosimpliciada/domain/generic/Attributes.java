package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.generic;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "")
@XmlRootElement
public class Attributes implements Serializable {

	/**
	 * Serial code version <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1532199025282933920L;

	private List<Attribute> attribute;

	public boolean add(final Attribute attribute) {
		return this.getAttributes().add(attribute);
	}

	@XmlElement(name = "attribute")
	public List<Attribute> getAttributes() {
		if (attribute == null){
			attribute = new LinkedList<Attribute>();
		}
		return attribute;
	}
	
	@Override
	public String toString() { 
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
}