package org.opensingular.form.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Rollback(value = true)
public class FormIndexServiceTest {

    @Inject
    private FormIndexService formIndexService;

    @Test
    public void listarTodosFormsTest() {
        try {
            formIndexService.indexAllForms();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
