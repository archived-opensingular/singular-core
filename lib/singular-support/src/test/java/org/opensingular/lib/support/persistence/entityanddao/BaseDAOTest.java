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

package org.opensingular.lib.support.persistence.entityanddao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.lib.commons.base.SingularException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DatabaseConfigurationMock.class})
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BaseDAOTest {

    @Inject
    private TestDAO dao;

    @Test
    public void crudTest(){
        TestEntity entity = new TestEntity(1, "entidade 1", null);
        Integer integer = dao.save(entity);
        Assert.assertNotNull(dao.get(integer).get());

        entity.setOtherField("qualquer valor");
        dao.merge(entity);

        Assert.assertNotNull(dao.get(integer).get());

        dao.delete(entity);
        Assert.assertFalse(dao.get(integer).isPresent());
    }

    @Test
    public void saveOrUpdateAndEvictAndGetOrExceptionTest(){
        TestEntity entity = new TestEntity(2, "entiidade 2", null);

        dao.save(entity);

        entity.setName("new name");

        dao.saveOrUpdate(entity);

        Assert.assertNotNull(dao.getOrException(2));

        dao.evict(entity);
    }

    @Test(expected = SingularException.class)
    public void getOrExceptionTest(){
        dao.getOrException(3);
    }

    @Test(expected = SingularException.class)
    public void findTest(){
        dao.save(new TestEntity(3, "name", null));

        Assert.assertNotNull(dao.find(3));

        Assert.assertNotNull(dao.findOrException(3));

        dao.findOrException(15);
    }

    @Test
    public void listAllTest(){
        Assert.assertNotNull(dao.listAll());
    }

    @Test
    public void findByPropertyTest(){
        dao.save(new TestEntity(0, "entidade 1", "entidade 1"));
        dao.save(new TestEntity(1, "name", "entidade 1"));

        assertThat(dao.findByProperty("name", "entidade 1")).hasSize(1);

    }

    @Test
    public void findByPropertyWithMaxValueTest(){
        dao.save(new TestEntity(6, "name", "entidadeNova"));
        assertThat(dao.findByProperty("otherField", null, 1)).hasSize(1);
    }

    @Test
    public void findByUniquePropertyTest(){
        dao.save(new TestEntity(7, "nome", "outro valor"));

        TestEntity founded = dao.findByUniqueProperty("name", "nome");
        Assert.assertEquals("outro valor", founded.getOtherField());
        dao.flush();
    }

    @Test
    public void findAfterDate(){
        TestEntity entity = new TestEntity(8, "nome", "outro valor");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);

        entity.setDate(new Date());
        dao.save(entity);

        List<String> byDateAfter = dao.findNameSavedAfterDate(calendar.getTime());

        assertThat(byDateAfter).hasSize(1);
        Assert.assertEquals("nome", byDateAfter.get(0));
    }

    @Test
    public void findByName(){
        dao.save(new TestEntity(9, "Nine", "other value"));

        List<TestEntity> byDateAfter = dao.findByName("Nine");

        assertThat(byDateAfter).hasSize(1);
        Assert.assertEquals("Nine", byDateAfter.get(0).getName());
    }

    @Test
    public void findByCod(){
        dao.save(new TestEntity(10, "TEN", "other value"));

        TestEntity byDateAfter = dao.findByCod(10);

        assertThat(byDateAfter).isNotNull();
        assertThat(byDateAfter.getCod()).isEqualTo(10);
    }

    @Test
    public void findAllByCod(){
        dao.save(new TestEntity(15, "Quinze", "other value"));
        dao.save(new TestEntity(16, "Dezesseis", "other value"));

        List<TestEntity> allByCod = dao.findAllByCod(Arrays.asList(15, 16));
        assertThat(allByCod).hasSize(2);
    }

    @Test
    public void findByExample(){
        TestEntity entity = new TestEntity(11, "Eleven", "Ell");
        TestEntity entity2 = new TestEntity(12, "Doze", "Doz");
        dao.save(entity);
        dao.save(entity2);

        List<TestEntity> byExample = dao.findByExample(entity);
        assertThat(byExample).hasSize(1);
        Assert.assertEquals("Eleven", byExample.get(0).getName());

        List<TestEntity> byExample2 = dao.findByExample(entity2, 1);
        assertThat(byExample2).hasSize(1);
        Assert.assertEquals("Doze", byExample2.get(0).getName());
    }

    @Test
    public void findUniqueResultCriteria(){
        TestEntity entity = new TestEntity(13, "FOO", "fooName");
        dao.save(entity);

        Optional<TestEntity> treze = dao.findUniqueResultCriteriaTest("fooName");

        Assert.assertTrue(treze.isPresent());
        Assert.assertEquals("FOO", treze.get().getName());
    }

    @Test
    public void findUniqueResultQuery(){
        TestEntity entity = new TestEntity(14, "value", "values");
        dao.save(entity);

        Optional<TestEntity> treze = dao.findUniqueResultQueryTest("value");

        Assert.assertTrue(treze.isPresent());
        Assert.assertEquals("value", treze.get().getName());
    }
}
