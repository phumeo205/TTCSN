package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.ExamController;
import com.example.ud_quizzi.model.Exam;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ManageExamController {

    @FXML
    private TableView<Exam> tableExams;
    @FXML
    private TableColumn<Exam, String> colExamID;
    @FXML
    private TableColumn<Exam, String> colName;
    @FXML
    private TableColumn<Exam, Date> colDate;
    @FXML
    private TableColumn<Exam, String> colTime;
    @FXML
    private TableColumn<Exam, String> colByName;

    private ExamController examController;
    private ObservableList<Exam> examList;

    @FXML
    private void initialize() {
        colExamID.setCellValueFactory(new PropertyValueFactory<>("examID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("examName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("testTime"));
        colByName.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
    }

    public void setConnection(Connection conn) {
        this.examController = new ExamController(conn);
        loadExams();
    }

    private void loadExams() {
        try {
            if (examController != null) {
                List<Exam> list = examController.getAllExams();
                examList = FXCollections.observableArrayList(list);
                tableExams.setItems(examList);
            } else {
                showAlert(Alert.AlertType.WARNING, "❗ Hệ thống chưa kết nối CSDL!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Không thể tải danh sách đề thi!");
        }
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ud_quizzi/view/AddExamScreen.fxml"));
            Parent root = loader.load();

            AddExamController addController = loader.getController();

            if (this.examController == null) {
                showAlert(Alert.AlertType.ERROR, "❌ Chưa khởi tạo kết nối CSDL!");
                return;
            }

            // 🔹 Truyền connection cho form thêm đề thi
            addController.setConnection(this.examController.getConnection());

            // 🔹 Cho phép form con reload bảng sau khi thêm xong
            addController.setManageController(this);

            Stage stage = new Stage();
            stage.setTitle("Thêm Đề Thi");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Không thể mở giao diện thêm đề thi!");
        }
    }


    @FXML
    private void handleDelete(ActionEvent event) {
        Exam selected = tableExams.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn đề thi để xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa đề thi \"" + selected.getExamName() + "\" ?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = examController.deleteExam(selected.getExamID());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "✅ Đã xóa đề thi!");
                loadExams();
            } else {
                showAlert(Alert.AlertType.ERROR, "❌ Không thể xóa đề thi!");
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) tableExams.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshTable() {
        loadExams();
    }
}
