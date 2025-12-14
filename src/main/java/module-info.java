module com.example.ud_quizzi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;
    requires jbcrypt;

    // Mở cho FXML loader
    opens com.example.ud_quizzi.view to javafx.fxml;
    opens com.example.ud_quizzi.controller to javafx.fxml;

    // Mở package chứa Main
    opens com.example.ud_quizzi.main to javafx.fxml, javafx.graphics;
    exports com.example.ud_quizzi.test;

    // Export để các package gọi nhau
    exports com.example.ud_quizzi.controller;
    exports com.example.ud_quizzi.dao;
    exports com.example.ud_quizzi.model;
    exports com.example.ud_quizzi.view;
}
