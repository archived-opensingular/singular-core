package org.opensingular.lib.support.persistence.util;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensingular.lib.support.persistence.entityanddao.DatabaseConfigurationToBeUsedByTest;
import org.opensingular.lib.support.persistence.entityanddao.TestDAO;
import org.opensingular.lib.support.persistence.entityanddao.TestEntity;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DatabaseConfigurationToBeUsedByTest.class})
public class GenericEnumUserTypeTest {

    @Inject
    private TestDAO dao;

    /**
     * Verifica se a referencia estática ao nome da classe está de acordo com o pacote
     * onde a classe de fato está.
     */
    @Test
    public void testStaticClassReference(){
        Assert.assertEquals(GenericEnumUserType.CLASS_NAME, GenericEnumUserType.class.getName());
    }

    @Test
    public void sqlTypesTest(){
        GenericEnumUserType type = new GenericEnumUserType();
        Assert.assertNull(type.sqlTypes());

        Properties properties = new Properties();
        properties.setProperty("enumClass", SimNao.class.getName());
        type.setParameterValues(properties);

        Assert.assertEquals(SimNao.class, type.returnedClass());

        Assert.assertEquals(1, type.sqlTypes().length);
    }

    @Test
    public void testMethodsWithReturnIgualParameter(){
        GenericEnumUserType type = new GenericEnumUserType();

        Assert.assertFalse(type.equals("x","y"));
        Assert.assertTrue(type.equals("x","x"));

        Assert.assertFalse(type.isMutable());

        Assert.assertEquals(120, type.hashCode("x"));

        Assert.assertEquals("some string", type.disassemble(new String("some string")));

        Double toCopy = new Double(123456.00);
        Assert.assertEquals(toCopy, type.deepCopy(toCopy));

        Integer assemble = new Integer(987);
        Assert.assertEquals(assemble, type.assemble(assemble, null));

        Integer replaced = 123456;
        Assert.assertEquals(123456, type.replace(replaced, 123, null));
    }

    @Test
    @Transactional
    public void testNullSafeGetAndSet() throws SQLException {
        TestEntity novoObj = new TestEntity(20, "name", "field");
        dao.save(novoObj);
        TestEntity testEntities = dao.listAll().get(0);

        dao.saveOrUpdate(novoObj);

        testEntities.setSimNaoEnum(SimNao.SIM);
        dao.saveOrUpdate(novoObj);

        SimNao simNaoEnum = dao.find(20).get().getSimNaoEnum();

        Assert.assertEquals(SimNao.SIM, simNaoEnum);
    }
}
