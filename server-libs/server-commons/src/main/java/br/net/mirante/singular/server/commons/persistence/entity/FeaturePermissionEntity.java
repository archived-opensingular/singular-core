package br.net.mirante.singular.server.commons.persistence.entity;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_FUNCIONALIDADE_PETICAO")
public class FeaturePermissionEntity extends BaseEntity<String> {

    @Id
    @Column(name = "CO_FUNCIONALIDADE")
    private String cod;

    @Column(name = "CO_PERMISSAO")
    private String permission;

    @Override
    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
