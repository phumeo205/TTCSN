package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.UserController;
import com.example.ud_quizzi.model.User;
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

public class ManageUserController {

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Integer> colUserID;
    @FXML private TableColumn<User, String> colUserName;
    @FXML private TableColumn<User, String> colPassword;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colRole;

    private UserController userController;
    private ObservableList<User> userList;
    private Connection connection;

    @FXML
    private void initialize() {
        colUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    // ✅ Giữ nguyên setConnection nhưng gọi loadUsers để hiển thị dữ liệu ngay sau login
    public void setConnection(Connection conn) {
        this.connection = conn;
        this.userController = new UserController(conn);
        loadUsers();
    }

    private void loadUsers() {
        if (userController == null) return;
        try {
            List<User> list = userController.getAllUsers();
            userList = FXCollections.observableArrayList(list);
            tableUsers.setItems(userList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Không thể tải danh sách người dùng!");
        }
    }

    @FXML
    public void handleAdd(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ud_quizzi/view/RegisterScreen.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng ký người dùng mới");
            stage.centerOnScreen();
            stage.showAndWait(); // chờ đóng màn hình đăng ký

            loadUsers(); // ✅ reload dữ liệu sau khi đóng
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Không thể mở màn hình đăng ký!");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Vui lòng chọn 1 người dùng để xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa tài khoản này?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = userController.deleteUser(selected.getUserID());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "✅ Đã xóa người dùng!");
                loadUsers(); // ✅ reload dữ liệu sau khi xóa
            } else {
                showAlert(Alert.AlertType.ERROR, "❌ Không thể xóa người dùng!");
            }
        }
    }

    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ud_quizzi/view/LoginScreen.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

            Stage currentStage = (Stage) tableUsers.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Không thể tải màn hình đăng nhập!");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshTable() {
        loadUsers();
    }
}
