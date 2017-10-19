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

package org.opensingular.form.wicket.mapper.search;

import org.apache.wicket.markup.html.form.Button;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.converter.ValueToSICompositeConverter;
import org.opensingular.form.provider.Config;
import org.opensingular.form.provider.FilteredPagedProvider;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SearchModalMapperTest {

    private static final String BRAND      = "brand";
    private static final String MODEL      = "model";
    private static final String MULTIMEDIA = "multimedia";

    private static int PAGE_SIZE = 5;

    private SingularDummyFormPageTester tester;

    @Before
    public void setUp() {
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(SearchModalMapperTest::buildBaseType);
    }

    private static List<Car> carros() {
        return new CarsBuilder()
                .addCarWithMultimedia("Ford", "Focus")
                .addCarWithMultimedia("Ford", "Fusion")
                .addCarWithMultimedia("Ford", "Fiesta")
                .addCarWithMultimedia("Ford", "Ká")
                .addCarWithMultimedia("Fiat", "Mobi")
                .addCarWithMultimedia("Fiat", "Toro")
                .addCarWithMultimedia("Chevrolet", "Onix")
                .addCarWithMultimedia("Chevrolet", "Cobalt")
                .addCarWithMultimedia("Chevrolet", "Cruze")
                .addCarWithMultimedia("Renault", "Sandeiro")
                .addCarWhitoutMultimedia("Chevrolet", "Celta")
                .addCarWhitoutMultimedia("Fiat", "Palio")
                .addCarWhitoutMultimedia("Renault", "Clio")
                .build();
    }

    protected static void buildBaseType(STypeComposite<?> baseType) {

        final STypeComposite<?> car = baseType.addFieldComposite("car");

        final STypeString  brand      = car.addFieldString(BRAND);
        final STypeString  model      = car.addFieldString(MODEL);
        final STypeBoolean multimedia = car.addFieldBoolean(MULTIMEDIA);

        car.withView(new SViewSearchModal(), (Consumer<SViewSearchModal>) view -> view.withPageSize(PAGE_SIZE));

        car.asAtrProvider().filteredProvider(new FilteredPagedProvider<Car>() {

            @Override
            public void configureProvider(Config cfg) {
                cfg.getFilter().addFieldString(BRAND);
                cfg.getFilter().addFieldString(MODEL);
                cfg.getFilter().addFieldBoolean(MULTIMEDIA);
                cfg.result()
                        .addColumn("brand", "Brand")
                        .addColumn("model", "Model")
                        .addColumn("hasMultimedia", "Multimedia");
            }

            @Override
            public List<Car> load(ProviderContext<SInstance> context) {
                return filterByInstance(context.getFilterInstance()).subList(context.getFirst(), context.getFirst() + context.getCount());
            }

            @Override
            public long getSize(ProviderContext<SInstance> context) {
                return (long) filterByInstance(context.getFilterInstance()).size();
            }

            private List<Car> filterByInstance(SInstance filter) {
                String  brand         = Value.of(filter, BRAND);
                String  model         = Value.of(filter, MODEL);
                Boolean hasMultimedia = Value.of(filter, MULTIMEDIA);
                return carros().stream().filter(car -> {
                    boolean contains = true;
                    if (brand != null) {
                        contains = car.brand.equalsIgnoreCase(brand);
                    }
                    if (model != null) {
                        contains = car.model.equalsIgnoreCase(model);
                    }
                    if (hasMultimedia != null) {
                        contains = car.hasMultimedia.equals(hasMultimedia);
                    }
                    return contains;
                }).collect(Collectors.toList());
            }

        });

        car.asAtrProvider()
                .converter((ValueToSICompositeConverter<Car>) (ins, _car) -> {
                    ins.setValue(brand, _car.brand);
                    ins.setValue(model, _car.model);
                    ins.setValue(multimedia, _car.hasMultimedia);
                });
    }

    private void openModal() {
        tester.executeAjaxEvent(tester.getAssertionsForm()
                .getSubCompomentWithId(SearchModalPanel.MODAL_TRIGGER_ID)
                .getTarget(Button.class), "click");
    }

    @Test
    public void testPagination() {
        tester.startDummyPage();
        openModal();
        final BSDataTable table = tester.getAssertionsPage()
                .findSubComponent(component -> component instanceof BSDataTable)
                .getTarget(BSDataTable.class);
        Assert.assertEquals(table.getRowsPerPage(), PAGE_SIZE);
        Assert.assertEquals(table.getPageCount(), Math.ceil((double) carros().size() / 5), 0);
    }

    private static class Car implements Serializable {

        private String  brand;
        private String  model;
        private Boolean hasMultimedia;

        private Car(String brand, String model, Boolean hasMultimedia) {
            this.brand = brand;
            this.model = model;
            this.hasMultimedia = hasMultimedia;
        }

        public String getBrand() {
            return brand;
        }

        public String getModel() {
            return model;
        }

        public Boolean getHasMultimedia() {
            return hasMultimedia;
        }
    }

    private static class CarsBuilder {

        private List<Car> carros = new ArrayList<>();

        CarsBuilder addCarWithMultimedia(String brand, String model) {
            return add(brand, model, true);
        }

        CarsBuilder addCarWhitoutMultimedia(String brand, String model) {
            return add(brand, model, false);
        }

        private CarsBuilder add(String brand, String model, Boolean hasMultimedia) {
            carros.add(new Car(brand, model, hasMultimedia));
            return this;
        }

        List<Car> build() {
            return carros;
        }

    }

}