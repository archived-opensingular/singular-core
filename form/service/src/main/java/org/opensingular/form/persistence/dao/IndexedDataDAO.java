/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.persistence.dao;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.opensingular.form.persistence.dto.BaseDTO;
import org.opensingular.form.persistence.dto.STypeIndexed;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.math.BigDecimal;

@Repository
@Transactional(Transactional.TxType.NEVER)
public class IndexedDataDAO {

    @Inject
    protected SessionFactory sessionFactory;

    protected String createSqlFromDTO(Class<? extends BaseDTO> dto, String schema) {
        IndexedDataQueryBuilder builder = new IndexedDataQueryBuilder(schema);
        for (Field field : dto.getDeclaredFields()) {
            if (field.getAnnotation(STypeIndexed.class).indexedColumn()) {
                builder.addColumn(field.getName(), field.getAnnotation(STypeIndexed.class).path());
            }
        }
        return builder.createQueryForIndexedData();
    }

    protected void addScalarsFromDTO(SQLQuery query, Class<? extends BaseDTO> dto) {
        for (Field field : dto.getDeclaredFields()) {
            if (field.getAnnotation(STypeIndexed.class).returnColumn()) {
                if (BigDecimal.class.isAssignableFrom(field.getType())) {
                    query.addScalar(field.getName(), StandardBasicTypes.BIG_DECIMAL);
                } else {
                    query.addScalar(field.getName());
                }
            }
        }
    }

}
