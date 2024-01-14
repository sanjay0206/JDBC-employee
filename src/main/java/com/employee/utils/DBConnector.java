package com.employee.utils;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employeedb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "java@2021";
    private static final String SCRIPT_NAME= "employeedb.sql";
    private static Connection conn = null;
    private static final Logger logger = LogManager.getLogger(DBConnector.class);

    static {
        logger.debug("DBConnector static block is executed...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            logger.debug("Connected to employee database!");
            executeSQLScript(conn);
        } catch (Exception e) {
           logger.error(e.getMessage());
        }
    }

    private static void executeSQLScript(Connection conn) {
        try {
            InputStream inputStream = DBConnector.class.getClassLoader().getResourceAsStream(SCRIPT_NAME);
            if (inputStream != null) {
                InputStreamReader reader = new InputStreamReader(inputStream);
                ScriptRunner scriptRunner = new ScriptRunner(conn);
                scriptRunner.setSendFullScript(false);
                scriptRunner.setStopOnError(true);
                scriptRunner.runScript(reader);
                reader.close();
                inputStream.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static Connection getConnection() {
        return conn;
    }
}
