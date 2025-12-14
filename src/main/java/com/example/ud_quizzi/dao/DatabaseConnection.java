package com.example.ud_quizzi.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            // Load file properties
            Properties props = new Properties();
            InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties");
            if (in == null) {
                System.err.println("Không tìm thấy file database.properties!");
                return null;
            }
            props.load(in);

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Lấy thông tin từ file config
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Kết nối CSDL thành công!");
            return conn;
        } catch (Exception e) {
            System.err.println("Kết nối CSDL thất bại!");
            e.printStackTrace();
            return null;
        }
    }
}