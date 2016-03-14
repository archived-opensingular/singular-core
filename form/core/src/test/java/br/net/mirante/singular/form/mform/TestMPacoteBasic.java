package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.SIDate;
import br.net.mirante.singular.form.mform.core.STypeDate;
import org.junit.Assert;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeInteger;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class TestMPacoteBasic{

    @Test public void testCargaSimples() {
        SDictionary dicionario = SDictionary.create();
        dicionario.loadPackage(SPackageBasic.class);

//        dicionario.debug();

        STypeInteger mtInt = dicionario.getType(STypeInteger.class);
        Assert.assertEquals(Integer.valueOf(1), mtInt.convert("1"));
        Assert.assertEquals(Integer.valueOf(-1), mtInt.convert("-1"));
        Assert.assertEquals(Integer.valueOf(10), mtInt.convert("010"));
    }

    @Test public void tipoDate(){
        SDictionary dicionario = SDictionary.create();
        dicionario.loadPackage(SPackageBasic.class);

        STypeDate mData = dicionario.getType(STypeDate.class);
        SIDate miData = mData.newInstance();
        miData.setValue("");
        assertThat(miData.getValue()).isNull();
    }
}
