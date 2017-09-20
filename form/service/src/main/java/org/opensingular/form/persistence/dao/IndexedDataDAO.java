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
