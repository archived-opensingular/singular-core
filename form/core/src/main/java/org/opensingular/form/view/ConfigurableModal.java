package org.opensingular.form.view;

import org.opensingular.form.enums.ModalSize;

public interface ConfigurableModal<S extends SView> {

    default S largeSize(){
        setModalSize(ModalSize.LARGE);
        return (S) this;
    }

    default S autoSize(){
        setModalSize(ModalSize.FIT);
        return (S) this;
    }

    default S smallSize(){
        setModalSize(ModalSize.SMALL);
        return (S) this;
    }

    default S mediumSize(){
        setModalSize(ModalSize.NORMAL);
        return (S) this;
    }

    default S fullSize(){
        setModalSize(ModalSize.FULL);
        return (S) this;
    }

    ModalSize getModalSize();

    void setModalSize(ModalSize size);

}
