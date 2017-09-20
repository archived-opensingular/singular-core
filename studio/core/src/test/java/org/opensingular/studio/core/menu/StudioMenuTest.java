package org.opensingular.studio.core.menu;

import org.junit.Test;
import org.opensingular.lib.commons.ui.Icon;

import static org.junit.Assert.*;

public class StudioMenuTest {

    @Test
    public void testMenuWithTwoItens() throws Exception {
        StudioMenu studioMenu = new StudioMenu(null);
        studioMenu.add(new ItemMenuEntry(Icon.of("X"), "X", new HTTPEndpointMenuView("http://localhost/x")));
        studioMenu.add(new ItemMenuEntry(Icon.of("Y"), "Y", new HTTPEndpointMenuView("http://localhost/y")));
        assertTrue(studioMenu.getChildren().size() == 2);
        assertTrue(studioMenu.getChildren().get(0).getName().equals("X"));
        assertTrue(studioMenu.getChildren().get(1).getName().equals("Y"));
    }

    @Test
    public void testMenuWithTwoGroups() throws Exception {
        StudioMenu studioMenu = new StudioMenu(null);
        studioMenu.add(new GroupMenuEntry(Icon.of("X"), "X"));
        studioMenu.add(new GroupMenuEntry(Icon.of("Y"), "Y"));
        assertTrue(studioMenu.getChildren().size() == 2);
        assertTrue(studioMenu.getChildren().get(0).getName().equals("X"));
        assertTrue(studioMenu.getChildren().get(1).getName().equals("Y"));
    }

    @Test
    public void testMenuOneItemAndOneGroup() throws Exception {
        StudioMenu studioMenu = new StudioMenu(null);
        studioMenu.add(new GroupMenuEntry(Icon.of("Group"), "Group"));
        studioMenu.add(new ItemMenuEntry(Icon.of("Item"), "Item", new HTTPEndpointMenuView("http://localhost/item")));
        assertTrue(studioMenu.getChildren().size() == 2);
        assertTrue(studioMenu.getChildren().get(0) instanceof GroupMenuEntry);
        assertTrue(studioMenu.getChildren().get(1) instanceof ItemMenuEntry);
    }
}