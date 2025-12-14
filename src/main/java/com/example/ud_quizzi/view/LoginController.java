package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.UserController;
import com.example.ud_quizzi.dao.DatabaseConnection;
import com.example.ud_quizzi.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private ImageView backgroundImage;
    @FXML private ImageView sideImage;

    @FXML
    private void initialize() {
        try {
            URL bgUrl = getClass().getResource("/images/backgroundLogin.png");
            URL sideUrl = getClass().getResource("/images/loginImage.png");

            if (bgUrl != null) backgroundImage.setImage(new Image(bgUrl.toExternalForm()));
            if (sideUrl != null) sideImage.setImage(new Image(sideUrl.toExternalForm()));

        } catch (Exception e) {
            System.out.println("Lỗi load hình ảnh: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            UserController userController = new UserController(conn);
            User user = userController.login(username, password);

            if (user != null) {

                // Lưu session
                CurrentSession.setLoggedInUser(user);

                FXMLLoader loader;
                Parent root;

                // Chọn màn hình theo role
                switch (user.getRole()) {

                    case "teacher" -> {
                        loader = new FXMLLoader(
                                getClass().getResource("/com/example/ud_quizzi/view/TeacherScreen.fxml"));
                        root = loader.load();
                        TeacherController tc = loader.getController();
                        tc.setConnection(conn);
                        tc.setTeacher(user);
                    }

                    case "student" -> {
                        loader = new FXMLLoader(
                                getClass().getResource("/com/example/ud_quizzi/view/StudentScreen.fxml"));
                        root = loader.load();
                        StudentController sc = loader.getController();
                        sc.setConnection(conn);
                        sc.setStudent(user);
                    }

                    default -> {  // admin
                        loader = new FXMLLoader(
                                getClass().getResource("/com/example/ud_quizzi/view/AdminScreen.fxml"));
                        root = loader.load();
                        ManageUserController ac = loader.getController();
                        ac.setConnection(conn);
                        // Load data trong initialize hoặc setConnection
                    }
                }

                // Đổi màn hình
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard - " + user.getRole());
                stage.centerOnScreen();
                stage.show();

            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Sai username hoặc password!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Lỗi đăng nhập hoặc kết nối database!");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/ud_quizzi/view/RegisterScreen.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng ký người dùng mới");
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Không mở được trang đăng ký!");
        }
    }
}
