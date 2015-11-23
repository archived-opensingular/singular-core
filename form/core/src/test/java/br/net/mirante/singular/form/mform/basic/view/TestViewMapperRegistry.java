package br.net.mirante.singular.form.mform.basic.view;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCEP;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;

public class TestViewMapperRegistry {

    private ViewMapperRegistry<String> mapper;

    @Before
    public void setup() {
        mapper = new ViewMapperRegistry<>();
    }

    @Test
    public void testBuscaHierarquiaTipo() {
        mapper.register(MTipoSimples.class, () -> "A");
        mapper.register(MTipoString.class, () -> "B");
        mapper.register(MTipoData.class, () -> "C");

        assertResult("A", MTipoInteger.class, MView.class);
        assertResult("B", MTipoString.class, MView.class);
        assertResult("B", MTipoCPF.class, MView.class);
        assertResult("C", MTipoData.class, MView.class);
        assertResult(null, MTipoComposto.class, MView.class);
    }

    @Test
    public void testBuscaEspecificoDepoisDefault() {
        mapper.register(MTipoSimples.class, () -> "A");
        mapper.register(MTipoString.class, () -> "B");
        mapper.register(MTipoString.class, ViewX.class, () -> "D");
        mapper.register(MTipoCNPJ.class, ViewX.class, () -> "E");
        mapper.register(MTipoData.class, () -> "C");

        assertResult("A", MTipoInteger.class, MView.class);
        assertResult("B", MTipoString.class, MView.class);
        assertResult("B", MTipoCPF.class, MView.class);
        assertResult("B", MTipoCNPJ.class, MView.class);
        assertResult("C", MTipoData.class, MView.class);
        assertResult(null, MTipoComposto.class, MView.class);

        assertResult("A", MTipoInteger.class, ViewX.class);
        assertResult("D", MTipoString.class, ViewX.class);
        assertResult("D", MTipoCPF.class, ViewX.class);
        assertResult("E", MTipoCNPJ.class, ViewX.class);
        assertResult("C", MTipoData.class, ViewX.class);
        assertResult(null, MTipoComposto.class, ViewX.class);

        assertResult("A", MTipoInteger.class, ViewY.class);
        assertResult("B", MTipoString.class, ViewY.class);
        assertResult("B", MTipoCPF.class, ViewY.class);
        assertResult("B", MTipoCNPJ.class, ViewY.class);
        assertResult("C", MTipoData.class, ViewY.class);
        assertResult(null, MTipoComposto.class, ViewY.class);
    }

    @Test
    public void testAceitarViewDerivada() {
        mapper.register(MTipoSimples.class, () -> "A");
        mapper.register(MTipoString.class, () -> "B");
        mapper.register(MTipoString.class, ViewY.class, () -> "D");
        mapper.register(MTipoCNPJ.class, ViewY.class, () -> "E");
        mapper.register(MTipoData.class, () -> "C");

        assertResult("A", MTipoInteger.class, MView.class);
        assertResult("B", MTipoString.class, MView.class);
        assertResult("B", MTipoCPF.class, MView.class);
        assertResult("B", MTipoCNPJ.class, MView.class);
        assertResult("C", MTipoData.class, MView.class);
        assertResult(null, MTipoComposto.class, MView.class);

        assertResult("A", MTipoInteger.class, ViewX.class);
        assertResult("D", MTipoString.class, ViewX.class);
        assertResult("D", MTipoCPF.class, ViewX.class);
        assertResult("E", MTipoCNPJ.class, ViewX.class);
        assertResult("C", MTipoData.class, ViewX.class);
        assertResult(null, MTipoComposto.class, ViewX.class);

        assertResult("A", MTipoInteger.class, ViewY.class);
        assertResult("D", MTipoString.class, ViewY.class);
        assertResult("D", MTipoCPF.class, ViewY.class);
        assertResult("E", MTipoCNPJ.class, ViewY.class);
        assertResult("C", MTipoData.class, ViewY.class);
        assertResult(null, MTipoComposto.class, ViewY.class);
    }

    @Test
    public void testPrioridadeDeAcordoComDerivacao() {
        mapper.register(MTipoSimples.class, () -> "A");
        mapper.register(MTipoString.class, () -> "B");
        mapper.register(MTipoString.class, ViewY.class, () -> "C");
        mapper.register(MTipoString.class, ViewX.class, () -> "D");
        // Adiciona em ondem invertida do anterior para ver se dá diferença
        mapper.register(MTipoCNPJ.class, ViewX.class, () -> "E");
        mapper.register(MTipoCNPJ.class, ViewY.class, () -> "F");
        mapper.register(MTipoCEP.class, ViewY.class, () -> "G");

        assertResult("B", MTipoString.class, MView.class);
        assertResult("B", MTipoCPF.class, MView.class);
        assertResult("B", MTipoCNPJ.class, MView.class);
        assertResult("B", MTipoCEP.class, MView.class);

        assertResult("D", MTipoString.class, ViewX.class);
        assertResult("D", MTipoCPF.class, ViewX.class);
        assertResult("E", MTipoCNPJ.class, ViewX.class);
        assertResult("G", MTipoCEP.class, ViewX.class);

        assertResult("C", MTipoString.class, ViewY.class);
        assertResult("C", MTipoCPF.class, ViewY.class);
        assertResult("F", MTipoCNPJ.class, ViewY.class);
        assertResult("G", MTipoCEP.class, ViewY.class);
    }

    public static class ViewX extends MView {
    }

    public static class ViewY extends ViewX {

    }

    private void assertResult(String expected, Class<? extends MTipo> type, Class<? extends MView> view) {
        try {
            MDicionario dicionario = MDicionario.create();
            assertResult(expected, dicionario.novaInstancia(type), view.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
    }

    private void assertResult(String expected, MInstancia instance, MView view) {
        assertEquals(expected, mapper.getMapper(instance, view).orElse(null));

    }
}

