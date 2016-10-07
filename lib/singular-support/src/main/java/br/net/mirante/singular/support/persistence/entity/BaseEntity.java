/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.support.persistence.entity;

import java.io.Serializable;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

@SuppressWarnings("serial")
public abstract class BaseEntity<PK extends Serializable> implements Serializable{

    public abstract PK getCod();
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getClass().getName());
        s.append("[id = ").append(getCod()).append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        PK cod = getCod();
        return (cod == null) ? super.hashCode() : cod.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BaseEntity)) {
            return false;
        }
        BaseEntity other = (BaseEntity) obj;
        if (!((getCod() == other.getCod()) || (getCod() != null && getCod().equals(other.getCod())))) {
            return false;
        }

        other = getOriginal(other);
        BaseEntity me = getOriginal(this);
        if (me == other) {
            return true;
        }
        if (getCod() == null) {
            return false; 
        }
        if (!other.getClass().isAssignableFrom(me.getClass())) {
            return false;
        }
        return true;
    }

    public static final <T> T getOriginal(T obj) {
        if (obj instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) obj;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            return (T) li.getImplementation();
        }
        return obj;
    }
}
