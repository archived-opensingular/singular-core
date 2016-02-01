package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.SIData;
import br.net.mirante.singular.form.mform.core.STypeData;
import org.junit.Assert;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeInteger;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class TestMPacoteBasic{

    @Test public void testCargaSimples() {
        SDictionary dicionario = SDictionary.create();
        dicionario.carregarPacote(SPackageBasic.class);

//        dicionario.debug();

        STypeInteger mtInt = dicionario.getTipo(STypeInteger.class);
        Assert.assertEquals(Integer.valueOf(1), mtInt.converter("1"));
        Assert.assertEquals(Integer.valueOf(-1), mtInt.converter("-1"));
        Assert.assertEquals(Integer.valueOf(10), mtInt.converter("010"));
    }

    @Test public void tipoDate(){
        SDictionary dicionario = SDictionary.create();
        dicionario.carregarPacote(SPackageBasic.class);

        STypeData mData = dicionario.getTipo(STypeData.class);
        SIData miData = mData.novaInstancia();
        miData.setValor("");
        assertThat(miData.getValor()).isNull();
    }
}
