package org.opensingular.form.decorator.action;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opensingular.form.decorator.action.SInstanceAction.ActionType;

public class SInstanceActionTest {

    @Test
    public void test() {
        SInstanceAction action = new SInstanceAction(ActionType.NORMAL)
            .setType(ActionType.LINK)
            .setType(ActionType.PRIMARY)
            .setType(ActionType.WARNING)
            .setText("text")
            .setIcon(SIcon.resolve("star"))
            .setDescription("description")
            .setPosition(0)
            .setSecondary(true)
            .setActionHandler((a, i, d) -> {
                d.showMessage("title", "msg");
                d.openForm("", null, null);
                d.closeForm(null);
                d.getInstanceRef();
                d.getInternalContext(null);
            });

        assertEquals(ActionType.WARNING, action.getType());
        assertEquals("text", action.getText());
        assertEquals("description", action.getDescription());
        assertEquals(0, action.getPosition());
        assertTrue(action.isSecondary());

        MockSInstanceActionDelegate delegate = new MockSInstanceActionDelegate();
        action.getActionHandler().onAction(action, null, delegate);
        assertEquals(1, delegate._showMessageCount);
        assertEquals(1, delegate._openFormCount);
        assertEquals(1, delegate._closeFormCount);
        assertEquals(1, delegate._getInstanceRefCount);
        assertEquals(1, delegate._getInternalContextCount);
    }
}
