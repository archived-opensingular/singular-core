/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.studio.core.view;

import org.opensingular.lib.commons.lambda.IBiFunction;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.menu.ItemMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;

public class StudioMenuEntry extends ItemMenuEntry {

    private final IBiFunction<String, MenuEntry, StudioContent> contentFactory;
    private final boolean                                       withMenu;

    public StudioMenuEntry(Icon icon, String name, IBiFunction<String, MenuEntry, StudioContent> contentFactory,
                           boolean withMenu) {
        super(icon, name);
        this.contentFactory = contentFactory;
        this.withMenu = withMenu;
    }

    @Override
    public StudioContent makeContent(String id) {
        return contentFactory.apply(id, this);
    }

    @Override
    public boolean isWithMenu() {
        return withMenu;
    }
}
