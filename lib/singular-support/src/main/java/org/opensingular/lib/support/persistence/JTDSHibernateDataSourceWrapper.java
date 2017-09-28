package org.opensingular.lib.support.persistence;

import org.opensingular.lib.commons.util.Loggable;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Data Source wrapping to workaround the unsupported setBinaryStream issue
 * of jtds and hibernate combination.
 * This wrapper redirects calls that use the long size version of setBinaryStream to the int versio n.
 */
public class JTDSHibernateDataSourceWrapper implements DataSource, Loggable {

    private DataSource dataSource;

    public JTDSHibernateDataSourceWrapper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JTDSHibernateDataSourceWrapper() {
    }

    @Override
    public Connection getConnection() throws SQLException {
        return proxyConnection(dataSource.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        return proxyConnection(dataSource.getConnection(username, password));
    }

    private Connection proxyConnection(Connection connection) {
        return (Connection) Proxy.newProxyInstance(connection.getClass().getClassLoader(), new Class[]{Connection.class}, (proxy, method, args) -> {
            Object invoke = method.invoke(connection, args);
            if ("prepareStatement".equals(method.getName())) {
                return createPreparedStatementProxy(invoke);
            }
            return invoke;
        });
    }

    private Object createPreparedStatementProxy(Object invoke) {
        return Proxy.newProxyInstance(invoke.getClass().getClassLoader(), new Class[]{PreparedStatement.class}, (proxy1, method1, args1) -> {
            Arguments arguments = new Arguments(args1);
            if ("setBinaryStream".equals(method1.getName()) && arguments.isCollectParameters()) {
                PreparedStatement ps = (PreparedStatement) invoke;
                ps.setBinaryStream((Integer) arguments.getFirst(), (InputStream) arguments.getSecound(), ((Long) arguments.getThird()).intValue());
                return null;
            }
            else if ("setCharacterStream".equals(method1.getName()) && arguments.isCollectParameters()) {
                PreparedStatement ps = (PreparedStatement) invoke;
                ps.setCharacterStream((Integer) arguments.getFirst(), (Reader) arguments.getSecound(), ((Long) arguments.getThird()).intValue());
                return null;
            }
            return method1.invoke(invoke, args1);
        });
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }


    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    static class Arguments {
        private static final int FIRST = 0, SECOUND = 1, THIRD = 2;

        private final Object[] args;

        Arguments(Object[] args) {
            this.args = args;
        }

        Object getFirst() {
            return getAtIndex(FIRST);
        }

        Object getSecound() {
            return getAtIndex(SECOUND);
        }

        Object getThird() {
            return getAtIndex(THIRD);
        }

        Object getAtIndex(int index) {
            if (args.length > index) {
                return args[index];
            }
            return null;
        }

        private boolean isCollectParameters() {
            return (args != null && args.length == 3 && getAtIndex(THIRD) instanceof Long);
        }
    }

}