package br.net.mirante.singular.form.mform.basic.view;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeCEP;
import br.net.mirante.singular.form.mform.util.comuns.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;

public class TestViewMapperRegistry {

    private ViewMapperRegistry<String> mapper;

    @Before
    public void setup() {
        mapper = new ViewMapperRegistry<>();
    }

    @Test
    public void testBuscaHierarquiaTipo() {
        mapper.register(STypeSimple.class, () -> "A");
        mapper.register(STypeString.class, () -> "B");
        mapper.register(STypeData.class, () -> "C");

        assertResult("A", STypeInteger.class, MView.class);
        assertResult("B", STypeString.class, MView.class);
        assertResult("B", STypeCPF.class, MView.class);
        assertResult("C", STypeData.class, MView.class);
        assertResult(null, STypeComposite.class, MView.class);
    }

    @Test
    public void testBuscaEspecificoDepoisDefault() {
        mapper.register(STypeSimple.class, () -> "A");
        mapper.register(STypeString.class, () -> "B");
        mapper.register(STypeString.class, ViewX.class, () -> "D");
        mapper.register(STypeCNPJ.class, ViewX.class, () -> "E");
        mapper.register(STypeData.class, () -> "C");

        assertResult("A", STypeInteger.class, MView.class);
        assertResult("B", STypeString.class, MView.class);
        assertResult("B", STypeCPF.class, MView.class);
        assertResult("B", STypeCNPJ.class, MView.class);
        assertResult("C", STypeData.class, MView.class);
        assertResult(null, STypeComposite.class, MView.class);

        assertResult("A", STypeInteger.class, ViewX.class);
        assertResult("D", STypeString.class, ViewX.class);
        assertResult("D", STypeCPF.class, ViewX.class);
        assertResult("E", STypeCNPJ.class, ViewX.class);
        assertResult("C", STypeData.class, ViewX.class);
        assertResult(null, STypeComposite.class, ViewX.class);

        assertResult("A", STypeInteger.class, ViewY.class);
        assertResult("B", STypeString.class, ViewY.class);
        assertResult("B", STypeCPF.class, ViewY.class);
        assertResult("B", STypeCNPJ.class, ViewY.class);
        assertResult("C", STypeData.class, ViewY.class);
        assertResult(null, STypeComposite.class, ViewY.class);
    }

    @Test
    public void testAceitarViewDerivada() {
        mapper.register(STypeSimple.class, () -> "A");
        mapper.register(STypeString.class, () -> "B");
        mapper.register(STypeString.class, ViewY.class, () -> "D");
        mapper.register(STypeCNPJ.class, ViewY.class, () -> "E");
        mapper.register(STypeData.class, () -> "C");

        assertResult("A", STypeInteger.class, MView.class);
        assertResult("B", STypeString.class, MView.class);
        assertResult("B", STypeCPF.class, MView.class);
        assertResult("B", STypeCNPJ.class, MView.class);
        assertResult("C", STypeData.class, MView.class);
        assertResult(null, STypeComposite.class, MView.class);

        assertResult("A", STypeInteger.class, ViewX.class);
        assertResult("D", STypeString.class, ViewX.class);
        assertResult("D", STypeCPF.class, ViewX.class);
        assertResult("E", STypeCNPJ.class, ViewX.class);
        assertResult("C", STypeData.class, ViewX.class);
        assertResult(null, STypeComposite.class, ViewX.class);

        assertResult("A", STypeInteger.class, ViewY.class);
        assertResult("D", STypeString.class, ViewY.class);
        assertResult("D", STypeCPF.class, ViewY.class);
        assertResult("E", STypeCNPJ.class, ViewY.class);
        assertResult("C", STypeData.class, ViewY.class);
        assertResult(null, STypeComposite.class, ViewY.class);
    }

    @Test
    public void testPrioridadeDeAcordoComDerivacao() {
        mapper.register(STypeSimple.class, () -> "A");
        mapper.register(STypeString.class, () -> "B");
        mapper.register(STypeString.class, ViewY.class, () -> "C");
        mapper.register(STypeString.class, ViewX.class, () -> "D");
        // Adiciona em ondem invertida do anterior para ver se dá diferença
        mapper.register(STypeCNPJ.class, ViewX.class, () -> "E");
        mapper.register(STypeCNPJ.class, ViewY.class, () -> "F");
        mapper.register(STypeCEP.class, ViewY.class, () -> "G");

        assertResult("B", STypeString.class, MView.class);
        assertResult("B", STypeCPF.class, MView.class);
        assertResult("B", STypeCNPJ.class, MView.class);
        assertResult("B", STypeCEP.class, MView.class);

        assertResult("D", STypeString.class, ViewX.class);
        assertResult("D", STypeCPF.class, ViewX.class);
        assertResult("E", STypeCNPJ.class, ViewX.class);
        assertResult("G", STypeCEP.class, ViewX.class);

        assertResult("C", STypeString.class, ViewY.class);
        assertResult("C", STypeCPF.class, ViewY.class);
        assertResult("F", STypeCNPJ.class, ViewY.class);
        assertResult("G", STypeCEP.class, ViewY.class);
    }

    public static class ViewX extends MView {
    }

    public static class ViewY extends ViewX {

    }

    private void assertResult(String expected, Class<? extends SType> type, Class<? extends MView> view) {
        try {
            SDictionary dicionario = SDictionary.create();
            assertResult(expected, dicionario.newInstance(type), view.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
    }

    private void assertResult(String expected, SInstance instance, MView view) {
        assertEquals(expected, mapper.getMapper(instance, view).orElse(null));

    }
}

