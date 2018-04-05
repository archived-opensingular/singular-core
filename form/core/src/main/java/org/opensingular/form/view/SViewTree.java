package org.opensingular.form.view;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.enums.ModalSize;

public class SViewTree extends SView implements ConfigurableModal<SViewTree> {

    private String title = StringUtils.EMPTY;
    private boolean onlyLeafSelect;
    private boolean open;

    private ModalSize size;

    public String getTitle() {
        return title;
    }

    public SViewTree setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean isOnlyLeafSelect() {
        return onlyLeafSelect;
    }

    public SViewTree onlyLeafSelect(boolean onlyLeafSelect) {
        this.onlyLeafSelect = onlyLeafSelect;
        return this;
    }

    public boolean isOpen() {
        return open;
    }

    public SViewTree open(boolean open) {
        this.open = open;
        return this;
    }

    @Override
    public ModalSize getModalSize() {
        return size;
    }

    @Override
    public void setModalSize(ModalSize size) {
        this.size = size;
    }
}
