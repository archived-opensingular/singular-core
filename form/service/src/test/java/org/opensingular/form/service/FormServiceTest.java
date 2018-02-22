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

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.lib.commons.context.RefService;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.inject.Inject;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Rollback(value = true)
public abstract class FormServiceTest {

    @Inject
    protected SessionFactory sessionFactory;

    @Inject
    protected FormService       formService;

    protected SInstance         instance;
    protected STypeComposite<?> tipoPessoa;
    protected STypeInteger      idade;
    protected STypeString       nome;
    protected SDocumentFactory  documentFactory;
    protected RefType           tipoPessoaRef = RefType.of(() -> tipoPessoa);
    private SDocument           document;

    protected static final Integer IDADE_15 = 15;
    protected static final Integer IDADE_22 = 22;

    @Before
    public void setUp() {
        PackageBuilder pb = createTestDictionary().createNewPackage("pessoaTeste");
        tipoPessoa = pb.createType("pessoa", STypeComposite.class);
        idade = tipoPessoa.addFieldInteger("idade");
        nome = tipoPessoa.addFieldString("nome");

        tipoPessoa.asAtrAnnotation().setAnnotated();
        idade.asAtrIndex().indexed(true);
        nome.asAtrIndex().indexed(true);

        documentFactory = SDocumentFactory.of(doc -> {
            IAttachmentPersistenceHandler<?> tempHandler = mock(IAttachmentPersistenceHandler.class);
            IAttachmentPersistenceHandler<?> persistentHandler = mock(IAttachmentPersistenceHandler.class);
            doc.setAttachmentPersistenceTemporaryHandler(RefService.ofToBeDescartedIfSerialized(tempHandler));
            doc.setAttachmentPersistencePermanentHandler(RefService.ofToBeDescartedIfSerialized(persistentHandler));
        });
        TransactionSynchronizationManager.bindResource(this.sessionFactory, new SessionHolder(sessionFactory.openSession()));
    }

    protected SIComposite formWithoutAnnotations() {
        SIComposite pessoa = (SIComposite) documentFactory.createInstance(tipoPessoaRef);
        pessoa.setValue(idade, IDADE_15);
        pessoa.setValue(nome, "João");
        return pessoa;
    }

    protected FormKey insert() {
        SIComposite pessoa = formWithoutAnnotations();
        FormKey pessoaKey = formService.insert(pessoa, 1);
        SIComposite pessoaLoaded = (SIComposite) formService.loadSInstance(pessoaKey, tipoPessoaRef, documentFactory);
        Assert.assertEquals(pessoa, pessoaLoaded);
        return pessoaKey;
    }


    @After
    public void dispose() {
        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
    }

    protected final SDictionary createTestDictionary() {
        return SDictionary.create();
    }

}
