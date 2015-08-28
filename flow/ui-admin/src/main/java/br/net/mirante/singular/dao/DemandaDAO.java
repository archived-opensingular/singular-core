package br.net.mirante.singular.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class DemandaDAO {
	
	@Inject
	private SessionFactory sessionFactory;
	
	public Session getSession() {
        return sessionFactory.getCurrentSession();
    }
	
	public List<Object> retrieveAll(){
		
		String sql="SELECT DEF.sigla, AVG(DATEDIFF(HOUR, DEM.data_inicio, DEM.data_fim)) FROM DMD_DEMANDA as DEM"
				+ "INNER JOIN DMD_DEFINICAO DEF ON (DEM.cod_definicao = DEF.cod)"
				+ "group by DEF.sigla";
		
		Query query = getSession().createSQLQuery(sql);
		query.setMaxResults(20);
		
		System.out.println(query.list());
		
		return null; //TODO arrumar retorno
	}
}
