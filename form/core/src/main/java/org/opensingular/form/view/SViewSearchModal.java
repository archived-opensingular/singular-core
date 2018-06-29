/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.view;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.enums.ModalSize;

/**
 * The type S view search modal.
 */
public class SViewSearchModal extends SView implements ConfigurableModal<SViewSearchModal> {

    private String title = StringUtils.EMPTY;
    private Integer pageSize = 5;
    private ModalSize size;
    private boolean enableColumnClick;

    /**
     * Instantiates a new S view search modal.
     */
    public SViewSearchModal() {
    }

    /**
     * Title for modal
     *
     * @param title the title
     * @return the s view search modal
     */
    public SViewSearchModal title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Set row per page for table inside modal.
     *
     * @param pageSize the page size
     * @return the s view search modal
     */
    public SViewSearchModal withPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets page size.
     *
     * @return the page size
     */
    public Integer getPageSize() {
        return pageSize;
    }

    @Override
    public ModalSize getModalSize() {
        return size;
    }

    @Override
    public void setModalSize(ModalSize size) {
        this.size = size;
    }

    public SViewSearchModal enableColumnClick(boolean enabled) {
        this.enableColumnClick = enabled;
        return this;
    }

    /**
     * Action to enabled the click on the cell's of the table.
     * The clicked will do the same action of the selection action.
     *
     * @return true will enabled, false will not.
     */
    public boolean isEnableColumnClick() {
        return enableColumnClick;
    }
}

