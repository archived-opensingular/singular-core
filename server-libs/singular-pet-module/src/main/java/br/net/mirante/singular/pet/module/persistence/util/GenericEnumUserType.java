package br.net.mirante.singular.pet.module.persistence.util;

/* ********************************************************************
 Licensed to Jasig under one or more contributor license
 agreements. See the NOTICE file distributed with this work
 for additional information regarding copyright ownership.
 Jasig licenses this file to you under the Apache License,
 Version 2.0 (the "License"); you may not use this file
 except in compliance with the License. You may obtain a
 copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on
 an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied. See the License for the
 specific language governing permissions and limitations
 under the License.
 */

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Look here for more info on design.
 * http://community.jboss.org/wiki/Java5EnumUserType modify to use
 * AbstractStandardBasicType instead.
 * 
 * @author Chun ping Wang.
 * 
 */
public class GenericEnumUserType implements UserType, ParameterizedType {

    /** Constante DEFAULT_IDENTIFIER_METHOD_NAME. */
    private static final String DEFAULT_IDENTIFIER_METHOD_NAME = "name";

    /** Constante DEFAULT_VALUE_OF_METHOD_NAME. */
    private static final String DEFAULT_VALUE_OF_METHOD_NAME = "valueOf";

    /** Constante CLASS_NAME. */
    public static final String CLASS_NAME = "br.net.mirante.singular.pet.module.persistence.util.GenericEnumUserType";

    /** Campo enum class. */
    @SuppressWarnings("rawtypes")
    private Class<? extends Enum> enumClass;

    /** Campo identifier method. */
    private Method identifierMethod;

    /** Campo value of method. */
    private Method valueOfMethod;

    /** Campo type. */
    private AbstractStandardBasicType<?> type;

    /** Campo sql types. */
    private int[] sqlTypes;

    /**
     * Atribui o valor de parameter values.
     *
     * @param parameters
     *            a novo valor de parameter values
     */
    @Override
    public void setParameterValues(final Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        try {
            enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
        } catch (ClassNotFoundException cfne) {
            throw new HibernateException("Enum class not found", cfne);
        }

        String identifierMethodName = parameters.getProperty(
                "identifierMethod", DEFAULT_IDENTIFIER_METHOD_NAME);

        Class<?> identifierType;
        try {
            identifierMethod = enumClass.getMethod(identifierMethodName);
            identifierType = identifierMethod.getReturnType();
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain identifier method",
                    e);
        }

        type = (AbstractSingleColumnStandardBasicType<?>) new TypeResolver()
                .heuristicType(identifierType.getName(), parameters);

        if (type == null) {
            throw new HibernateException("Unsupported identifier type "
                    + identifierType.getName());
        }

        sqlTypes = new int[] { ((AbstractSingleColumnStandardBasicType<?>) type)
                .sqlType() };

        String valueOfMethodName = parameters.getProperty("valueOfMethod",
                DEFAULT_VALUE_OF_METHOD_NAME);

        try {
            valueOfMethod = enumClass.getMethod(valueOfMethodName, identifierType);
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain valueOf method", e);
        }
    }

    /**
     * Returned class.
     *
     * @return the class<? extends enum>
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends Enum> returnedClass() {
        return enumClass;
    }


    @Override
    public Object nullSafeGet(ResultSet rs, String[] names,
            SessionImplementor sImpl, Object owner) throws HibernateException,
            SQLException {
        Object identifier = type.get(rs, names[0], sImpl);
        if (rs.wasNull()) {
            return null;
        }

        try {
            return valueOfMethod.invoke(enumClass, identifier);
        } catch (Exception e) {
            throw new HibernateException(
                    "Exception while invoking valueOf method '"
                            + valueOfMethod.getName() + "' of "
                            + "enumeration class '" + enumClass + "'", e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
            SessionImplementor sImpl) throws HibernateException, SQLException {
        try {
            if (value == null) {
                st.setNull(index,
                        ((AbstractSingleColumnStandardBasicType<?>) type)
                                .sqlType());
            } else {
                Object identifier = identifierMethod.invoke(value);
                type.nullSafeSet(st, identifier, index, sImpl);
            }
        } catch (Exception e) {
            throw new HibernateException(
                    "Exception while invoking identifierMethod '"
                            + identifierMethod.getName() + "' of "
                            + "enumeration class '" + enumClass + "'", e);
        }
    }

    /**
     * Sql types.
     *
     * @return um objeto do tipo int[]
     */
    @Override
    public int[] sqlTypes() {
        return sqlTypes;
    }

    /**
     * Assemble.
     *
     * @param cached
     *            um cached
     * @param owner
     *            um owner
     * @return um objeto do tipo Object
     * @throws HibernateException
     *             uma exceção hibernate exception
     */
    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    /**
     * Deep copy.
     *
     * @param value
     *            um value
     * @return um objeto do tipo Object
     * @throws HibernateException
     *             uma exceção hibernate exception
     */
    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    /**
     * Disassemble.
     *
     * @param value
     *            um value
     * @return um objeto do tipo Serializable
     * @throws HibernateException
     *             uma exceção hibernate exception
     */
    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) value;
    }

    /**
     * Equals.
     *
     * @param x
     *            um x
     * @param y
     *            um y
     * @return true, em caso de sucesso
     * @throws HibernateException
     *             uma exceção hibernate exception
     */
    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return x == y;
    }

    /**
     * Hash code.
     *
     * @param x
     *            um x
     * @return um objeto do tipo int
     * @throws HibernateException
     *             uma exceção hibernate exception
     */
    @Override
    public int hashCode(final Object x) throws HibernateException {
        return x.hashCode();
    }

    /**
     * Verifica se é mutable.
     *
     * @return true, se mutable
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * Replace.
     *
     * @param original
     *            um original
     * @param target
     *            um target
     * @param owner
     *            um owner
     * @return um objeto do tipo Object
     * @throws HibernateException
     *             uma exceção hibernate exception
     */
    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }
}
