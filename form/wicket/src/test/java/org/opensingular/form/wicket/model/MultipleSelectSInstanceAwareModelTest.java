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

package org.opensingular.form.wicket.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opensingular.form.*;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.provider.SimpleProvider;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Enclosed.class)
public class MultipleSelectSInstanceAwareModelTest {

    public static class SISimpleCase {
        private STypeList<STypeString, SIString> alphabet;
        private SIComposite root;

        @Before
        public void setUp() throws Exception {
            PackageBuilder myPackage = SDictionary.create().createNewPackage("org.opensingular");
            STypeComposite<SIComposite> compostoRaiz = myPackage.createCompositeType("compostoRaiz");
            alphabet = compostoRaiz.addFieldListOf("alphabet", STypeString.class);
            alphabet.selectionOf(Arrays.asList("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()).toArray());
            root = compostoRaiz.newInstance();
        }

        @Test(expected = SingularFormException.class)
        public void shouldSendErrorWhenCreateNotUsingSIList() throws Exception {
            new MultipleSelectSInstanceAwareModel(new SInstanceRootModel<>(root));
        }

        @Test
        public void shouldFillListOfSelectModelsOnCreate() throws Exception {
            SIList<SIString> list = root.getField(alphabet);
            list.addNew(i -> i.setValue("A"));
            list.addNew(i -> i.setValue("B"));

            assertTrue(newModel(list).getObject().size() == 2);
        }

        @Test
        public void shouldNotChangeWhenSubmitedWithSameValues() throws Exception {
            SIList<SIString> list = root.getField(alphabet);
            list.addNew(i -> i.setValue("A"));
            list.addNew(i -> i.setValue("B"));
            SIList<SIString> spiedList = Mockito.spy(list);

            MultipleSelectSInstanceAwareModel model = Mockito.spy(newModel(spiedList));
            model.setObject(Arrays.asList("A", "B", "Z"));

            assertContains(list, "A", "B", "Z");
            verify(spiedList, times(0)).clearInstance();
            verify(spiedList, times(0)).setValue(Mockito.any());
        }

        @Test
        public void shouldUpdateValuesWhenChanged() throws Exception {
            SIList<SIString> list = root.getField(alphabet);
            list.addNew(i -> i.setValue("A"));
            list.addNew(i -> i.setValue("B"));

            newModel(list).setObject(Arrays.asList("C", "D"));

            assertContains(list, "C", "D");
        }

        @Test
        public void shouldKeepTheSameIdsWhenSubmitedWithChange() throws Exception {
            SIList<SIString> list = root.getField(alphabet);
            Integer idA = list.addNew(i -> i.setValue("A")).getId();
            Integer idB = list.addNew(i -> i.setValue("B")).getId();

            MultipleSelectSInstanceAwareModel model = newModel(list);
            model.setObject(Arrays.asList("A", "B", "C"));

            assertContains(list, "A", "B", "C");
            assertEquals(idA, findValueInList("A", list).getId());
            assertEquals(idB, findValueInList("B", list).getId());
        }

        @Test
        public void shouldKeepTheSameIdsWhenSubmitedWithChangeOnMidleOfTheList() throws Exception {
            SIList<SIString> list = root.getField(alphabet);
            Integer idA = list.addNew(i -> i.setValue("A")).getId();
            Integer idC = list.addNew(i -> i.setValue("C")).getId();

            MultipleSelectSInstanceAwareModel model = newModel(list);
            model.setObject(Arrays.asList("A", "B", "C"));

            assertContains(list, "A", "B", "C");
            assertEquals(idA, findValueInList("A", list).getId());
            assertEquals(idC, findValueInList("C", list).getId());
        }

        @Test
        public void shouldKeepTheSameIdsWhenSubmitedWithChangeOnHeadOfTheList() throws Exception {
            SIList<SIString> list = root.getField(alphabet);
            Integer idB = list.addNew(i -> i.setValue("B")).getId();
            Integer idC = list.addNew(i -> i.setValue("C")).getId();

            MultipleSelectSInstanceAwareModel model = newModel(list);
            model.setObject(Arrays.asList("A", "B", "C"));

            assertContains(list, "A", "B", "C");
            assertEquals(idB, findValueInList("B", list).getId());
            assertEquals(idC, findValueInList("C", list).getId());
        }

        @Test
        public void shouldKeepTheSameIdsWhenSubmitedWithChangeOnTailOfTheList() throws Exception {
            SIList<SIString> list = root.getField(alphabet);
            Integer idB = list.addNew(i -> i.setValue("B")).getId();
            Integer idC = list.addNew(i -> i.setValue("C")).getId();

            MultipleSelectSInstanceAwareModel model = newModel(list);
            model.setObject(Arrays.asList("B", "C", "D"));

            assertContains(list, "B", "C", "D");
            assertEquals(idB, findValueInList("B", list).getId());
            assertEquals(idC, findValueInList("C", list).getId());
        }

        private void assertContains(SIList<SIString> list, String... expected) {
            assertThat(list.stream().map(SISimple::getValue).collect(Collectors.toList()), Matchers.containsInAnyOrder(expected));
        }

        private SIString findValueInList(String value, SIList<SIString> list) {
            return list.stream()
                    .filter(i -> i.getValue().equals(value))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("A lista não possui o valor " + value));
        }

        @Nonnull
        private MultipleSelectSInstanceAwareModel newModel(final SIList<SIString> iStrings) {
            return new MultipleSelectSInstanceAwareModel(new AbstractReadOnlyModel<SInstance>() {
                @Override
                public SIList getObject() {
                    return iStrings;
                }
            });
        }
    }


