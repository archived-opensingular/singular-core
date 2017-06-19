package org.opensingular.lib.support.persistence.entityanddao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.entity.EntityInterceptor;
import org.opensingular.lib.support.persistence.util.Constants;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@ContextConfiguration(classes = DatabaseConfigurationMock.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BaseEntityTest {

    @Inject
    private TestDAO dao;

    @Test
    public void testBaseToString(){
        dao.save(new TestEntity(0, "name", "entidade 1"));

        Optional<TestEntity> testEntity = dao.get(0);
        Assert.assertNotNull(testEntity.get().toString());
    }

    @Test
    public void testEquals(){
        dao.save(new TestEntity(0, "name", "entidade 1"));
        dao.save(new TestEntity(1, "name", "entidade 2"));

        TestEntity testEntity = dao.get(0).get();
        TestEntity testEntity2 = dao.get(1).get();

        Assert.assertTrue(testEntity.equals(testEntity));
        Assert.assertFalse(testEntity.equals("invalid Value"));
        Assert.assertFalse(testEntity.equals(testEntity2));

        TestEntity copyEntity = new TestEntity();
        copyEntity.setCod(0);
        copyEntity.setName("name");
        Assert.assertTrue(testEntity.equals(copyEntity));
    }

    @Test
    public void testGetOriginal(){
        dao.save(new TestEntity(0, "name", "entidade 1"));
        TestEntity testEntity = dao.get(0).get();

        Assert.assertEquals(0, testEntity.hashCode());
        Assert.assertNotNull(BaseEntity.getOriginal(testEntity));
    }

    @Test
    public void entityInterceptor(){
        Assert.assertNotNull(new EntityInterceptor().onPrepareStatement(Constants.SCHEMA+"qualquer coisa"));
    }
}
