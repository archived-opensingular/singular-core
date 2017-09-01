package org.opensingular.form.type.country.brazil;

import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.provider.ProviderContext;

import java.io.Serializable;
import java.util.List;

import static org.junit.Assert.*;


public class STypeUFTest {


    @Test
    public void test() throws Exception {
        SIComposite uf = SDictionary.create().newInstance(STypeUF.class);
        assertTrue(uf.isEmptyOfData());
        List values = uf.asAtrProvider().getProvider().load(ProviderContext.of(uf));
        assertFalse(values.isEmpty());
        uf.asAtrProvider().getConverter().fillInstance(uf, (Serializable) values.get(0));
        assertFalse(uf.isEmptyOfData());
    }
}