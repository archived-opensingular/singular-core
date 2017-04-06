package org.opensingular.lib.support.persistence.entityanddao;

import org.opensingular.lib.support.persistence.BaseDAO;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Transactional
public class TestDAO extends BaseDAO<TestEntity, Integer> {

    public TestDAO() {
        super(TestEntity.class);
    }

}
