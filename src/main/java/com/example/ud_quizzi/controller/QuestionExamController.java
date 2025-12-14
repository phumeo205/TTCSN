package com.example.ud_quizzi.controller;

import com.example.ud_quizzi.dao.QuestionExamDAO;
import com.example.ud_quizzi.model.Question;
import com.example.ud_quizzi.model.Question_Exam;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionExamController {

    private final QuestionExamDAO questionExamDAO;
    private final QuestionController questionController;

    public QuestionExamController(Connection conn) {
        this.questionExamDAO = new QuestionExamDAO(conn);
        this.questionController = new QuestionController(conn); // Dùng để load Question
    }

    // Thêm nhiều câu hỏi vào đề thi
    public boolean addQuestionsToExam(List<Question_Exam> selectedQuestions) {
        if (selectedQuestions == null || selectedQuestions.isEmpty()) {
            return false;
        }

        boolean allSuccess = true;

        for (Question_Exam qe : selectedQuestions) {
            try {
                boolean success = questionExamDAO.insert(qe);
                if (!success) allSuccess = false;
            } catch (SQLException e) {
                e.printStackTrace();
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    // Thêm 1 câu hỏi vào đề thi
    public boolean addQuestionToExam(Question_Exam qe) {
        try {
            return questionExamDAO.insert(qe);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách Question_Exam theo examId
    public List<Question_Exam> getQuestionExamListByExam(int examId) {
        try {
            return questionExamDAO.getByExam(examId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy danh sách Question theo examId (dùng để hiển thị)
    public List<Question> getQuestionsByExamId(int examId) {
        try {
            List<Question_Exam> qExamList = questionExamDAO.getByExam(examId);
            List<Question> result = new ArrayList<>();
            for (Question_Exam qe : qExamList) {
                Question q = questionController.getQuestionById(qe.getQuestionID());
                if (q != null) result.add(q);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
