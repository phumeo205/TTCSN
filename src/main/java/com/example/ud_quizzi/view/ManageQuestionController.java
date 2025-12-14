package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.QuestionController;
import com.example.ud_quizzi.model.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class ManageQuestionController {

    @FXML
    private TableView<Question> tableQuestions;
    @FXML
    private TableColumn<Question, Integer> colId;
    @FXML
    private TableColumn<Question, String> colContent;
    @FXML
    private TableColumn<Question, String> colAnswer;

    private QuestionController questionController;
    private ObservableList<Question> questionList;
    private Connection connection;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("questionID"));
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colAnswer.setCellValueFactory(new PropertyValueFactory<>("answer"));
    }

    public void setConnection(Connection conn) {
        this.connection = conn;
        this.questionController = new QuestionController(conn);
        loadQuestions();
    }

    public void loadQuestions() {
        try {
            if (questionController != null) {
                List<Question> list = questionController.getAllQuestions();
                questionList = FXCollections.observableArrayList(list);
                tableQuestions.setItems(questionList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể tải danh sách câu hỏi!");
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ud_quizzi/view/AddQuestionScreen.fxml"));
            Parent root = loader.load();

            AddQuestionController addController = loader.getController();
            addController.setConnection(this.connection);  // truyền Connection
            addController.setManageController(this);      // truyền reference để refresh bảng

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thêm Câu Hỏi");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể mở giao diện thêm câu hỏi!");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Question selected = tableQuestions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn câu hỏi để xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa câu hỏi này?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = questionController.deleteQuestion(selected.getQuestionID());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "✅ Đã xóa câu hỏi!");
                loadQuestions();
            } else {
                showAlert(Alert.AlertType.ERROR, "❌ Không thể xóa câu hỏi!");
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) tableQuestions.getScene().getWindow();
        stage.close();
    }

    public void refreshTable() {
        loadQuestions();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
