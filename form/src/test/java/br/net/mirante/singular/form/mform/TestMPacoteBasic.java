package br.net.mirante.singular.form.mform;

import junit.framework.TestCase;

import org.junit.Assert;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MTipoInteger;

public class TestMPacoteBasic extends TestCase {

    public void testCargaSimples() {
        MDicionario dicionario = MDicionario.create();
        dicionario.carregarPacote(MPacoteBasic.class);

        dicionario.debug();

        MTipoInteger mtInt = dicionario.getTipo(MTipoInteger.class);
        Assert.assertEquals(Integer.valueOf(1), mtInt.converter("1"));
        Assert.assertEquals(Integer.valueOf(-1), mtInt.converter("-1"));
        Assert.assertEquals(Integer.valueOf(10), mtInt.converter("010"));

        MTipoComposto<MIComposto> mtPos = (MTipoComposto<MIComposto>) dicionario.getTipo("mform.basic.PosicaoTela");

        MIComposto miPos = mtPos.novaInstancia();
        miPos.setValor("lin", "1");
        miPos.setValor("col", "2");
        miPos.setValor("colSpan", "3");
        
        System.out.println(mtPos.getCampo("lin").getNome());

        miPos.debug();

        System.out.println(miPos.getDisplayString());
    }
}
