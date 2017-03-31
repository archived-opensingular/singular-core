package org.opensingular.form.persistence.dao;

import org.hibernate.transform.Transformers;
import org.opensingular.form.persistence.dto.PeticaoPrimariaDTO;
import org.opensingular.lib.support.persistence.SimpleDAO;

import javax.transaction.Transactional;
import java.util.List;

public class ReportDAO extends SimpleDAO {

    @Transactional(Transactional.TxType.NEVER)
    public List<PeticaoPrimariaDTO> listPeticoesPrimarias(String sql) {
        List<PeticaoPrimariaDTO> peticoes = getSession()
                .createSQLQuery(sql)
                .setResultTransformer(Transformers.aliasToBean(PeticaoPrimariaDTO.class))
                .list();
        return peticoes;
    }

}
