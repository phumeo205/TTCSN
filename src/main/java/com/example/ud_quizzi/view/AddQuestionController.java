package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.QuestionController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;

public class AddQuestionController {

    @FXML
    private TextField contentField, optionAField, optionBField, optionCField, optionDField, answerField;
    @FXML
    private Label messageLabel;

    private QuestionController questionController;
    private ManageQuestionController manageController;

    @FXML
    private void initialize() {
        messageLabel.setText("");
    }

    public void setManageController(ManageQuestionController controller) {
        this.manageController = controller;
    }

    public void setConnection(Connection conn) {
        this.questionController = new QuestionController(conn);
    }

    @FXML
    private void handleAdd() {
        if (questionController == null) {
            messageLabel.setText("❌ Chưa kết nối CSDL!");
            return;
        }

        String content = contentField.getText().trim();
        String optionA = optionAField.getText().trim();
        String optionB = optionBField.getText().trim();
        String optionC = optionCField.getText().trim();
        String optionD = optionDField.getText().trim();
        String answer = answerField.getText().trim().toUpperCase();

        if (content.isEmpty() || optionA.isEmpty() || optionB.isEmpty()
                || optionC.isEmpty() || optionD.isEmpty() || answer.isEmpty()) {
            messageLabel.setText("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!answer.matches("[ABCD]")) {
            messageLabel.setText("Đáp án phải là A, B, C hoặc D!");
            return;
        }

        boolean success = questionController.addQuestion(content, optionA, optionB, optionC, optionD, answer);

        if (success) {
            messageLabel.setText("✅ Thêm câu hỏi thành công!");
            clearFields();

            // 🔁 Load lại bảng trong ManageQuestionController
            if (manageController != null) {
                manageController.refreshTable();
            }

            // 🔒 Đóng cửa sổ thêm câu hỏi
            Stage stage = (Stage) contentField.getScene().getWindow();
            stage.close();
        } else {
            messageLabel.setText("❌ Thêm câu hỏi thất bại!");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) contentField.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        contentField.clear();
        optionAField.clear();
        optionBField.clear();
        optionCField.clear();
        optionDField.clear();
        answerField.clear();
    }
}