    public static class SICompositeCase {

        private STypeLong id;
        private STypeString name;
        private SIComposite rootInstance;
        private STypeList<STypeComposite<SIComposite>, SIComposite> fruits;
        private STypeComposite<SIComposite> fruit;
        private SIList<SIComposite> list;

        public static class Fruit implements Serializable {
            public static Fruit BANANA = new Fruit(1L, "Banana");
            public static Fruit MACA = new Fruit(2L, "Maça");
            public static Fruit ABACATE = new Fruit(3L, "Abacate");
            public static Fruit LARANJA = new Fruit(4L, "Laranja");

            Long id;
            String name;

            public Fruit() {
            }

            Fruit(Long id, String name) {
                this.id = id;
                this.name = name;
            }

            public static List<Fruit> fruits() {
                List<Fruit> fruits = new ArrayList<>();
                fruits.add(ABACATE);
                fruits.add(BANANA);
                fruits.add(LARANJA);
                fruits.add(MACA);
                return fruits;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Fruit fruit = (Fruit) o;
                return Objects.equals(id, fruit.id) &&
                        Objects.equals(name, fruit.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id, name);
            }
        }


        @Before
        public void setUp() throws Exception {
            PackageBuilder myPackage = SDictionary.create().createNewPackage("org.opensingular");
            STypeComposite<SIComposite> root = myPackage.createCompositeType("root");
            fruits = root.addFieldListOfComposite("fruit", "fruits");
            fruit = fruits.getElementsType();
            id = fruit.addField("id", STypeLong.class);
            name = fruit.addField("name", STypeString.class);
            fruits.selectionOf(Fruit.class)
                    .id(i -> i.id)
                    .display(i -> i.name)
                    .autoConverterOf(Fruit.class)
                    .simpleProvider((SimpleProvider<Fruit, SIList<SIComposite>>) ins -> Fruit.fruits());
            rootInstance = root.newInstance();
            list = rootInstance.getField(fruits);
        }

        @Test(expected = SingularFormException.class)
        public void shouldSendErrorWhenCreateNotUsingSIList() throws Exception {
            new MultipleSelectSInstanceAwareModel(new SInstanceRootModel<>(rootInstance));
        }

        public void fillFruitInstance(SIComposite instance, Fruit fruit) {
            instance.setValue(id, fruit.id);
            instance.setValue(name, fruit.name);
        }

        @Test
        public void shouldFillListOfSelectModelsOnCreate() throws Exception {
            list.addNew(i -> fillFruitInstance(i, Fruit.ABACATE));
            list.addNew(i -> fillFruitInstance(i, Fruit.BANANA));

            assertTrue(newModel(list).getObject().size() == 2);
        }

