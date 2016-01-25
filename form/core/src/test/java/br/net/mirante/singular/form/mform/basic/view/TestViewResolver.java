package br.net.mirante.singular.form.mform.basic.view;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class TestViewResolver {

    private static final String textoTeste = "stringzonamuitolocabemgrandeparaevitarproblemascomarrayoutofboundsnessestestesunitariosaqui";

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static MTipoLista<?, ?> createSimpleList(PacoteBuilder pb, String name, Class<? extends MTipoSimples<?, ?>> type, int size,
                                                     Function<Integer, Object> valueProvider) {
        MTipoSimples<?, ?> simpleType = pb.createTipo(name, type);
        simpleType.withSelectionOf((Collection) repeate(valueProvider, size));
        return pb.createTipoListaOf("list" + name, simpleType);
    }

    private static <T> Collection<T> repeate(Function<Integer, T> valueSupplier, int size) {
        ArrayList<T> l = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            l.add(valueSupplier.apply(i));
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
        MTipoSimples<?, ?> tipoInteger = pb.createTipo("Integer", MTipoInteger.class).withSelectionOf(repeate(i -> i, 2).toArray(new Integer[]{}));
        MTipoSimples<?, ?> tipoDate = pb.createTipo("Date", MTipoData.class).withSelectionOf(repeate(i -> new Date(new Date().getTime() + 10 * i), 2).toArray(new Date[]{}));
        MTipoSimples<?, ?> tipoString = pb.createTipo("String", MTipoString.class).withSelectionOf(repeate(i -> textoTeste.substring(i), 2).toArray(new String[]{}));
        MTipoSimples<?, ?> tipoStringLarge = pb.createTipo("StringLarge", MTipoString.class).withSelectionOf(repeate(i -> textoTeste.substring(i), 5).toArray(new String[]{}));

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
        MTipoLista<?, ?> listInteger3 = createSimpleList(pb, "Integer3", MTipoInteger.class, 3, i -> 100 + i);
        MTipoLista<?, ?> listInteger10 = createSimpleList(pb, "Integer10", MTipoInteger.class, 10, i -> 100 + i);
        MTipoLista<?, ?> listInteger20 = createSimpleList(pb, "Integer20", MTipoInteger.class, 20, i -> 100 + i);

        MTipoLista<?, ?> listString3 = createSimpleList(pb, "String3", MTipoString.class, 3, i -> textoTeste.substring(i));
        MTipoLista<?, ?> listString10 = createSimpleList(pb, "String10", MTipoString.class, 10, i -> textoTeste.substring(i));
        MTipoLista<?, ?> listString20 = createSimpleList(pb, "String20", MTipoString.class, 20, i -> textoTeste.substring(i));

        assertView(MSelecaoMultiplaPorCheckView.class, listInteger3);
        assertView(MSelecaoMultiplaPorSelectView.class, listInteger10);
        assertView(MSelecaoMultiplaPorPicklistView.class, listInteger20);

        assertView(MSelecaoMultiplaPorCheckView.class, listString3);
        assertView(MSelecaoMultiplaPorSelectView.class, listString10);
        assertView(MSelecaoMultiplaPorPicklistView.class, listString20);
    }

}
