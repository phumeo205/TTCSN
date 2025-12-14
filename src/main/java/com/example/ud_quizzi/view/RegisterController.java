package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.UserController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private Label messageLabel;

    @FXML private ImageView registerForm; // fix fx:id match FXML

    private UserController userController;

    private static final String DB_HOST = "localhost";
    private static final String DB_INSTANCE = "";
    private static final String DB_NAME = "UD_QUIZZI";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "123456";

    @FXML
    private void initialize() {
        // Init ChoiceBox
        roleChoiceBox.getItems().addAll("teacher", "student", "admin");
        roleChoiceBox.setValue("teacher");

        // Kết nối DB
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://localhost:1433;databaseName=UD_QUIZZI;encrypt=false;trustServerCertificate=true;";
            String user = "sa";          // hoặc user bạn đang dùng trong SSMS
            String password = "123456"; // đổi thành mật khẩu thật
            Connection conn = DriverManager.getConnection(url, user, password);
            userController = new UserController(conn);
            System.out.println("Kết nối CSDL thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Kết nối DB thất bại!");
        }

        // Load hình nền
        URL url = getClass().getResource("/images/registerImage.png");
        if (url != null) {
            registerForm.setImage(new Image(url.toExternalForm()));
        } else {
            System.out.println("Không tìm thấy registerImage.png");
        }
    }

    @FXML
    private void handleRegister() {
        if(userController == null) {
            messageLabel.setText("DB chưa kết nối!");
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = roleChoiceBox.getValue();

        if(username.isEmpty() || password.isEmpty() || fullName.isEmpty()
                || email.isEmpty() || phone.isEmpty()) {
            messageLabel.setText("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        boolean success = userController.registerUser(username, password, fullName, email, phone, role);

        if(success) {
            messageLabel.setText("Đăng ký thành công!");
            clearFields();
        } else {
            boolean exists = userController.usernameExists(username);
            if(exists) {
                messageLabel.setText("Username đã tồn tại!");
            } else {
                messageLabel.setText("Đăng ký thất bại! Kiểm tra DB hoặc tên cột.");
            }
        }
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }


    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        roleChoiceBox.setValue("teacher");
    }
}
