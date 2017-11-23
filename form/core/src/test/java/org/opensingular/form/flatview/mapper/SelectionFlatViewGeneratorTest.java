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

package org.opensingular.form.flatview.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.provider.SSimpleProvider;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.provider.SSimpleProviderListBuilder;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Enclosed.class)
public class SelectionFlatViewGeneratorTest {

    @RunWith(Parameterized.class)
    public static class STypeDisplay {

        private STypeComposite<SIComposite> estado;

        private String sigla;
        private String nome;

        public STypeDisplay(String sigla, String nome) {
            this.sigla = sigla;
            this.nome = nome;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"DF", "Distrito Federal"},
                    {"SP", "São Paulo"},
                    {"RN", "Rio Grande do Norte"},
                    {"AM", "Amazonas"}
            });
        }

        @Before
        public void setUp() throws Exception {
            PackageBuilder myPackage = SDictionary.create().createNewPackage("br.com");
            estado = myPackage.createCompositeType("estado");
            STypeString sigla = estado.addFieldString("sigla");
            STypeString nome = estado.addFieldString("nome");
            estado.selection().id(sigla).display(nome).simpleProvider((SSimpleProvider) builder -> builder.add()
                    .set(sigla, "DF").set(nome, "Distrito Federal")
                    .set(sigla, "SP").set(nome, "São Paulo")
                    .set(sigla, "RN").set(nome, "Rio Grande do Norte")
                    .set(sigla, "AM").set(nome, "Amazonas"));
        }

        @Test
        public void testWriteValue() throws Exception {
            SelectionFlatViewGenerator selectionFlatViewGenerator = new SelectionFlatViewGenerator();
            MockDocumentCanvas mockDocumentCanvas = new MockDocumentCanvas();
            SIComposite instances = estado.newInstance();
            instances.setValue("sigla", sigla);
            instances.setValue("nome", nome);
            FlatViewContext flatViewContext = new FlatViewContext(instances);
            selectionFlatViewGenerator.writeOnCanvas(mockDocumentCanvas, flatViewContext);
            mockDocumentCanvas.assertLabelValue(nome);
        }
    }

    @RunWith(Parameterized.class)
    public static class FreemarkerDisplay {

        private STypeComposite<SIComposite> estado;

        private String sigla;
        private String nome;

        public FreemarkerDisplay(String sigla, String nome) {
            this.sigla = sigla;
            this.nome = nome;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"DF", "Distrito Federal"},
                    {"SP", "São Paulo"},
                    {"RN", "Rio Grande do Norte"},
                    {"AM", "Amazonas"}
            });
        }

        @Before
        public void setUp() throws Exception {
            PackageBuilder myPackage = SDictionary.create().createNewPackage("br.com");
            estado = myPackage.createCompositeType("estado");
            STypeString sigla = estado.addFieldString("sigla");
            STypeString nome = estado.addFieldString("nome");
            estado.selection().id(sigla).display("${sigla}-${nome!}").simpleProvider(new SSimpleProvider() {
                @Override
                public void fill(SSimpleProviderListBuilder builder) {
                    builder.add()
                            .set(sigla, "DF").set(nome, "Distrito Federal")
                            .set(sigla, "SP").set(nome, "São Paulo")
                            .set(sigla, "RN").set(nome, "Rio Grande do Norte")
                            .set(sigla, "AM").set(nome, "Amazonas");
                }
            });
        }

        @Test
        public void testWriteValue() throws Exception {
            SelectionFlatViewGenerator selectionFlatViewGenerator = new SelectionFlatViewGenerator();
            MockDocumentCanvas mockDocumentCanvas = new MockDocumentCanvas();
            SIComposite instances = estado.newInstance();
            instances.setValue("sigla", sigla);
            instances.setValue("nome", nome);
            FlatViewContext flatViewContext = new FlatViewContext(instances);
            selectionFlatViewGenerator.writeOnCanvas(mockDocumentCanvas, flatViewContext);
            mockDocumentCanvas.assertLabelValue(sigla + "-" + nome);
        }
    }

}