package com.example.ud_quizzi.view;

import com.example.ud_quizzi.model.Exam;
import com.example.ud_quizzi.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;

public class ExamDetailCardController {

    @FXML private Label lblExamID;
    @FXML private Label lblExamName;
    @FXML private Label lblDate;
    @FXML private Label lblAuthor;
    @FXML private Label lblTime;

    private Exam exam;
    private User student;
    private Connection conn;

    public void setExamData(Exam exam, User student, Connection conn) {
        if (exam == null || student == null || conn == null) {
            throw new IllegalArgumentException("Exam, Student hoặc Connection không được null");
        }

        this.exam = exam;
        this.student = student;
        this.conn = conn;

        try {
            lblExamID.setText(String.valueOf(exam.getExamID()));
            lblExamName.setText(exam.getExamName());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            lblDate.setText(exam.getCreatedDate() != null ? sdf.format(exam.getCreatedDate()) : "");

            lblAuthor.setText(exam.getCreatedBy());
            lblTime.setText(exam.getTestTime() + " phút");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể hiển thị thông tin đề thi: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void handleStartExam() {
        try {
            // 1. Đóng cửa sổ Popup hiện tại
            Stage currentStage = (Stage) lblExamID.getScene().getWindow();
            currentStage.close();

            // 2. Load màn hình làm bài thi
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ud_quizzi/view/TakingExamScreen.fxml"));
            Parent root = loader.load();

            // 3. Lấy Controller và truyền dữ liệu
            TakingExamController takingController = loader.getController();
            takingController.setupExam(this.exam, this.student, this.conn);

            // 4. Hiển thị
            Stage examStage = new Stage();
            examStage.setTitle("Đang làm bài thi: " + exam.getExamName());
            examStage.setScene(new Scene(root));
            examStage.setMaximized(true);
            examStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setContentText("Không thể mở màn hình làm bài thi: " + e.getMessage());
            alert.show();
        }
    }

}
