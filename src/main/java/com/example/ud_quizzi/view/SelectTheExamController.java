package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.ExamController;
import com.example.ud_quizzi.model.Exam;
import com.example.ud_quizzi.model.User;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.text.SimpleDateFormat;
import java.util.List;

public class SelectTheExamController {

    @FXML private TableView<Exam> tableExams;
    @FXML private TableColumn<Exam, String> colExamName;
    @FXML private TableColumn<Exam, String> colDate;
    @FXML private TableColumn<Exam, Integer> colTime;

    private Connection connection;
    private User student;
    private ExamController examController;

    private final ObservableList<Exam> examList = FXCollections.observableArrayList();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    private void initialize() {
        colExamName.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getExamName()));
        colDate.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getCreatedDate() != null ? sdf.format(c.getValue().getCreatedDate()) : ""
        ));
        colTime.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTestTime()));

        tableExams.setItems(examList);

        tableExams.setRowFactory(tv -> {
            TableRow<Exam> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    openExamDetail(row.getItem());
                }
            });
            return row;
        });
    }

    public void setConnection(Connection conn) {
        this.connection = conn;
        if (conn != null) {
            this.examController = new ExamController(conn);
        }
        refresh();
    }

    public void setStudent(User student) {
        this.student = student;
        refresh();
    }

    // Phương thức load dữ liệu mới
    public void refresh() {
        if (connection == null || student == null) return;
        try {
            List<Exam> list = examController.getAllExams();
            examList.clear();
            examList.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể tải danh sách đề thi!");
        }
    }

    private void openExamDetail(Exam exam) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/ud_quizzi/view/ExamDetailCard.fxml")
            );
            Parent root = loader.load();

            ExamDetailCardController controller = loader.getController();
            controller.setExamData(exam, student, connection);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết đề thi: " + exam.getExamName());
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.showAndWait(); // chờ popup đóng

            refresh(); // load lại dữ liệu sau khi xem xong
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể mở chi tiết đề thi!");
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) tableExams.getScene().getWindow();
        stage.close();
    }
}
