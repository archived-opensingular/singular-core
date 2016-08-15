package br.net.mirante.singular.form.service;


import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.persistence.FormKey;
import org.junit.Assert;
import org.junit.Test;

public class FormVersionTest extends FormServiceTest {

    private SIComposite formWithoutAnnotations() {
        SIComposite pessoa = (SIComposite) documentFactory.createInstance(tipoPessoaRef);
        pessoa.setValue(idade, 15);
        pessoa.setValue(nome, "João");
        return pessoa;
    }

    @Test
    public void insertTest() {
        SIComposite pessoa = formWithoutAnnotations();
        FormKey pessoaKey = formService.insert(pessoa);

        SIComposite pessoaLoaded = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory);
        Assert.assertEquals(pessoa, pessoaLoaded);
    }
}
