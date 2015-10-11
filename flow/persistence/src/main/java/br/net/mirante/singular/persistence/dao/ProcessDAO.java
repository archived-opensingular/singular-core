package br.net.mirante.singular.persistence.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class ProcessDAO extends AbstractHibernateDAO<Process> {

    public ProcessDAO(SessionLocator sessionLocator) {
        super(Process.class, sessionLocator);
    }

    public Process retrieveByAbbreviation(String abbreviation) {
        return retrieveByUniqueProperty("processDefinition.abbreviation", abbreviation);
    }

    @SuppressWarnings("unchecked")
    public List<ProcessInstance> retrivePorEstado(Date minDataInicio, Date maxDataInicio, ProcessDefinition processDefinition, Collection<? extends TaskDefinition> states) {
        final Criteria c = getSession().createCriteria(ProcessInstance.class);
        c.createAlias("process", "DEF");
        c.add(Restrictions.eq("DEF.processDefinition", processDefinition));
        if (states != null && !states.isEmpty()) {
            c.add(Restrictions.in("currentTaskDefinition", states));
        }
        if (minDataInicio != null) {
            c.add(Restrictions.ge("beginDate", minDataInicio));
        }
        if (maxDataInicio != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(maxDataInicio);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.add(Calendar.MILLISECOND, -1);
            c.add(Restrictions.le("beginDate", calendar.getTime()));
        }
        c.addOrder(Order.desc("beginDate"));
        return c.list();


    }
}
