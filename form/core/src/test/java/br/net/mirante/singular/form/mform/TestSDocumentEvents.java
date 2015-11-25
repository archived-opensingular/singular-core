package br.net.mirante.singular.form.mform;

import org.junit.Assert;
import org.junit.Ignore;

import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.mform.event.MInstanceValueChangeEvent;

@Ignore
public class TestSDocumentEvents extends TestCaseForm {

    public void testEventListener() {
        MDicionario dicionario = MDicionario.create();
        MIString root = dicionario.novaInstancia(MTipoString.class);
        SDocument doc = root.getDocument();

        IMInstanceListener.EventCollector globalCollector = new IMInstanceListener.EventCollector(e -> e instanceof MInstanceValueChangeEvent);
        IMInstanceListener.EventCollector collector1 = new IMInstanceListener.EventCollector(e -> e instanceof MInstanceValueChangeEvent);
        IMInstanceListener.EventCollector collector2 = new IMInstanceListener.EventCollector(e -> e instanceof MInstanceValueChangeEvent);

        doc.addInstanceListener(globalCollector);

        root.setValor("ABC");
        Assert.assertEquals(1, globalCollector.getEvents().size());

        doc.addInstanceListener(collector1);
        root.setValor("ABC");
        Assert.assertEquals(1, globalCollector.getEvents().size());
        Assert.assertEquals(0, collector1.getEvents().size());

        doc.addInstanceListener(collector2);

        root.setValor("CCC");
        root.setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, true);

        Assert.assertEquals(2, globalCollector.getEvents().size());
        Assert.assertEquals(1, collector1.getEvents().size());
        Assert.assertEquals(1, collector2.getEvents().size());
    }
    
    public void testUpdateAttributes() {
        MDicionario dicionario = MDicionario.create();
        MIString root = dicionario.novaInstancia(MTipoString.class);
        SDocument doc = root.getDocument();
        
        IMInstanceListener.EventCollector globalCollector = new IMInstanceListener.EventCollector(e -> e instanceof MInstanceValueChangeEvent);
        IMInstanceListener.EventCollector collector1 = new IMInstanceListener.EventCollector(e -> e instanceof MInstanceValueChangeEvent);
        IMInstanceListener.EventCollector collector2 = new IMInstanceListener.EventCollector(e -> e instanceof MInstanceValueChangeEvent);
        
        doc.addInstanceListener(globalCollector);
        
        root.setValor("ABC");
        Assert.assertEquals(1, globalCollector.getEvents().size());
        
        doc.addInstanceListener(collector1);
        root.setValor("ABC");
        Assert.assertEquals(1, globalCollector.getEvents().size());
        Assert.assertEquals(0, collector1.getEvents().size());
        
        doc.addInstanceListener(collector2);
        
        root.setValor("CCC");
        root.setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, true);
        
        Assert.assertEquals(2, globalCollector.getEvents().size());
        Assert.assertEquals(1, collector1.getEvents().size());
        Assert.assertEquals(1, collector2.getEvents().size());
    }
}
