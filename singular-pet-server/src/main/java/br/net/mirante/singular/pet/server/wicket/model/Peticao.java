package br.net.mirante.singular.pet.server.wicket.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.wicket.model.IModel;

@Entity
@Table(name = "TB_PETICAO")
@SuppressWarnings({"rawtypes", "serial"})
//TODO remover o IModel
public class Peticao implements IModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String key;

    @Column
    private String type;

    @Lob
    private String xml;

    @Lob
    private String annotations;

    public Peticao() {}

    public Peticao(String key) {
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

    public String getAnnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void detach() {}

    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public void setObject(Object arg0) {}
}
