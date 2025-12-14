package com.example.ud_quizzi.view;

import com.example.ud_quizzi.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class StudentController {

    private Connection connection;
    private User student;

    public void setConnection(Connection conn) {
        this.connection = conn;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    @FXML
    private void handleSelectTheExam() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/ud_quizzi/view/SelectTheExamScreen.fxml")
            );
            Parent root = loader.load();

            SelectTheExamController controller = loader.getController();
            controller.setConnection(connection);
            controller.setStudent(student);
            controller.refresh(); // load dữ liệu mới ngay khi mở

            Stage examStage = new Stage();
            examStage.setTitle("Chọn đề thi");
            examStage.setScene(new Scene(root, 600, 550));
            examStage.centerOnScreen();
            examStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSeeTestScore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ud_quizzi/view/SeeTestScoreScreen.fxml"));
            Parent root = loader.load();

            SeeTestScoreScreenController controller = loader.getController();
            controller.setData(student, connection);

            Stage stage = new Stage();
            stage.setTitle("Xem kết quả thi");
            stage.setScene(new Scene(root, 600, 400));
            stage.centerOnScreen();
            stage.showAndWait(); // chờ popup đóng, Student Dashboard vẫn giữ nguyên

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleLogout(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/ud_quizzi/view/LoginScreen.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.centerOnScreen();
            stage.show();

            CurrentSession.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
