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

package org.opensingular.form.decorator.action;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opensingular.form.decorator.action.SInstanceAction.ActionType;
import org.opensingular.form.decorator.action.SInstanceAction.FormDelegate;
import org.opensingular.lib.commons.ref.Out;

public class SInstanceActionTest {

    @Test
    public void test() {
        SInstanceAction action = new SInstanceAction(ActionType.NORMAL)
            .setType(ActionType.LINK)
            .setType(ActionType.PRIMARY)
            .setType(ActionType.DANGER)
            .setText("text")
            .setIcon(SIcon.resolve("star"))
            .setDescription("description")
            .setPosition(0)
            .setSecondary(true)
            .setActionHandler((a, i, d) -> {
                d.showMessage("title", "msg");
                Out<FormDelegate> fd = new Out<>();
                d.openForm(fd, "", null, null, null);
                fd.get().close();
                d.getInstanceRef();
                d.getInternalContext(null);
            });

        assertEquals(ActionType.DANGER, action.getType());
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
