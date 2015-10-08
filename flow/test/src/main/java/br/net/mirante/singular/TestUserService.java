package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.persistence.entity.Actor;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class TestUserService implements IUserService {

    public static final Actor USER = new Actor() {
        @Override
        public Integer getCod() {
            return 1;
        }

        @Override
        public Long getCodigo() {
            return 1l;
        }

        @Override
        public String getNomeGuerra() {
            return "Soldado Ryan";
        }

        @Override
        public String getEmail() {
            return "mirante.teste@gmail.com";
        }
    };

    @Inject
    private SessionFactory sessionFactory;

    @Override
    public MUser getUserIfAvailable() {
        return (MUser) sessionFactory.getCurrentSession().createCriteria(Actor.class)
                .add(Restrictions.idEq(USER.getCodigo())).uniqueResult();
    }

    @Override
    public boolean canBeAllocated(MUser user) {
        return false;
    }
}
