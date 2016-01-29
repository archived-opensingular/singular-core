package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.STypeSimples;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class TestViewResolver {

    private static final String textoTeste = "stringzonamuitolocabemgrandeparaevitarproblemascomarrayoutofboundsnessestestesunitariosaqui";

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static STypeLista<?, ?> createSimpleList(PacoteBuilder pb, String name, Class<? extends STypeSimples<?, ?>> type, int size,
                                                     Function<Integer, Object> valueProvider) {
        STypeSimples<?, ?> simpleType = pb.createTipo(name, type);
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
        SDictionary dicionario = SDictionary.create();
        assertView(expectedView, dicionario.getTipo((Class<SType<?>>) type));
    }

    private static void assertView(Class<?> expectedView, SType<?> newInstance) {
        assertView(expectedView, newInstance.novaInstancia());
    }

    private static void assertView(Class<?> expectedView, SInstance instance) {
        MView view = ViewResolver.resolve(instance);
        if (expectedView == null && view == MView.DEFAULT) {
            return;
        }
        assertEquals(expectedView, view.getClass());
    }

    @Test
    public void testBasicView() {
        assertView(null, STypeBoolean.class);
        assertView(null, STypeString.class);
        assertView(null, STypeInteger.class);
        assertView(null, STypeString.class);
        assertView(null, STypeComposto.class);
        assertView(MPanelListaView.class, STypeLista.class);
    }

    @Test
    public void testTipoSimplesSelectOf() {
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        STypeSimples<?, ?> tipoInteger = pb.createTipo("Integer", STypeInteger.class).withSelectionOf(repeate(i -> i, 2).toArray(new Integer[]{}));
        STypeSimples<?, ?> tipoDate = pb.createTipo("Date", STypeData.class).withSelectionOf(repeate(i -> new Date(new Date().getTime() + 10 * i), 2).toArray(new Date[]{}));
        STypeSimples<?, ?> tipoString = pb.createTipo("String", STypeString.class).withSelectionOf(repeate(i -> textoTeste.substring(i), 2).toArray(new String[]{}));
        STypeSimples<?, ?> tipoStringLarge = pb.createTipo("StringLarge", STypeString.class).withSelectionOf(repeate(i -> textoTeste.substring(i), 5).toArray(new String[]{}));

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
        SDictionary dicionario = SDictionary.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        STypeLista<?, ?> listInteger3 = createSimpleList(pb, "Integer3", STypeInteger.class, 3, i -> 100 + i);
        STypeLista<?, ?> listInteger10 = createSimpleList(pb, "Integer10", STypeInteger.class, 10, i -> 100 + i);
        STypeLista<?, ?> listInteger20 = createSimpleList(pb, "Integer20", STypeInteger.class, 20, i -> 100 + i);

        STypeLista<?, ?> listString3 = createSimpleList(pb, "String3", STypeString.class, 3, i -> textoTeste.substring(i));
        STypeLista<?, ?> listString10 = createSimpleList(pb, "String10", STypeString.class, 10, i -> textoTeste.substring(i));
        STypeLista<?, ?> listString20 = createSimpleList(pb, "String20", STypeString.class, 20, i -> textoTeste.substring(i));

        assertView(MSelecaoMultiplaPorCheckView.class, listInteger3);
        assertView(MSelecaoMultiplaPorSelectView.class, listInteger10);
        assertView(MSelecaoMultiplaPorPicklistView.class, listInteger20);

        assertView(MSelecaoMultiplaPorCheckView.class, listString3);
        assertView(MSelecaoMultiplaPorSelectView.class, listString10);
        assertView(MSelecaoMultiplaPorPicklistView.class, listString20);
    }

}
