package br.net.mirante.singular.form.mform.basic.view;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class TestViewResolver {

    @Test
    public void testBasicView() {
        assertView(null, MTipoBoolean.class);
        assertView(null, MTipoString.class);
        assertView(null, MTipoInteger.class);
        assertView(null, MTipoString.class);
        assertView(null, MTipoComposto.class);
        assertView(MPanelListaView.class, MTipoLista.class);
    }

    @Test
    public void testTipoSimplesSelectOf() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        MTipoSimples<?, ?> tipoInteger = pb.createTipo("Integer", MTipoInteger.class).withSelectionOf(repeate(1, 2).toArray(new Integer[]{}));
        MTipoSimples<?, ?> tipoDate = pb.createTipo("Date", MTipoData.class).withSelectionOf(repeate(new Date(), 2).toArray(new Date[]{}));
        MTipoSimples<?, ?> tipoString = pb.createTipo("String", MTipoString.class).withSelectionOf(repeate("A", 2).toArray(new String[]{}));
        MTipoSimples<?, ?> tipoStringLarge = pb.createTipo("StringLarge", MTipoString.class).withSelectionOf(repeate("A", 5).toArray(new String[]{}));

        assertView(MSelecaoPorSelectView.class, tipoInteger);
        assertView(MSelecaoPorSelectView.class, tipoDate);
        assertView(MSelecaoPorSelectView.class, tipoString);
        assertView(MSelecaoPorSelectView.class, tipoStringLarge);

        tipoInteger.withObrigatorio(true);
        tipoDate.withObrigatorio(true);
        tipoString.withObrigatorio(true);
        tipoStringLarge.withObrigatorio(true);

        assertView(MSelecaoPorRadioView.class, tipoInteger);
        assertView(MSelecaoPorRadioView.class, tipoDate);
        assertView(MSelecaoPorRadioView.class, tipoString);
        assertView(MSelecaoPorSelectView.class, tipoStringLarge);
    }

    @Test
    public void testListTipoSimplesSelectOf() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        MTipoLista<?, ?> listInteger3 = createSimpleList(pb, "Integer3", MTipoInteger.class, 3, 100);
        MTipoLista<?, ?> listInteger10 = createSimpleList(pb, "Integer10", MTipoInteger.class, 10, 100);
        MTipoLista<?, ?> listInteger20 = createSimpleList(pb, "Integer20", MTipoInteger.class, 20, 100);

        MTipoLista<?, ?> listString3 = createSimpleList(pb, "String3", MTipoString.class, 3, "AA");
        MTipoLista<?, ?> listString10 = createSimpleList(pb, "String10", MTipoString.class, 10, "AA");
        MTipoLista<?, ?> listString20 = createSimpleList(pb, "String20", MTipoString.class, 20, "AA");

        assertView(MSelecaoMultiplaPorCheckView.class, listInteger3);
        assertView(MSelecaoMultiplaPorSelectView.class, listInteger10);
        assertView(MSelecaoMultiplaPorPicklistView.class, listInteger20);

        assertView(MSelecaoMultiplaPorCheckView.class, listString3);
        assertView(MSelecaoMultiplaPorSelectView.class, listString10);
        assertView(MSelecaoMultiplaPorPicklistView.class, listString20);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static MTipoLista<?, ?> createSimpleList(PacoteBuilder pb, String name, Class<? extends MTipoSimples<?, ?>> type, int size,
            Object value) {
        MTipoSimples<?, ?> simpleType = pb.createTipo(name, type);
        simpleType.withSelectionOf((Collection) repeate(value, size));
        return pb.createTipoListaOf("list" + name, simpleType);
    }

    private static <T> Collection<T> repeate(T value, int size) {
        ArrayList<T> l = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            l.add(value);
        }
        return l;
    }

    @SuppressWarnings("unchecked")
    private static void assertView(Class<?> expectedView, Class<?> type) {
        MDicionario dicionario = MDicionario.create();
        assertView(expectedView, dicionario.getTipo((Class<MTipo<?>>) type));
    }

    private static void assertView(Class<?> expectedView, MTipo<?> newInstance) {
        assertView(expectedView, newInstance.novaInstancia());
    }

    private static void assertView(Class<?> expectedView, MInstancia instance) {
        MView view = ViewResolver.resolve(instance);
        if (expectedView == null && view == MView.DEFAULT) {
            return;
        }
        assertEquals(expectedView, view.getClass());
    }

}
