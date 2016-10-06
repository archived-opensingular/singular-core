package org.opensingular.server.commons.persistence.entity;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_FUNCIONALIDADE_PETICAO")
public class FeaturePermissionEntity extends BaseEntity<FeaturePermissionEntityPK> {

    @EmbeddedId
    private FeaturePermissionEntityPK cod;

    @Override
    public FeaturePermissionEntityPK getCod() {
        return cod;
    }

    public void setCod(FeaturePermissionEntityPK cod) {
        this.cod = cod;
    }
}
