package org.opensingular.form.flatview.mapper;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.*;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.type.core.STypeString;

public class TableFlatViewGeneratorTest {

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