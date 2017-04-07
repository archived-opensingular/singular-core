package org.opensingular.lib.wicket.util.model;

import static org.junit.Assert.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.Arrays;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.junit.Test;
import org.opensingular.lib.wicket.util.WicketUtilTester;

import com.google.common.collect.ImmutableMap;

public class NullOrEmptyModelTest {

    @Test
    public void test() {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IModel<Object> value = (IModel) $m.ofValue();

        NullOrEmptyModel model = new NullOrEmptyModel(value);
        assertTrue(model.getObject());

        value.setObject(1);
        assertFalse(model.getObject());
        assertTrue(model.not().getObject());

        value.setObject("");
        assertTrue(model.getObject());

        value.setObject("A");
        assertFalse(model.getObject());
        
        value.setObject(Arrays.asList());
        assertTrue(model.getObject());
        
        value.setObject(Arrays.asList('x'));
        assertFalse(model.getObject());
        
        value.setObject(ImmutableMap.of());
        assertTrue(model.getObject());
        
        value.setObject(ImmutableMap.of(1, 2));
        assertFalse(model.getObject());
        
        new WicketUtilTester();
        
        value.setObject(new Label("label"));
        assertTrue(model.getObject());
        
        value.setObject(new Label("label", "..."));
        assertFalse(model.getObject());
        
        model.detach();
    }

}
