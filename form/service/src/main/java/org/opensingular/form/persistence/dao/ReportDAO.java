package org.opensingular.form.persistence.dao;

import org.hibernate.Hibernate;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.opensingular.form.persistence.dto.BaseDTO;
import org.opensingular.form.persistence.dto.PeticaoPrimariaDTO;
import org.opensingular.lib.support.persistence.SimpleDAO;

import javax.transaction.Transactional;
import java.util.List;

public class ReportDAO extends SimpleDAO {

    @Transactional(Transactional.TxType.NEVER)
    public List<? extends BaseDTO> listDtos(String sql, Class type) {
        List<BaseDTO> dtos = getSession()
                .createSQLQuery(sql)
                .addScalar("codVersaoFormulario", new LongType())
                .addScalar("nome")
                .addScalar("idade")
                .setResultTransformer(Transformers.aliasToBean(type))
                .list();
        return dtos;
    }

}
