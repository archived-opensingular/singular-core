package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDate;
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
    private static STypeList<?, ?> createSimpleList(PackageBuilder pb, String name, Class<? extends STypeSimple<?, ?>> type, int size,
                                                     Function<Integer, Object> valueProvider) {
        STypeSimple<?, ?> simpleType = pb.createType(name, type);
        simpleType.withSelectionOf((Collection) repeate(valueProvider, size));
        return pb.createListTypeOf("list" + name, simpleType);
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
        assertView(expectedView, dicionario.getType((Class<SType<?>>) type));
    }

    private static void assertView(Class<?> expectedView, SType<?> newInstance) {
        assertView(expectedView, newInstance.newInstance());
    }

    private static void assertView(Class<?> expectedView, SInstance instance) {
        SView view = ViewResolver.resolve(instance);
        if (expectedView == null && view == SView.DEFAULT) {
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
        assertView(null, STypeComposite.class);
        assertView(SViewListByForm.class, STypeList.class);
    }

    @Test
    public void testTipoSimplesSelectOf() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        STypeSimple<?, ?> tipoInteger = pb.createType("Integer", STypeInteger.class).withSelectionOf(repeate(i -> i, 2).toArray(new Integer[]{}));
        STypeSimple<?, ?> tipoDate = pb.createType("Date", STypeDate.class).withSelectionOf(repeate(i -> new Date(new Date().getTime() + 10 * i), 2).toArray(new Date[]{}));
        STypeSimple<?, ?> tipoString = pb.createType("String", STypeString.class).withSelectionOf(repeate(i -> textoTeste.substring(i), 2).toArray(new String[]{}));
        STypeSimple<?, ?> tipoStringLarge = pb.createType("StringLarge", STypeString.class).withSelectionOf(repeate(i -> textoTeste.substring(i), 5).toArray(new String[]{}));

        assertView(SViewSelectionBySelect.class, tipoInteger);
        assertView(SViewSelectionBySelect.class, tipoDate);
        assertView(SViewSelectionBySelect.class, tipoString);
        assertView(SViewSelectionBySelect.class, tipoStringLarge);

        tipoInteger.withRequired(true);
        tipoDate.withRequired(true);
        tipoString.withRequired(true);
        tipoStringLarge.withRequired(true);

        assertView(SViewSelectionByRadio.class, tipoInteger);
        assertView(SViewSelectionByRadio.class, tipoDate);
        assertView(SViewSelectionByRadio.class, tipoString);
        assertView(SViewSelectionBySelect.class, tipoStringLarge);
    }

    @Test
    public void testListTipoSimplesSelectOf() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");
        STypeList<?, ?> listInteger3 = createSimpleList(pb, "Integer3", STypeInteger.class, 3, i -> 100 + i);
        STypeList<?, ?> listInteger10 = createSimpleList(pb, "Integer10", STypeInteger.class, 10, i -> 100 + i);
        STypeList<?, ?> listInteger20 = createSimpleList(pb, "Integer20", STypeInteger.class, 20, i -> 100 + i);

        STypeList<?, ?> listString3 = createSimpleList(pb, "String3", STypeString.class, 3, i -> textoTeste.substring(i));
        STypeList<?, ?> listString10 = createSimpleList(pb, "String10", STypeString.class, 10, i -> textoTeste.substring(i));
        STypeList<?, ?> listString20 = createSimpleList(pb, "String20", STypeString.class, 20, i -> textoTeste.substring(i));

        assertView(SMultiSelectionByCheckboxView.class, listInteger3);
        assertView(SMultiSelectionBySelectView.class, listInteger10);
        assertView(SMultiSelectionByPicklistView.class, listInteger20);

        assertView(SMultiSelectionByCheckboxView.class, listString3);
        assertView(SMultiSelectionBySelectView.class, listString10);
        assertView(SMultiSelectionByPicklistView.class, listString20);
    }

}
