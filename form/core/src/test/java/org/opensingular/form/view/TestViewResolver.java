/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.view;

import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIList;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.list.SViewListByForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class TestViewResolver {

    private static final String textoTeste = "stringzonamuitolocabemgrandeparaevitarproblemascomarrayoutofboundsnessestestesunitariosaqui";

    @SuppressWarnings({"unchecked"})
    private static <T extends STypeSimple<X, V>, X extends SISimple<V>, V extends Serializable> STypeList<T, X> createSimpleList(
            PackageBuilder pb, String name, Class<T> type, int size, Function<Integer, Object> valueProvider) {
        T simpleType = pb.createType(name, type);
        simpleType.typelessSelection()
                .selfIdAndDisplay()
                .simpleProvider(ins -> repeate(valueProvider, size));
        return pb.createListTypeOf("list" + name, simpleType);
    }

    private static <T> List<T> repeate(Function<Integer, T> valueSupplier, int size) {
        ArrayList<T> l = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            l.add(valueSupplier.apply(i));
        }
        return l;
    }

    @SuppressWarnings("unchecked")
    private static void assertView(Class<?> expectedView, Class<?> type) {
        SDictionary dictionary = SDictionary.create();
        assertView(expectedView, dictionary.getType((Class<SType<?>>) type));
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

    private static void assertViewForListOf(Class<?> expectedView, Class<?> type) {
        SDictionary dictionary = SDictionary.create();
        SIList<?> list = dictionary.getType((Class<SType<?>>) type).newList();
        assertView(expectedView, list);
    }

    @Test
    public void testBasicView() {
        assertView(null, STypeBoolean.class);
        assertView(null, STypeString.class);
        assertView(null, STypeInteger.class);
        assertView(null, STypeString.class);
        assertView(null, STypeComposite.class);
        assertViewForListOf(SViewListByForm.class, STypeComposite.class);
        assertViewForListOf(SViewListByForm.class, STypeString.class);
    }

    @Test
    public void testTipoSimplesSelectOf() {
        SDictionary       dictionary      = SDictionary.create();
        PackageBuilder    pb              = dictionary.createNewPackage("teste");
        STypeSimple<?, ?> tipoInteger     = pb.createType("Integer", STypeInteger.class).selectionOf(repeate(i -> i, 2).toArray(new Integer[]{}));
        STypeSimple<?, ?> tipoDate        = pb.createType("Date", STypeDate.class).selectionOf(repeate(i -> new Date(new Date().getTime() + 10000 * i), 2).toArray(new Date[]{}));
        STypeSimple<?, ?> tipoString      = pb.createType("String", STypeString.class).selectionOf(repeate(textoTeste::substring, 2).toArray(new String[]{}));
        STypeSimple<?, ?> tipoStringLarge = pb.createType("StringLarge", STypeString.class).selectionOf(repeate(textoTeste::substring, 5).toArray(new String[]{}));

        assertView(SViewSelectionBySelect.class, tipoInteger);
        assertView(SViewSelectionBySelect.class, tipoDate);
        assertView(SViewSelectionBySelect.class, tipoString);
        assertView(SViewSelectionBySelect.class, tipoStringLarge);

        tipoInteger.asAtr().required(true);
        tipoDate.asAtr().required(true);
        tipoString.asAtr().required(true);
        tipoStringLarge.asAtr().required(true);

        assertView(SViewSelectionByRadio.class, tipoInteger);
        assertView(SViewSelectionByRadio.class, tipoDate);
        assertView(SViewSelectionByRadio.class, tipoString);
        assertView(SViewSelectionBySelect.class, tipoStringLarge);
    }

    @Test
    public void testListTipoSimplesSelectOf() {
        SDictionary                        dictionary    = SDictionary.create();
        PackageBuilder                     pb            = dictionary.createNewPackage("teste");
        STypeList<STypeInteger, SIInteger> listInteger3  = createSimpleList(pb, "Integer3", STypeInteger.class, 3, i -> 100 + i);
        STypeList<STypeInteger, SIInteger> listInteger10 = createSimpleList(pb, "Integer10", STypeInteger.class, 10, i -> 100 + i);
        STypeList<STypeInteger, SIInteger> listInteger20 = createSimpleList(pb, "Integer20", STypeInteger.class, 20, i -> 100 + i);

        STypeList<?, ?> listString3  = createSimpleList(pb, "String3", STypeString.class, 3, textoTeste::substring);
        STypeList<?, ?> listString10 = createSimpleList(pb, "String10", STypeString.class, 10, textoTeste::substring);
        STypeList<?, ?> listString20 = createSimpleList(pb, "String20", STypeString.class, 20, textoTeste::substring);

        assertView(SMultiSelectionByCheckboxView.class, listInteger3);
        assertView(SMultiSelectionBySelectView.class, listInteger10);
        assertView(SMultiSelectionByPicklistView.class, listInteger20);

        assertView(SMultiSelectionByCheckboxView.class, listString3);
        assertView(SMultiSelectionBySelectView.class, listString10);
        assertView(SMultiSelectionByPicklistView.class, listString20);
    }

}
