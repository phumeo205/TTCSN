package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.ExamController;
import com.example.ud_quizzi.controller.ResultController;
import com.example.ud_quizzi.model.Exam;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.List;

public class ManageResultController {

    @FXML private TableView<Exam> tableResults;
    @FXML private TableColumn<Exam, String> colExamName;
    @FXML private TableColumn<Exam, Integer> colTotalAttempts;
    @FXML private TableColumn<Exam, Integer> colUniqueStudents;

    private ExamController examController;
    private ResultController resultController;
    private Connection connection;

    // Gọi khi TeacherController truyền connection
    public void setConnection(Connection conn) {
        this.connection = conn;
        this.examController = new ExamController(conn);
        this.resultController = new ResultController(conn);
        loadResults();
    }

    private void loadResults() {
        try {
            List<Exam> exams = examController.getAllExams();
            tableResults.setItems(FXCollections.observableArrayList(exams));

            colExamName.setCellValueFactory(cell ->
                    new SimpleStringProperty(cell.getValue().getExamName()));

            colTotalAttempts.setCellValueFactory(cell ->
                    new SimpleIntegerProperty(
                            resultController.getTotalAttemptsForExam(cell.getValue().getExamID())
                    ).asObject()
            );

            colUniqueStudents.setCellValueFactory(cell ->
                    new SimpleIntegerProperty(
                            resultController.getUniqueStudentsForExam(cell.getValue().getExamID())
                    ).asObject()
            );

            tableResults.setRowFactory(tv -> {
                TableRow<Exam> row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && !row.isEmpty()) {
                        openList(row.getItem());
                    }
                });
                return row;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opening ResultListScreen
    private void openList(Exam exam) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/ud_quizzi/view/ResultListScreen.fxml")
            );
            Parent root = loader.load();

            ResultListController controller = loader.getController();
            controller.setExamData(exam, connection);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết kết quả: " + exam.getExamName());
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.showAndWait();

            // Reload after closing popup
            loadResults();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể mở danh sách kết quả!");
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) tableResults.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }
}
