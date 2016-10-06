package org.opensingular.singular.server.commons.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FeaturePermissionEntityPK implements Serializable {

    @Column(name = "CO_FUNCIONALIDADE")
    private String feature;

    @Column(name = "CO_PERMISSAO")
    private String permission;

    @Column(name = "CO_MODULO_SINGULAR")
    private String module;



    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeaturePermissionEntityPK that = (FeaturePermissionEntityPK) o;

        if (!feature.equals(that.feature)) return false;
        if (!permission.equals(that.permission)) return false;
        return module.equals(that.module);

    }

    @Override
    public int hashCode() {
        int result = feature.hashCode();
        result = 31 * result + permission.hashCode();
        result = 31 * result + module.hashCode();
        return result;
    }
}
