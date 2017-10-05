package org.opensingular.studio.core.menu;

import org.junit.Test;
import org.opensingular.lib.commons.ui.Icon;

import static org.junit.Assert.assertTrue;

public class StudioMenuTest {

    @Test
    public void testMenuWithTwoItens() throws Exception {
        StudioMenu studioMenu = new StudioMenu(null);
        studioMenu.add(new UrlMenuEntry(Icon.of("X"), "X", "http://localhost/x"));
        studioMenu.add(new UrlMenuEntry(Icon.of("Y"), "Y", "http://localhost/y"));
        assertTrue(studioMenu.getChildren().size() == 2);
        assertTrue(studioMenu.getChildren().get(0).getName().equals("X"));
        assertTrue(studioMenu.getChildren().get(1).getName().equals("Y"));
    }

    @Test
    public void testMenuWithTwoGroups() throws Exception {
        StudioMenu studioMenu = new StudioMenu(null);
        studioMenu.add(new GroupMenuEntry(Icon.of("X"), "X", MenuView.PORTAL));
        studioMenu.add(new GroupMenuEntry(Icon.of("Y"), "Y", MenuView.PORTAL));
        assertTrue(studioMenu.getChildren().size() == 2);
        assertTrue(studioMenu.getChildren().get(0).getName().equals("X"));
        assertTrue(studioMenu.getChildren().get(1).getName().equals("Y"));
    }

    @Test
    public void testMenuOneItemAndOneGroup() throws Exception {
        StudioMenu studioMenu = new StudioMenu(null);
        studioMenu.add(new GroupMenuEntry(Icon.of("Group"), "Group", MenuView.PORTAL));
        studioMenu.add(new UrlMenuEntry(Icon.of("Item"), "Item", "http://localhost/item"));
        assertTrue(studioMenu.getChildren().size() == 2);
        assertTrue(studioMenu.getChildren().get(0) instanceof GroupMenuEntry);
        assertTrue(studioMenu.getChildren().get(1) instanceof ItemMenuEntry);
    }
}