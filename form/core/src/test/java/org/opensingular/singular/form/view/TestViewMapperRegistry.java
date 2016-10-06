package org.opensingular.singular.form.view;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.opensingular.form.view.SView;
import org.opensingular.form.view.ViewMapperRegistry;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.form.type.country.brazil.STypeCPF;

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
        mapper.register(STypeDate.class, () -> "C");

        assertResult("A", STypeInteger.class, SView.class);
        assertResult("B", STypeString.class, SView.class);
        assertResult("B", STypeCPF.class, SView.class);
        assertResult("C", STypeDate.class, SView.class);
        assertResult(null, STypeComposite.class, SView.class);
    }

    @Test
    public void testBuscaEspecificoDepoisDefault() {
        mapper.register(STypeSimple.class, () -> "A");
        mapper.register(STypeString.class, () -> "B");
        mapper.register(STypeString.class, ViewX.class, () -> "D");
        mapper.register(STypeCNPJ.class, ViewX.class, () -> "E");
        mapper.register(STypeDate.class, () -> "C");

        assertResult("A", STypeInteger.class, SView.class);
        assertResult("B", STypeString.class, SView.class);
        assertResult("B", STypeCPF.class, SView.class);
        assertResult("B", STypeCNPJ.class, SView.class);
        assertResult("C", STypeDate.class, SView.class);
        assertResult(null, STypeComposite.class, SView.class);

        assertResult("A", STypeInteger.class, ViewX.class);
        assertResult("D", STypeString.class, ViewX.class);
        assertResult("D", STypeCPF.class, ViewX.class);
        assertResult("E", STypeCNPJ.class, ViewX.class);
        assertResult("C", STypeDate.class, ViewX.class);
        assertResult(null, STypeComposite.class, ViewX.class);

        assertResult("A", STypeInteger.class, ViewY.class);
        assertResult("B", STypeString.class, ViewY.class);
        assertResult("B", STypeCPF.class, ViewY.class);
        assertResult("B", STypeCNPJ.class, ViewY.class);
        assertResult("C", STypeDate.class, ViewY.class);
        assertResult(null, STypeComposite.class, ViewY.class);
    }

    @Test
    public void testAceitarViewDerivada() {
        mapper.register(STypeSimple.class, () -> "A");
        mapper.register(STypeString.class, () -> "B");
        mapper.register(STypeString.class, ViewY.class, () -> "D");
        mapper.register(STypeCNPJ.class, ViewY.class, () -> "E");
        mapper.register(STypeDate.class, () -> "C");

        assertResult("A", STypeInteger.class, SView.class);
        assertResult("B", STypeString.class, SView.class);
        assertResult("B", STypeCPF.class, SView.class);
        assertResult("B", STypeCNPJ.class, SView.class);
        assertResult("C", STypeDate.class, SView.class);
        assertResult(null, STypeComposite.class, SView.class);

        assertResult("A", STypeInteger.class, ViewX.class);
        assertResult("D", STypeString.class, ViewX.class);
        assertResult("D", STypeCPF.class, ViewX.class);
        assertResult("E", STypeCNPJ.class, ViewX.class);
        assertResult("C", STypeDate.class, ViewX.class);
        assertResult(null, STypeComposite.class, ViewX.class);

        assertResult("A", STypeInteger.class, ViewY.class);
        assertResult("D", STypeString.class, ViewY.class);
        assertResult("D", STypeCPF.class, ViewY.class);
        assertResult("E", STypeCNPJ.class, ViewY.class);
        assertResult("C", STypeDate.class, ViewY.class);
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

        assertResult("B", STypeString.class, SView.class);
        assertResult("B", STypeCPF.class, SView.class);
        assertResult("B", STypeCNPJ.class, SView.class);
        assertResult("B", STypeCEP.class, SView.class);

        assertResult("D", STypeString.class, ViewX.class);
        assertResult("D", STypeCPF.class, ViewX.class);
        assertResult("E", STypeCNPJ.class, ViewX.class);
        assertResult("G", STypeCEP.class, ViewX.class);

        assertResult("C", STypeString.class, ViewY.class);
        assertResult("C", STypeCPF.class, ViewY.class);
        assertResult("F", STypeCNPJ.class, ViewY.class);
        assertResult("G", STypeCEP.class, ViewY.class);
    }

    public static class ViewX extends SView {
    }

    public static class ViewY extends ViewX {

    }

    private void assertResult(String expected, Class<? extends SType> type, Class<? extends SView> view) {
        try {
            SDictionary dicionario = SDictionary.create();
            assertResult(expected, dicionario.newInstance(type), view.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw SingularUtil.propagate(e);
        }
    }

    private void assertResult(String expected, SInstance instance, SView view) {
        assertEquals(expected, mapper.getMapper(instance, view).orElse(null));

    }
}

