package com.example.ud_quizzi.view;

import com.example.ud_quizzi.dao.ResultDAO;
import com.example.ud_quizzi.model.Exam;
import com.example.ud_quizzi.model.Result;
import com.example.ud_quizzi.model.User;
import com.example.ud_quizzi.controller.UserController;
import com.example.ud_quizzi.controller.ExamController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

public class ResultListController {

    @FXML private TableView<Result> tableResultList;   // FIX đúng ID với FXML!!
    @FXML private TableColumn<Result, String> colUserName;
    @FXML private TableColumn<Result, String> colExamName;
    @FXML private TableColumn<Result, Double> colScore;
    @FXML private TableColumn<Result, String> colDate;

    private Connection connection;
    private UserController userController;
    private ExamController examController;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public void setConnection(Connection connection) {
        this.connection = connection;
        this.userController = new UserController(connection);
        this.examController = new ExamController(connection);

        loadResults();
    }

    private void loadResults() {
        if (connection == null) return;

        try {
            ResultDAO dao = new ResultDAO(connection);
            List<Result> results = dao.getAllResults();   // DAO phải có hàm này

            colUserName.setCellValueFactory(cell -> {
                User u = userController.getUserById(cell.getValue().getStudentID());
                return new javafx.beans.property.SimpleStringProperty(
                        u != null ? u.getUsername() : "Unknown"
                );
            });

            colExamName.setCellValueFactory(cell -> {
                Exam e = examController.getExamById(cell.getValue().getExamID());
                return new javafx.beans.property.SimpleStringProperty(
                        e != null ? e.getExamName() : "Unknown"
                );
            });

            colScore.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleDoubleProperty(
                            cell.getValue().getScore()
                    ).asObject()
            );

            colDate.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(
                            cell.getValue().getSubmittedDate() != null
                                    ? sdf.format(cell.getValue().getSubmittedDate())
                                    : ""
                    )
            );

            tableResultList.setItems(FXCollections.observableArrayList(results));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể tải kết quả!");
        }
    }

    private void loadResults(Exam exam) {
        try {
            ResultDAO dao = new ResultDAO(connection);

            // Lấy kết quả theo examID
            List<Result> results = dao.getResultsByExamId(exam.getExamID());

            colUserName.setCellValueFactory(cell -> {
                User u = userController.getUserById(cell.getValue().getStudentID());
                return new javafx.beans.property.SimpleStringProperty(
                        u != null ? u.getUsername() : "Unknown"
                );
            });

            colExamName.setCellValueFactory(cell -> {
                Exam e = examController.getExamById(cell.getValue().getExamID());
                return new javafx.beans.property.SimpleStringProperty(
                        e != null ? e.getExamName() : "Unknown"
                );
            });

            colScore.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleDoubleProperty(
                            cell.getValue().getScore()
                    ).asObject()
            );

            colDate.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(
                            cell.getValue().getSubmittedDate() != null
                                    ? sdf.format(cell.getValue().getSubmittedDate())
                                    : ""
                    )
            );

            tableResultList.setItems(FXCollections.observableArrayList(results));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể tải kết quả!");
        }
    }


    @FXML
    private void handleBack() {
        Stage stage = (Stage) tableResultList.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void handleSortA(ActionEvent actionEvent) {
        if (tableResultList.getItems() == null) return;

        tableResultList.getItems().sort(
                (r1, r2) -> Double.compare(r1.getScore(), r2.getScore())
        );
    }

    public void handleSortD(ActionEvent actionEvent) {
        if (tableResultList.getItems() == null) return;

        tableResultList.getItems().sort(
                (r1, r2) -> Double.compare(r2.getScore(), r1.getScore())
        );
    }

    public void setExamData(Exam exam, Connection connection) {
        if (exam == null || connection == null) {
            throw new IllegalArgumentException("Exam, Connection không được null");
        }

        this.connection = connection;

        // Khởi tạo controller phụ thuộc connection
        this.userController = new UserController(connection);
        this.examController = new ExamController(connection);

        // Load dữ liệu bảng
        loadResults(exam);
    }

}
