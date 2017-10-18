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

package org.opensingular.form.service;


import org.opensingular.form.SIComposite;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.entity.FormAnnotationEntity;
import org.opensingular.form.persistence.entity.FormAnnotationVersionEntity;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FormAnnotationVersionTest extends FormServiceTest {


    private static final Integer IDADE_15 = 15;
    private static final String TEXTO_ANOTACAO_INICIAL = "Essa pessoa é só de teste";

    private SIComposite formWithAnnotations() {
        SIComposite pessoa = (SIComposite) documentFactory.createInstance(tipoPessoaRef);
        pessoa.setValue(idade, IDADE_15);
        pessoa.setValue(nome, "João");
        SIAnnotation  annotation = pessoa.asAtrAnnotation().annotation();
        annotation.setText(TEXTO_ANOTACAO_INICIAL);
        annotation.setApproved(false);
        return pessoa;
    }

    protected FormKey insert() {
        SIComposite pessoa = formWithAnnotations();
        FormKey pessoaKey = formService.insert(pessoa, 1);
        SIComposite pessoaLoaded = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory);
        Assert.assertEquals(pessoa, pessoaLoaded);
        Assert.assertEquals(true, pessoaLoaded.asAtrAnnotation().hasAnyRefusal());
        Assert.assertEquals(TEXTO_ANOTACAO_INICIAL, pessoaLoaded.asAtrAnnotation().text());
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
        Assert.assertNotNull(fe.getCurrentFormVersionEntity().getFormAnnotations());
        Assert.assertEquals(1, fe.getCurrentFormVersionEntity().getFormAnnotations().size());
        FormAnnotationEntity currentAnnotation = fe.getCurrentFormVersionEntity().getFormAnnotations().get(0);
        List<FormVersionEntity> versions = sessionFactory.getCurrentSession().createCriteria(FormAnnotationVersionEntity.class).add(Restrictions.eq("formAnnotationEntity", currentAnnotation)).list();
        Assert.assertEquals(versions.size(), 1);
    }

    /**
     * Insere um novo formulário.
     * Cria uma nova versão
     * Valida se as anotações foram mantidas na nova versão.
     */
    @Test
    public void checkKeepAnnotations() {
        FormKey pessoaKey = insert();
        SIComposite firstVersion = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory);
        FormKey newPessoaKey = formService.newVersion(firstVersion, 1);
        SIComposite secondVersion = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory);

        Assert.assertEquals(true, secondVersion.asAtrAnnotation().hasAnyRefusal());
        Assert.assertEquals(TEXTO_ANOTACAO_INICIAL, secondVersion.asAtrAnnotation().text());
    }


}


