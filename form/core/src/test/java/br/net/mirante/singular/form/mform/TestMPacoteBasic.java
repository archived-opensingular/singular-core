package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.MIData;
import br.net.mirante.singular.form.mform.core.MTipoData;
import org.junit.Assert;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import junit.framework.TestCase;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class TestMPacoteBasic{

    @Test public void testCargaSimples() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(MPacoteBasic.class);

//        dicionario.debug();

        MTipoInteger mtInt = dicionario.getTipo(MTipoInteger.class);
        Assert.assertEquals(Integer.valueOf(1), mtInt.converter("1"));
        Assert.assertEquals(Integer.valueOf(-1), mtInt.converter("-1"));
        Assert.assertEquals(Integer.valueOf(10), mtInt.converter("010"));
    }

    @Test public void tipoDate(){
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(MPacoteBasic.class);

        MTipoData mData = dicionario.getTipo(MTipoData.class);
        MIData miData = mData.novaInstancia();
        miData.setValor("");
        assertThat(miData.getValor()).isNull();
    }
}
