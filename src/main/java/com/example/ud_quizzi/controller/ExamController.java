package com.example.ud_quizzi.controller;

import com.example.ud_quizzi.dao.ExamDAO;
import com.example.ud_quizzi.model.Exam;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamController {

    private final ExamDAO examDAO;

    public ExamController(Connection conn) {
        this.examDAO = new ExamDAO(conn);
    }

    //Thêm đề thi (cách 1): truyền đối tượng Exam
    public boolean addExam(Exam exam) {
        try {
            return examDAO.insert(exam);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Thêm đề thi (cách 2): truyền trực tiếp dữ liệu
    public boolean addExam(String examName, String createdBy, Date createdDate, int testTime) {
        try {
            Exam exam = new Exam(0, examName, createdBy, createdDate, testTime);
            return examDAO.insert(exam);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Lấy danh sách tất cả đề thi
    public List<Exam> getAllExams() {
        try {
            return examDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    //Lấy đề thi theo ID
    public Exam getExamById(int id) {
        try {
            return examDAO.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Xóa đề thi
    public boolean deleteExam(int id) {
        try {
            return examDAO.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        return examDAO.getConnection();
    }
}
