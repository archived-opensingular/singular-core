package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.flow.util.vars.VarType;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.VariableType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class VariableTypeDAO extends AbstractHibernateDAO<VariableType> {


    public VariableTypeDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public VariableType retrieveById(Serializable id) {
        return (VariableType) getSession().load(VariableType.class, id);
    }

    public VariableType retrieveByTypeClassName(String typeClassName) {
        return retrieveByUniqueProperty(VariableType.class, "typeClassName", typeClassName);
    }

    public VariableType retrieveOrSave(VarType tipoParametro) {
        VariableType variableType = retrieveByTypeClassName(tipoParametro.getClass().getName());
        if (variableType == null) {
            variableType = new VariableType();
            variableType.setTypeClassName(tipoParametro.getClass().getName());
            save(variableType);
        }
        return variableType;
    }

}
