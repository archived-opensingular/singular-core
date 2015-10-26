package br.net.mirante.singular.dao.form;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.wicket.model.IModel;

@Entity
@Table(name = "EXAMPLE_DATA")
@SuppressWarnings({"rawtypes", "serial"})
public class ExampleDataDTO implements IModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    private String key, type;
    @Lob private String xml;

    public ExampleDataDTO() {
    }

    public ExampleDataDTO(String key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
