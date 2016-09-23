package br.net.mirante.singular.form.service;


import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.persistence.entity.FormVersionEntity;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FormVersionTest extends FormServiceTest {


    private static final Integer IDADE_15 = 15;
    private static final Integer IDADE_22 = 22;

    private SIComposite formWithoutAnnotations() {
        SIComposite pessoa = (SIComposite) documentFactory.createInstance(tipoPessoaRef);
        pessoa.setValue(idade, IDADE_15);
        pessoa.setValue(nome, "João");
        return pessoa;
    }

    private FormKey insert() {
        SIComposite pessoa = formWithoutAnnotations();
        FormKey pessoaKey = formService.insert(pessoa, 1);
        SIComposite pessoaLoaded = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory);
        Assert.assertEquals(pessoa, pessoaLoaded);
        return pessoaKey;
    }

    @Test
    public void insertTestAndLoad(){
        insert();
    }


    @Test
    public void insertTestCheckNewVersion() {
        FormKey pessoaKey = insert();
        FormEntity fe = formService.loadFormEntity(pessoaKey);
        Assert.assertNotNull(fe);
        Assert.assertNotNull(fe.getCurrentFormVersionEntity());
        List<FormVersionEntity> versions = sessionFactory.getCurrentSession().createCriteria(FormVersionEntity.class).add(Restrictions.eq("formEntity.cod", fe.getCod())).list();
        Assert.assertEquals(versions.size(), 1);
    }


    /**
     * Insere um novo formulário.
     * Cria uma nova versão
     * Salva um novo valor nessa nova versão
     * Valida se os valores esperados na primeira e na segunda versão estão corretos.
     */
    @Test
    public void checkVersionChanged() {
        FormKey pessoaKey = insert();
        SIComposite firstVersion = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory);
        FormKey newPessoaKey = formService.newVersion(firstVersion, 1);
        SIComposite secondVersion = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory);
        secondVersion.setValue(idade, IDADE_22);
        newPessoaKey = formService.insertOrUpdate(secondVersion, 1);

        //verificando
        FormEntity fe = formService.loadFormEntity(pessoaKey);
        Assert.assertNotNull(fe);
        Assert.assertNotNull(fe.getCurrentFormVersionEntity());
        List<FormVersionEntity> versions = sessionFactory.getCurrentSession().createCriteria(FormVersionEntity.class).add(Restrictions.eq("formEntity.cod", fe.getCod())).addOrder(Order.asc("cod")).list();
        Assert.assertEquals(versions.size(), 2);
        SIComposite pessoav1 = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory, versions.get(0).getCod());
        SIComposite pessoav2 = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory, versions.get(1).getCod());
        Assert.assertEquals(IDADE_15, pessoav1.getValue(idade));
        Assert.assertEquals(IDADE_22, pessoav2.getValue(idade));

    }
}


