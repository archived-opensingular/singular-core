package org.opensingular.form.view;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class BlockTest {
    @Test
    public void blockTest(){
        Block block = new Block();

        block.setName("name");
        block.setTypes(new ArrayList<>());

        Assert.assertEquals("name", block.getName());
        Assert.assertEquals(0, block.getTypes().size());
    }
}
