package com.example.ud_quizzi.main;

import com.example.ud_quizzi.dao.DatabaseConnection;
import com.example.ud_quizzi.model.Exam;
import com.example.ud_quizzi.model.User;
import com.example.ud_quizzi.view.TakingExamController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class TakingExamViewDemo extends Application {

    private Exam exam;       // exam thực tế
    private User student;    // user thực tế

    public TakingExamViewDemo() {
        // ⚠️ Phải gán exam & student từ màn hình chọn đề thi
        // Ví dụ: nhận từ SelectTheExamController hoặc từ session login
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ud_quizzi/view/TakingExamScreen.fxml"));
            Parent root = loader.load();

            TakingExamController controller = loader.getController();

            // Kết nối DB
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("❌ Không thể kết nối DB!");
                return;
            }

            // Kiểm tra dữ liệu exam & student
            if (exam == null || student == null) {
                System.err.println("❌ Exam hoặc Student chưa được cung cấp!");
                return;
            }

            // Setup màn hình làm bài thi
            controller.setupExam(exam, student, conn);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Làm bài thi: " + exam.getExamName());
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
