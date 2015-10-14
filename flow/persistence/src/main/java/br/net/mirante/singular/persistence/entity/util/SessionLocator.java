package br.net.mirante.singular.persistence.entity.util;

import org.hibernate.Session;

@FunctionalInterface
public interface SessionLocator {

    Session getCurrentSession();
}