        @Test
        public void shouldNotChangeWhenSubmitedWithSameValues() throws Exception {
            list.addNew(i -> fillFruitInstance(i, Fruit.ABACATE));
            list.addNew(i -> fillFruitInstance(i, Fruit.BANANA));
            SIList<SIComposite> spiedList = Mockito.spy(list);

            MultipleSelectSInstanceAwareModel model = Mockito.spy(newModel(spiedList));
            model.setObject(Arrays.asList(Fruit.ABACATE, Fruit.BANANA, Fruit.LARANJA));

            assertContains(list, Fruit.ABACATE, Fruit.BANANA, Fruit.LARANJA);
            verify(spiedList, times(0)).clearInstance();
            verify(spiedList, times(0)).setValue(Mockito.any());
        }

        @Test
        public void shouldUpdateValuesWhenChanged() throws Exception {
            list.addNew(i -> fillFruitInstance(i, Fruit.ABACATE));
            list.addNew(i -> fillFruitInstance(i, Fruit.BANANA));

            newModel(list).setObject(Arrays.asList(Fruit.LARANJA, Fruit.MACA));

            assertContains(list, Fruit.LARANJA, Fruit.MACA);
        }

        @Test
        public void shouldKeepTheSameIdsWhenSubmitedWithChange() throws Exception {
            Integer idA = list.addNew(i -> fillFruitInstance(i, Fruit.ABACATE)).getId();
            Integer idB = list.addNew(i -> fillFruitInstance(i, Fruit.BANANA)).getId();

            MultipleSelectSInstanceAwareModel model = newModel(list);
            model.setObject(Arrays.asList(Fruit.ABACATE, Fruit.BANANA, Fruit.LARANJA));

            assertContains(list, Fruit.ABACATE, Fruit.BANANA, Fruit.LARANJA);
            assertEquals(idA, findValueInList(Fruit.ABACATE, list).getId());
            assertEquals(idB, findValueInList(Fruit.BANANA, list).getId());
        }

        @Test
        public void shouldKeepTheSameIdsWhenSubmitedWithChangeOnMidleOfTheList() throws Exception {
            Integer idAbacate = list.addNew(i -> fillFruitInstance(i, Fruit.ABACATE)).getId();
            Integer idLaranja = list.addNew(i -> fillFruitInstance(i, Fruit.LARANJA)).getId();

            MultipleSelectSInstanceAwareModel model = newModel(list);
            model.setObject(Arrays.asList(Fruit.ABACATE, Fruit.MACA, Fruit.LARANJA));

            assertContains(list, Fruit.ABACATE, Fruit.MACA, Fruit.LARANJA);
            assertEquals(idAbacate, findValueInList(Fruit.ABACATE, list).getId());
            assertEquals(idLaranja, findValueInList(Fruit.LARANJA, list).getId());
        }

        @Test
        public void shouldKeepTheSameIdsWhenSubmitedWithChangeOnHeadOfTheList() throws Exception {
            Integer idBanana = list.addNew(i -> fillFruitInstance(i, Fruit.BANANA)).getId();
            Integer idLaranja = list.addNew(i -> fillFruitInstance(i, Fruit.LARANJA)).getId();

            MultipleSelectSInstanceAwareModel model = newModel(list);
            model.setObject(Arrays.asList(Fruit.ABACATE, Fruit.BANANA, Fruit.LARANJA));

            assertContains(list, Fruit.ABACATE, Fruit.BANANA, Fruit.LARANJA);
            assertEquals(idBanana, findValueInList(Fruit.BANANA, list).getId());
            assertEquals(idLaranja, findValueInList(Fruit.LARANJA, list).getId());
        }

        private void assertContains(SIList<SIComposite> list, Fruit... fruits) {
            SInstanceConverter converter = this.fruits.asAtrProvider().getConverter();
            assertThat(list.stream().map((Function<SIComposite, Serializable>) converter::toObject)
                    .collect(Collectors.toList()), Matchers.containsInAnyOrder(fruits));
        }

        private SIComposite findValueInList(Fruit value, SIList<SIComposite> list) {
            SInstanceConverter converter = this.fruits.asAtrProvider().getConverter();
            return list.stream()
                    .filter(i -> converter.toObject(i).equals(value))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("A lista não possui o valor " + value));
        }

        @Nonnull
        private MultipleSelectSInstanceAwareModel newModel(final SIList<SIComposite> composite) {
            return new MultipleSelectSInstanceAwareModel(new AbstractReadOnlyModel<SInstance>() {
                @Override
                public SIList getObject() {
                    return composite;
                }
            });
        }
    }


}
