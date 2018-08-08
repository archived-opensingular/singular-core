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

package org.opensingular.form.flatview;

import org.opensingular.form.SFormUtil;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

public class FlatViewContext {

    private final SInstance instance;
    private final boolean withoutTitle;
    private final boolean renderIfEmpty;

    public FlatViewContext(SInstance instance) {
        this.instance = instance;
        this.withoutTitle = false;
        this.renderIfEmpty = false;
    }

    public FlatViewContext(SInstance instance, boolean withoutTitle) {
        this.instance = instance;
        this.withoutTitle = withoutTitle;
        this.renderIfEmpty = false;
    }

    public FlatViewContext(SInstance instance, boolean withoutTitle, boolean renderIfEmpty) {
        this.instance = instance;
        this.withoutTitle = withoutTitle;
        this.renderIfEmpty = renderIfEmpty;
    }

    @SuppressWarnings("unchecked")
    public <T extends SInstance> T getInstanceAs(Class<T> tClass) {
        return (T) instance;
    }

    public SInstance getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public String getLabel() {
        String label = instance.asAtr().getLabel();
        if (label == null) {
            label = SFormUtil.getTypeLabel((Class<? extends SType<?>>) instance.getType().getClass()).orElse(null);
        }
        if (label == null) {
            label = instance.getName();
        }
        return label;
    }

    public boolean shouldRender() {
        return instance.asAtr().isVisible()
                && instance.asAtr().isExists()
                && (renderIfEmpty || !instance.isEmptyOfData());
    }

    public boolean isWithoutTitle() {
        return withoutTitle;
    }

}