package org.opensingular.form.context;

import java.io.Serializable;

import org.opensingular.form.SInstance;
import org.opensingular.form.view.SView;

public interface IFormBuildContext extends Serializable {

    <T extends SInstance> T  getCurrentInstance();
    
    IFormBuildContext getParent();
    
    SView getView();
    
    default boolean isRootContext() {
        return (this.getParent() == null);
    }
}
