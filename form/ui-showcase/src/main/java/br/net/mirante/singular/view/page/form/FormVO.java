package br.net.mirante.singular.view.page.form;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;

@SuppressWarnings("serial")
public class FormVO implements Serializable, IModel<String> {
	private String key;
	private MTipoComposto<?> value;

	public FormVO(String key, MTipoComposto<?> value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public MTipoComposto<?> getValue() {
		return value;
	}

	public void setValue(MTipoComposto<?> value) {
		this.value = value;
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
	}

	public String getObject() {
		return getKey();
	}

	public void setObject(String o) {
		setKey(o);
	}
	
}