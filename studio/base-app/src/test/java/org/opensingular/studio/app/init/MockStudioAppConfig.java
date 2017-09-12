package org.opensingular.studio.app.init;

import org.opensingular.studio.app.AbstractStudioAppConfig;
import org.opensingular.studio.app.menu.GroupMenuEntry;
import org.opensingular.studio.app.menu.ItemMenuEntry;
import org.opensingular.studio.app.menu.StudioMenu;

/**
 * Keep this file, is used in StudioWebAppinitializerTest
 */
public class MockStudioAppConfig extends AbstractStudioAppConfig {
    @Override
    public StudioMenu getAppMenu() {
        StudioMenu studioMenu = new StudioMenu(null);
        GroupMenuEntry mockGroup1 = studioMenu.add(new GroupMenuEntry(null, "Mock Group 1"));
        GroupMenuEntry mockGroup2 = studioMenu.add(new GroupMenuEntry(null, "Mock Group 2"));
        GroupMenuEntry mockGroup1_1 = mockGroup1.add(new GroupMenuEntry(null, "Mock Group 1 -> 1"));
        mockGroup1.add(new ItemMenuEntry(null, null,  null));
        mockGroup2.add(new ItemMenuEntry(null, null,  null));
        mockGroup1_1.add(new ItemMenuEntry(null, null,  null));
        return studioMenu;
    }
}
