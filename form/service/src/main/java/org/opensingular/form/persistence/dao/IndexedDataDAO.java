package org.opensingular.form.persistence.dao;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.opensingular.form.persistence.dto.BaseDTO;
import org.opensingular.form.persistence.dto.STypeIndexed;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.reflect.Field;

@Repository
@Transactional(Transactional.TxType.NEVER)
public class IndexedDataDAO {

    @Inject
    protected SessionFactory sessionFactory;

    protected String createSqlFromDTO (Class<? extends BaseDTO> dto) {
        IndexedDataQueryBuilder builder = new IndexedDataQueryBuilder();
        for (Field field : dto.getDeclaredFields()) {
            if (field.getAnnotation(STypeIndexed.class).ignore()) continue;

            builder.addColumn(field.getName(), field.getAnnotation(STypeIndexed.class).path());
        }
        return builder.createQueryForIndexedData();
    }

    protected void addScalarsFromDTO(SQLQuery query, Class<? extends BaseDTO> dto) {
        query.addScalar("co_tipo_formulario", new LongType())
             .addScalar("co_versao_formulario", new LongType());

        for (Field field : dto.getDeclaredFields()) {
//            if (field.getAnnotation(STypeIndexed.class).ignore()) continue;
            query.addScalar(field.getName());
        }
    }

}
