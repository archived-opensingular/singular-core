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

package org.opensingular.studio.app.init;

import org.opensingular.studio.app.config.AbstractStudioAppConfig;
import org.opensingular.studio.core.menu.GroupMenuEntry;
import org.opensingular.studio.core.menu.MenuView;
import org.opensingular.studio.core.menu.StudioMenu;
import org.opensingular.studio.core.menu.UrlMenuEntry;

/**
 * Keep this file, is used in StudioWebAppinitializerTest
 */
public class MockStudioAppConfig extends AbstractStudioAppConfig {
    @Override
    public StudioMenu getAppMenu() {
        StudioMenu     studioMenu   = new StudioMenu(null);
        GroupMenuEntry mockGroup1   = studioMenu.add(new GroupMenuEntry(null, "Mock Group 1", MenuView.SIDEBAR));
        GroupMenuEntry mockGroup2   = studioMenu.add(new GroupMenuEntry(null, "Mock Group 2", MenuView.SIDEBAR));
        GroupMenuEntry mockGroup1_1 = mockGroup1.add(new GroupMenuEntry(null, "Mock Group 1 -> 1", MenuView.SIDEBAR));
        mockGroup1.add(new UrlMenuEntry(null, null, null));
        mockGroup2.add(new UrlMenuEntry(null, null, null));
        mockGroup1_1.add(new UrlMenuEntry(null, null, null));
        return studioMenu;
    }
}
