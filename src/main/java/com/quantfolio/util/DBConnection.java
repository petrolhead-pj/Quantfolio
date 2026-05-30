package com.quantfolio.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASS;

    static {
        try (InputStream is = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) throw new RuntimeException("db.properties not found in classpath");
            Properties props = new Properties();
            props.load(is);
            URL  = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASS = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Cannot load db.properties: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static String getDisplayUrl() {
        return URL != null ? URL.split("\\?")[0] : "jdbc:mysql://localhost:3306/quantfolio";
    }
}
