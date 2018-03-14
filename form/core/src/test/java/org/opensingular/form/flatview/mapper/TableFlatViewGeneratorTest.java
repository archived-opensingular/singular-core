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
import org.opensingular.form.*;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByTable;

import javax.annotation.Nonnull;

@RunWith(Enclosed.class)
public class TableFlatViewGeneratorTest {

    public static class CaseOne {
        private STypeList<STypeComposite<SIComposite>, SIComposite> pessoas;
        private STypeComposite<SIComposite> pessoa;
        private STypeString nome;
        private STypeString idade;
        private MockDocumentCanvas mockDocumentCanvas;
        private TableFlatViewGenerator tableFlatViewGenerator;

        @Before
        public void setUp() throws Exception {
            PackageBuilder myPackage = SDictionary.create().createNewPackage("br.com");
            pessoas = myPackage.createListOfNewCompositeType("pessoas", "pessoa");
            pessoa = pessoas.getElementsType();
            nome = pessoa.addField("nome", STypeString.class);
            idade = pessoa.addField("idade", STypeString.class);
            nome.asAtr().label("Nome");
            idade.asAtr().label("Idade");
            mockDocumentCanvas = new MockDocumentCanvas();
            tableFlatViewGenerator = new TableFlatViewGenerator();
            pessoas.withView(SViewListByTable::new);
        }

        @Test
        public void shouldCreateTableWithHeaderAndTwoRows() throws Exception {
            SIList<SIComposite> ipessoas = pessoas.newInstance();
            SIComposite ipessoa_1 = ipessoas.addNew();
            ipessoa_1.setValue(nome, "Danilo");
            ipessoa_1.setValue(idade, "26");
            SIComposite ipessoa_2 = ipessoas.addNew();
            ipessoa_2.setValue(nome, "Daniel");
            ipessoa_2.setValue(idade, "27");
            tableFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(ipessoas));
            mockDocumentCanvas.assertTableCount(1);

            MockTableRowCanvas header = mockDocumentCanvas.getMockTableCanvas(0).getMockTableHeader().getMockTableRowCanvas(0);
            header.assertColumnCount(2);
            header.assertColumn(0, "Nome");
            header.assertColumn(1, "Idade");

            MockTableRowCanvas bodyRow_1 = mockDocumentCanvas.getMockTableCanvas(0).getMockTableBody().getMockTableRowCanvas(0);
            bodyRow_1.assertColumnCount(2);
            bodyRow_1.assertColumn(0, "Danilo");
            bodyRow_1.assertColumn(1, "26");

            MockTableRowCanvas bodyRow_2 = mockDocumentCanvas.getMockTableCanvas(0).getMockTableBody().getMockTableRowCanvas(1);
            bodyRow_2.assertColumnCount(2);
            bodyRow_2.assertColumn(0, "Daniel");
            bodyRow_2.assertColumn(1, "27");
        }

        @Test
        public void shouldCreateTableWithEmptyState() throws Exception {
            SIList<SIComposite> ipessoas = pessoas.newInstance();
            SIComposite ipessoa_1 = ipessoas.addNew();
            ipessoa_1.setValue(nome, "Danilo");
            ipessoa_1.setValue(idade, null);
            SIComposite ipessoa_2 = ipessoas.addNew();
            ipessoa_2.setValue(nome, "Daniel");
            ipessoa_2.setValue(idade, "XX");
            tableFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(ipessoas));
            mockDocumentCanvas.assertTableCount(1);

            MockTableRowCanvas bodyRow_1 = mockDocumentCanvas.getMockTableCanvas(0).getMockTableBody().getMockTableRowCanvas(0);
            bodyRow_1.assertColumnCount(2);
            bodyRow_1.assertColumn(0, "Danilo");
            bodyRow_1.assertColumn(1, "-");

            MockTableRowCanvas bodyRow_2 = mockDocumentCanvas.getMockTableCanvas(0).getMockTableBody().getMockTableRowCanvas(1);
            bodyRow_2.assertColumnCount(2);
            bodyRow_2.assertColumn(0, "Daniel");
            bodyRow_2.assertColumn(1, "XX");
        }
    }


    public static class CaseTwo {

        private CaseTwo.RootType rootType;
        private MockDocumentCanvas mockDocumentCanvas;
        private TableFlatViewGenerator tableFlatViewGenerator;
        private SDictionary dictionary;

        @Before
        public void setUp() throws Exception {
            dictionary = SDictionary.create();
            dictionary.loadPackage(CaseTwo.TestPackage.class);
            rootType = dictionary.getType(CaseTwo.RootType.class);
            mockDocumentCanvas = new MockDocumentCanvas();
            tableFlatViewGenerator = new TableFlatViewGenerator();
        }

        @SInfoPackage(name = "testPackage")
        public static class TestPackage extends SPackage {
        }

        @SInfoType(name = "root", spackage = TestPackage.class)
        public static class RootType extends STypeComposite<SIComposite> {
            public STypeList<CompositeChildType, SIComposite> childs;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                childs = addFieldListOf("childs", CompositeChildType.class);
                childs.withView(new SViewListByTable().setRenderCompositeFieldsAsColumns(false));
            }
        }

        @SInfoType(name = "child", spackage = TestPackage.class)
        public static class CompositeChildType extends STypeComposite<SIComposite> {
            public STypeLong id;
            public STypeString name;

            protected void onLoadType(@Nonnull TypeBuilder tb) {
                id = addField("id", STypeLong.class);
                name = addField("name", STypeString.class);
                selection().id(id).display(name).simpleProvider(s -> s.add().set(id, 1L).set(name, "value"));
                asAtr().label("Value");
            }
        }

        @Test
        public void shouldRenderOneTitleAndOneRow() throws Exception {
            SIComposite root = rootType.newInstance();
            SIList<SIComposite> field = root.getField(rootType.childs);
            CompositeChildType childType = dictionary.getType(CompositeChildType.class);
            dictionary.getType(CaseTwo.RootType.class);
            SIComposite newValue = field.addNew();
            newValue.setValue(childType.id, 1L);
            newValue.setValue(childType.name, "value");
            tableFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(field));
            mockDocumentCanvas.assertTableCount(1);
            mockDocumentCanvas.getMockTableCanvas(0).getMockTableHeader().getMockTableRowCanvas(0).assertColumn(0, "Value");
            mockDocumentCanvas.getMockTableCanvas(0).getMockTableBody().getMockTableRowCanvas(0).assertColumn(0, "value");
        }


    }


}