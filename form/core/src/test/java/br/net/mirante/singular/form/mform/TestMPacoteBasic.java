package br.net.mirante.singular.form.mform;

import org.junit.Assert;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import junit.framework.TestCase;

public class TestMPacoteBasic extends TestCase {

    public void testCargaSimples() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(MPacoteBasic.class);

//        dicionario.debug();

        MTipoInteger mtInt = dicionario.getTipo(MTipoInteger.class);
        Assert.assertEquals(Integer.valueOf(1), mtInt.converter("1"));
        Assert.assertEquals(Integer.valueOf(-1), mtInt.converter("-1"));
        Assert.assertEquals(Integer.valueOf(10), mtInt.converter("010"));
    }
}
