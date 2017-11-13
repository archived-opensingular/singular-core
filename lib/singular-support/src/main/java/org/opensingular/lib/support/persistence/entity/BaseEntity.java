/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.support.persistence.entity;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseEntity<PK extends Serializable> implements Serializable {

    public abstract PK getCod();

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getClass().getName());
        s.append("[id = ").append(getCod()).append(']');
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

    /**
     * Compara duas entidades de acordo com a ordenação da chave primária. Caso uma chave seja null, então é considerado
     * que essa entidade é menor do que a outra com chave não nula.
     */
    public static <KEY extends Comparable<KEY> & Serializable> int compare(BaseEntity<KEY> e1, BaseEntity<KEY> e2) {
        if (e1 == e2) { //Também trata o caso de ambos serem null
            return 0;
        } else if (e1 == null) {
            return -1;
        } else if (e2 == null) {
            return 1;
        }
        KEY k1 = e1.getCod();
        KEY k2 = e2.getCod();
        if (k1 == k2) { //Também trata o caso de ambos serem null
            return 0;
        } else if (k1 == null) {
            return -1;
        } else if (k2 == null) {
            return 1;
        }
        return k1.compareTo(k2);
    }
}
