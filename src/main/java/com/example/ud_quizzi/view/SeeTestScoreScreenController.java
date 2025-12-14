package com.example.ud_quizzi.view;

import com.example.ud_quizzi.dao.ResultDAO;
import com.example.ud_quizzi.model.Result;
import com.example.ud_quizzi.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

public class SeeTestScoreScreenController {

    @FXML private TableView<Result> tableResults;
    @FXML private TableColumn<Result, String> colExamName;
    @FXML private TableColumn<Result, Double> colScore;
    @FXML private TableColumn<Result, String> colDate;

    private Connection connection;
    private User student;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    /** Set dữ liệu từ StudentController, load điểm luôn */
    public void setData(User student, Connection connection) {
        this.student = student;
        this.connection = connection;
        loadResults();
    }

    /** Load lại danh sách kết quả thi */
    private void loadResults() {
        if (connection == null || student == null) return;

        try {
            ResultDAO dao = new ResultDAO(connection);
            List<Result> results = dao.getResultsWithExamNameByStudent(student.getUserID());

            colExamName.setCellValueFactory(new PropertyValueFactory<>("description"));
            colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
            colDate.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(
                            cell.getValue().getSubmittedDate() != null
                                    ? sdf.format(cell.getValue().getSubmittedDate())
                                    : ""
                    )
            );

            tableResults.setItems(FXCollections.observableArrayList(results));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể tải điểm!");
        }
    }

    /** Chỉ đóng popup khi bấm Back */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) tableResults.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}