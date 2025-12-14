package com.example.ud_quizzi.test;

import com.example.ud_quizzi.dao.DatabaseConnection;

import java.sql.Connection;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Kết nối database thành công!");
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Kết nối database thất bại!");
        }
    }
}
