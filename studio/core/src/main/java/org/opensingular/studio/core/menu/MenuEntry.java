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

package org.opensingular.studio.core.menu;

import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.util.StudioWicketUtils;
import org.opensingular.studio.core.view.StudioContent;
import org.opensingular.studio.core.view.StudioPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface MenuEntry extends Serializable {

    /**
     * @return The element icon
     */
    Icon getIcon();

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the parent
     */
    MenuEntry getParent();

    /**
     * Set the parent of the entry
     *
     * @param parent
     */
    void setParent(MenuEntry parent);

    /**
     * Get the studio content
     *
     * @return
     */
    default StudioContent makeContent(String id) {
        return null;
    }

    default boolean isWithMenu() {
        return true;
    }

    /**
     * Get currente menupath
     *
     * @return
     */
    default String getMenuPath() {
        List<String> paths = new ArrayList<>();
        MenuEntry    entry = this;
        while (entry != null) {
            paths.add(entry.getName());
            entry = entry.getParent();
        }
        return Lists.reverse(paths).stream()
                .map(i -> SingularUtil.convertToJavaIdentity(i, true).toLowerCase())
                .collect(Collectors.joining("/"));
    }

    default String getEndpoint(){
        return StudioWicketUtils.getMergedPathIntoURL(StudioPage.class, getMenuPath());
    }
}