package org.opensingular.form.flatview.mapper;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewByBlock;

public class BlockFlatViewGeneratorTest {

    private STypeComposite<SIComposite> pessoa;
    private STypeString nome;
    private STypeString idade;
    private STypeComposite<SIComposite> pessoaWrap;
    private MockDocumentCanvas mockDocumentCanvas;
    private BlockFlatViewGenerator blockFlatViewGenerator;

    @Before
    public void setUp() throws Exception {
        PackageBuilder myPackage = SDictionary.create().createNewPackage("br.com");
        pessoa = myPackage.createCompositeType("pessoa");
        pessoaWrap = pessoa.addFieldComposite("pessoaWrap");
        nome = pessoaWrap.addField("nome", STypeString.class);
        idade = pessoaWrap.addField("idade", STypeString.class);
        nome.asAtr().label("Nome");
        idade.asAtr().label("Idade");
        mockDocumentCanvas = new MockDocumentCanvas();
        blockFlatViewGenerator = new BlockFlatViewGenerator();
    }

    @Test
    public void shouldUseTheViewTitleAsTitle() throws Exception {
        pessoa.withView(new SViewByBlock(), block ->
                block.newBlock("Pessoa")
                        .add(pessoaWrap));
        SIComposite ipessoa = pessoa.newInstance();
        blockFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(ipessoa));
        mockDocumentCanvas.assertTitle("Pessoa");
    }

    @Test
    public void shouldUseTypeLabelAsTitle() throws Exception {
        pessoaWrap.asAtr().label("Pessoa Label");
        pessoa.withView(new SViewByBlock(), block -> block.newBlock().add(pessoaWrap));
        SIComposite ipessoa = pessoa.newInstance();
        blockFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(ipessoa));
        mockDocumentCanvas.assertTitle("Pessoa Label");
    }
}