package org.quantum.synchronizer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    private static final String DB_URL_KEY = "db.url";
    private static final String DB_USERNAME_KEY = "db.username";
    private static final String DB_PASSWORD_KEY = "db.password";

    private ConnectionManager() {

    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DbProperties.get(DB_URL_KEY), DbProperties.get(DB_USERNAME_KEY),
                    DbProperties.get(DB_PASSWORD_KEY));
        } catch (SQLException e) {
            // TODO logg in file
            throw new RuntimeException(e);
        }
    }

}
