package org.opensingular.form.service;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.entity.FormCacheFieldEntity;
import org.opensingular.form.persistence.entity.FormCacheValueEntity;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

public class FormVersionTest extends FormServiceTest {

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

    @Test
    @Ignore
    public void indexFieldAlreadyRegisteredDoesNotDuplicate() {
        Session  session  = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(FormCacheFieldEntity.class);
        List<FormCacheFieldEntity> indexedFields = criteria.list();
        Assert.assertTrue(indexedFields.size() == 0);

        insert();
        indexedFields = criteria.list();
        Assert.assertTrue(indexedFields.size() == 2);

        insert();
        indexedFields = criteria.list();
        Assert.assertTrue(indexedFields.size() == 2);
    }

    @Test
    @Ignore
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void indexFieldValues() {
        Session  session  = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(FormCacheValueEntity.class);
        List<FormCacheValueEntity> indexedValues = criteria.list();
        Assert.assertTrue(indexedValues.size() == 0);

        insert();
        indexedValues = criteria.list();
        Assert.assertTrue(indexedValues.size() == 2);

        insert();
        indexedValues = criteria.list();
        System.out.println("Total de " + indexedValues.size());
        Assert.assertTrue(indexedValues.size() == 4);
    }

}


