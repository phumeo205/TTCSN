package com.example.ud_quizzi.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            //Tạo FXMLLoader và load FXML
            FXMLLoader loader = new FXMLLoader();

            //Chú ý đường dẫn: từ thư mục resources
            loader.setLocation(getClass().getResource("/com/example/ud_quizzi/view/LoginScreen.fxml"));

            //Load FXML
            Parent root = loader.load();

            //Tạo Scene và hiển thị Stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Quizzi Login");
            primaryStage.show();

        } catch (Exception e) {
            //Bắt lỗi nếu FXML không tìm thấy hoặc load lỗi
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
