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

import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.opensingular.lib.support.persistence.BaseDAO;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Named
@Transactional
public class TestDAO extends BaseDAO<TestEntity, Integer> {

    public TestDAO() {
        super(TestEntity.class);
    }

    public List<TestEntity> findByName(String nome){
        Map<String, Object> params = new HashMap();
        params.put("name", nome);

        Query query = getSession().createQuery("from "+TestEntity.class.getName()+" as t where t.name = :name");
        this.setParametersQuery(query, params);
        return query.list();
    }

    public List<String> findNameSavedAfterDate(Date date){
        Map<String, Object> params = new HashMap();
        params.put("date", date);

        Query query = getSession().createQuery("select t.name from "+TestEntity.class.getName()+" as t where t.date > :date");
        this.setParametersQuery(query, params);
        return query.list();
    }

    public TestEntity findByCod(Integer cod){
        Map<String, Object> params = new HashMap();
        params.put("cod", cod);

        Query query = getSession().createQuery("from "+TestEntity.class.getName()+" as t where t.cod = :cod");
        this.setParametersQuery(query, params);

        return (TestEntity) query.uniqueResult();
    }

    public List<TestEntity> findAllByCod(List<Integer> codigos){
        Map<String, Object> params = new HashMap();
        params.put("codigos", codigos);

        Query query = getSession().createQuery("from "+TestEntity.class.getName()+" as t where t.cod in (:codigos)");
        this.setParametersQuery(query, params);

        return query.list();
    }

    public Optional<TestEntity> findUniqueResultCriteriaTest(String otherValue){
        return findUniqueResult(TestEntity.class,
            getSession().createCriteria(TestEntity.class).add(Restrictions.eq("otherField", otherValue)));
    }

    public Optional<TestEntity> findUniqueResultQueryTest(String name){
        Map<String, Object> params = new HashMap();
        params.put("name", name);

        Query query = getSession()
                .createQuery("from " + TestEntity.class.getName() + " as t where t.name = :name");
        this.setParametersQuery(query, params);

        return findUniqueResult(TestEntity.class, query);
    }
}
