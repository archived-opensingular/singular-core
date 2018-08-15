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

package org.opensingular.form.view.list;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.ui.Icon;

import java.io.Serializable;

public class ButtonAction implements Serializable {

    private IPredicate<SInstance> visibleFor;
    private String hint;
    private Icon icon;

    public ButtonAction(IPredicate<SInstance> visibleFor, String hint, Icon icon) {
        this.visibleFor = visibleFor;
        this.hint = hint;
        this.icon = icon;
    }

    public IPredicate<SInstance> getVisibleFor() {
        return visibleFor;
    }

    public String getHint() {
        return hint;
    }

    public Icon getIcon() {
        return icon;
    }

    public boolean isEnabled(SInstance instance) {
        return getVisibleFor() == null || getVisibleFor().test(instance);
    }
}
