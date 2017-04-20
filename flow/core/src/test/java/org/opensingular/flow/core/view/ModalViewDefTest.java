package org.opensingular.flow.core.view;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.net.ModalViewDef;

public class ModalViewDefTest {

    @Test
    public void modalViewDefTest(){
        ModalViewDef modal = ModalViewDef.of(50, 50);
        Assert.assertEquals(50, modal.getWidth());

        modal.setHeight(20);
        modal.setWidth(20);
        Assert.assertEquals(20, modal.getHeight());
        Assert.assertEquals(20, modal.getWidth());
    }
}
