package org.opensingular.lib.support.persistence.entityanddao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.lib.commons.base.SingularException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DatabaseConfigurationToBeUsedByTest.class})
public class BaseDAOTest {

    @Inject
    private TestDAO dao;

    @Test
    @Transactional
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
    @Transactional
    public void saveOrUpdateAndEvictAndGetOrExceptionTest(){
        TestEntity entity = new TestEntity(2, "entiidade 2", null);

        dao.save(entity);

        entity.setName("new name");

        dao.saveOrUpdate(entity);

        Assert.assertNotNull(dao.getOrException(2));

        dao.evict(entity);

    }

    @Test(expected = SingularException.class)
    @Transactional
    public void findTest(){
        dao.save(new TestEntity(3, "name", null));

        Assert.assertNotNull(dao.find(3));

        Assert.assertNotNull(dao.findOrException(3));

        dao.findOrException(15);
    }

    @Test
    @Transactional
    public void listAllTest(){
        Assert.assertNotNull(dao.listAll());
    }

    @Test
    @Transactional
    public void findByPropertyTest(){
        dao.save(new TestEntity(0, "entidade 1", "entidade 1"));
        dao.save(new TestEntity(1, "name", "entidade 1"));

        assertThat(dao.findByProperty("name", "entidade 1")).hasSize(1);
    }
}
