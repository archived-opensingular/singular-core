package org.opensingular.lib.support.persistence;

import org.opensingular.lib.commons.util.Loggable;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
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
 * This wrapper redirects calls that use the long size version of setBinaryStream to the int version.
 */
public class JTDSHibernateDataSourceWrapper implements DataSource, Loggable {

    @NotNull
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

    private Connection proxyConnection(Connection connection) throws SQLException {
        return (Connection) Proxy.newProxyInstance(connection.getClass().getClassLoader(), new Class[]{Connection.class}, (proxy, method, args) -> {
            Object invoke = method.invoke(connection, args);
            if (method.getName().equals("prepareStatement")) {
                return createPreparedStatementProxy(invoke);
            }
            return invoke;
        });
    }

    private Object createPreparedStatementProxy(Object invoke) {
        return Proxy.newProxyInstance(invoke.getClass().getClassLoader(), new Class[]{PreparedStatement.class}, (proxy1, method1, args1) -> {
            if (method1.getName().equals("setBinaryStream") && isCollectParameters(args1)) {
                PreparedStatement ps = (PreparedStatement) invoke;
                ps.setBinaryStream((Integer) args1[0], (InputStream) args1[1], Integer.valueOf(((Long) args1[2]).intValue()));
                return null;
            } else if (method1.getName().equals("setCharacterStream") && isCollectParameters(args1)) {
                PreparedStatement ps = (PreparedStatement) invoke;
                ps.setCharacterStream((Integer) args1[0], (Reader) args1[1], Integer.valueOf(((Long) args1[2]).intValue()));
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

    private boolean isCollectParameters(Object[] args1) {
        return (args1 != null && args1.length == 3 && args1[2] instanceof Long);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}


