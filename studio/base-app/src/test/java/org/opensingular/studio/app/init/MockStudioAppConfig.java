package org.opensingular.studio.app.init;

import org.opensingular.studio.app.AbstractStudioAppConfig;
import org.opensingular.studio.core.menu.StudioMenu;

/**
 * Keep this file, is used in StudioWebAppinitializerTest
 */
public class MockStudioAppConfig extends AbstractStudioAppConfig {
    @Override
    public StudioMenu getAppMenu() {
        return new StudioMenu();
    }
}
