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
