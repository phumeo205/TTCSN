package com.example.ud_quizzi.view;

import com.example.ud_quizzi.controller.QuestionController;
import com.example.ud_quizzi.controller.QuestionExamController;
import com.example.ud_quizzi.controller.ResultController;
import com.example.ud_quizzi.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.util.*;

public class TakingExamController {

    @FXML private Label lblExamTitle, lblTimer, lblQuestionNumber, lblQuestionContent;
    @FXML private RadioButton radA, radB, radC, radD;
    @FXML private Button btnPrev, btnNext;

    private ToggleGroup toggleGroup;
    private boolean isRestoring = false;

    private List<Question> questionList = new ArrayList<>();
    private Map<Integer, String> userAnswers = new HashMap<>();

    private int currentIndex = 0;
    private int timeSeconds;
    private Timeline timeline;

    private Exam exam;
    private User student;
    private Connection conn;

    public void setupExam(Exam exam, User student, Connection conn) {
        this.exam = exam;
        this.student = student;
        this.conn = conn;

        lblExamTitle.setText("Đề thi: " + exam.getExamName());

        toggleGroup = new ToggleGroup();
        radA.setToggleGroup(toggleGroup);
        radB.setToggleGroup(toggleGroup);
        radC.setToggleGroup(toggleGroup);
        radD.setToggleGroup(toggleGroup);

        // Lắng nghe chọn đáp án
        toggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (isRestoring) return;
            if (newVal != null) {
                RadioButton selected = (RadioButton) newVal;
                // Lấy ký tự đáp án trước dấu chấm (A, B, C, D)
                String ans = selected.getText().split("\\.")[0].trim();
                userAnswers.put(currentIndex, ans);
            }
        });

        loadQuestions();

        this.timeSeconds = exam.getTestTime() * 60;
        startTimer();

        showQuestion(0);
    }

    private void loadQuestions() {
        QuestionExamController qeController = new QuestionExamController(conn);
        QuestionController qController = new QuestionController(conn);

        List<Question_Exam> qeList = qeController.getQuestionExamListByExam(exam.getExamID());

        for (Question_Exam qe : qeList) {
            Question q = qController.getQuestionById(qe.getQuestionID());
            if (q != null) questionList.add(q);
        }

        if (questionList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Đề thi chưa có câu hỏi!");
            alert.showAndWait();
            closeWindow();
            return;
        }

        // Random câu hỏi
        Collections.shuffle(questionList);
    }

    private void startTimer() {
        lblTimer.setText(formatTime(timeSeconds));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeSeconds--;
            lblTimer.setText(formatTime(timeSeconds));

            if (timeSeconds < 60) {
                lblTimer.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 22px;");
            }

            if (timeSeconds <= 0) {
                timeline.stop();
                lockUI();
                handleAutoSubmit();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= questionList.size()) return;

        currentIndex = index;
        Question q = questionList.get(index);

        lblQuestionNumber.setText("Câu " + (index + 1) + "/" + questionList.size());
        lblQuestionContent.setText(q.getContent());

        radA.setText("A. " + q.getOptionA());
        radB.setText("B. " + q.getOptionB());
        radC.setText("C. " + q.getOptionC());
        radD.setText("D. " + q.getOptionD());

        // Khôi phục đáp án
        isRestoring = true;
        toggleGroup.selectToggle(null);

        String saved = userAnswers.get(index);
        if (saved != null) {
            switch (saved) {
                case "A" -> toggleGroup.selectToggle(radA);
                case "B" -> toggleGroup.selectToggle(radB);
                case "C" -> toggleGroup.selectToggle(radC);
                case "D" -> toggleGroup.selectToggle(radD);
            }
        }
        isRestoring = false;

        btnPrev.setDisable(index == 0);
        btnNext.setDisable(index == questionList.size() - 1);
    }

    @FXML
    private void handleNext() {
        if (currentIndex < questionList.size() - 1) {
            showQuestion(currentIndex + 1);
        }
    }

    @FXML
    private void handlePrev() {
        if (currentIndex > 0) {
            showQuestion(currentIndex - 1);
        }
    }

    @FXML
    private void handleSubmit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận nộp bài");
        alert.setHeaderText("Bạn có chắc muốn nộp?");
        alert.setContentText("Sau khi nộp sẽ không thể chỉnh sửa.");

        Optional<ButtonType> rs = alert.showAndWait();
        if (rs.isPresent() && rs.get() == ButtonType.OK) {
            lockUI();
            finishExam();
        }
    }

    private void handleAutoSubmit() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hết giờ");
        alert.setHeaderText(null);
        alert.setContentText("Hết thời gian. Hệ thống sẽ tự động nộp bài.");
        alert.showAndWait();

        finishExam();
    }

    private void lockUI() {
        btnNext.setDisable(true);
        btnPrev.setDisable(true);
        radA.setDisable(true);
        radB.setDisable(true);
        radC.setDisable(true);
        radD.setDisable(true);
    }

    private void finishExam() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }

        int correctCount = 0;

        for (int i = 0; i < questionList.size(); i++) {
            String userAns = userAnswers.get(i);
            String correctAns = questionList.get(i).getAnswer();

            if (userAns != null && correctAns != null &&
                    userAns.trim().equalsIgnoreCase(correctAns.trim())) {
                correctCount++;
            }
        }

        double score = 0;
        if (!questionList.isEmpty()) {
            score = ((double) correctCount / questionList.size()) * 10.0;
            score = Math.round(score * 100.0) / 100.0;
        }

        ResultController rc = new ResultController(conn);
        Result result = new Result(exam.getExamID(), student.getUserID(), score, new java.util.Date());
        boolean saved = rc.addResult(result);

        StringBuilder msg = new StringBuilder();
        msg.append("Số câu đúng: ").append(correctCount).append("/").append(questionList.size()).append("\n");
        msg.append("Điểm: ").append(score).append("\n");

        Alert.AlertType type = Alert.AlertType.INFORMATION;
        if (saved) {
            msg.append("\nKết quả đã được lưu.");
        } else {
            msg.append("\nLỗi khi lưu kết quả!");
            type = Alert.AlertType.ERROR;
        }

        Alert alert = new Alert(type);
        alert.setTitle("KẾT QUẢ");
        alert.setHeaderText("Hoàn thành bài thi: " + exam.getExamName());
        alert.setContentText(msg.toString());
        alert.showAndWait();

        closeWindow();
    }

    private void closeWindow() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        Stage stage = (Stage) lblExamTitle.getScene().getWindow();
        stage.close();
    }
}
