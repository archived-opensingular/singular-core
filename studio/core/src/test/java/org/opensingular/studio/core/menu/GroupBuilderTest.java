package org.opensingular.studio.core.menu;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.lib.commons.ui.Icon;

public class GroupBuilderTest {

    GroupBuilder groupBuilder;

    @Before
    public void setUp() throws Exception {
        groupBuilder = new GroupBuilder();
    }

    @Test
    public void shouldAllowAddAnotherGroups() throws Exception {
        groupBuilder.addGroup(Icon.of("icon"), "name");
        StudioMenu studioMenu = groupBuilder.build();
        Assert.assertThat(studioMenu.getChildren().get(0), Matchers.instanceOf(GroupMenuEntry.class));
    }

    @Test
    public void shouldAllowAddGroupOfGroups() throws Exception {
        GroupBuilder subGroupBuilder = groupBuilder.addGroup(Icon.of("icon"), "name");
        subGroupBuilder.addGroup(Icon.of("icon_2"), "nome_2");
        StudioMenu studioMenu = groupBuilder.build();
        Assert.assertEquals("nome_2", ((GroupMenuEntry) studioMenu.getChildren().get(0)).getChildren().get(0).getName());
    }


    @Test
    public void shouldAllowAddSingleEntry() throws Exception {
        groupBuilder.addItem(Icon.of("icon"), "name", null);
        StudioMenu studioMenu = groupBuilder.build();
        Assert.assertThat(studioMenu.getChildren().get(0), Matchers.instanceOf(ItemMenuEntry.class));
    }
}