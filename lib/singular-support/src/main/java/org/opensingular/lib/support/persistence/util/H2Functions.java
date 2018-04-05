/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.support.persistence.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class H2Functions {

    public static final String DROPALLONCE_SCRIPT = "CREATE ALIAS if not exists dropAllObjects FOR \"" + H2Functions.class.getName() + ".dropAllObjects\";select dropAllObjects();";

    public static Double dateDiffInDays(Date d1, Date d2) {
        long d1l = d1 == null ? 0 : d1.getTime();
        long d2l = d2 == null ? 0 : d2.getTime();
        return (d1l - d2l) / ((double) 1000 * 60 * 60 * 24);
    }

    /**
     * Function to trick H2 to drop all database objects using INIT script (see h2 docs) only once per database initialization.
     * Using plain "DROP ALL OBJECTS" in the INIT connection parameter makes H2 drop the entire database every time the connection pool creates
     * a new connections.
     * The corresponding INIT script to be used with this functions is defined in constant {@link #DROPALLONCE_SCRIPT}
     * @param conn
     * @throws SQLException
     */
    public static void dropAllObjects(Connection conn) throws SQLException {
        conn.createStatement().executeUpdate("CREATE TEMPORARY table IF NOT EXISTS INITONCE (initialized BOOLEAN not null);");
        ResultSet rs = conn.createStatement().executeQuery(" SELECT COUNT(*) FROM INITONCE");
        if (rs.next()) {
            if (rs.getInt(1) == 0) {
                conn.createStatement().executeUpdate("DROP ALL OBJECTS");
                conn.createStatement().executeUpdate("CREATE TEMPORARY table IF NOT EXISTS INITONCE (initialized BOOLEAN not null);");
                conn.createStatement().executeUpdate("insert into initonce values (true)");
            }
        }
    }
}
