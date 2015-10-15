package br.net.mirante.singular.dao.form;

import org.apache.wicket.model.IModel;

@SuppressWarnings("rawtypes")
public class ExampleDateDTO implements IModel {
	
	private String key, xml;

	public ExampleDateDTO(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void setObject(Object arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
