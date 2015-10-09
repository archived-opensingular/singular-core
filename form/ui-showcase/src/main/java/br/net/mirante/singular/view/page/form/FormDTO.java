package br.net.mirante.singular.view.page.form;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FormDTO implements Serializable {
	String key, value;

	public FormDTO(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}