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

public class SViewTree extends SView implements ConfigurableModal<SViewTree> {

    private String title = StringUtils.EMPTY;
    private boolean selectOnlyLeafs;
    private boolean showOnlyMatches = true;
    private boolean showOnlyMatchesChildren = true;
    private boolean open;

    private ModalSize size;

    public String getTitle() {
        return title;
    }

    public SViewTree setTitle(String title) {
        this.title = title;
        return this;
    }

    public SViewTree showOnlyMatchesChildren(boolean showOnlyMatchesChildren) {
        this.showOnlyMatchesChildren = showOnlyMatchesChildren;
        return this;
    }

    public SViewTree open(boolean open) {
        this.open = open;
        return this;
    }

    public SViewTree selectOnlyLeaf(boolean selectOnlyLeafs) {
        this.selectOnlyLeafs = selectOnlyLeafs;
        return this;
    }

    public boolean isSelectOnlyLeafs() {
        return selectOnlyLeafs;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isShowOnlyMatches() {
        return showOnlyMatches;
    }

    public boolean isShowOnlyMatchesChildren() {
        return showOnlyMatchesChildren;
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
