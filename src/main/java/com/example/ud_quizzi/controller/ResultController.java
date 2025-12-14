package com.example.ud_quizzi.controller;

import com.example.ud_quizzi.dao.ResultDAO;
import com.example.ud_quizzi.model.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ResultController {
    private final ResultDAO resultDAO;

    public ResultController(Connection conn) {
        this.resultDAO = new ResultDAO(conn);
    }

    public boolean addResult(Result result) {
        try {
            return resultDAO.insert(result);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Result> getResultsByExam(int examId) {
        try {
            return resultDAO.getByExam(examId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Result> getResultsByStudent(int studentId) {
        try {
            return resultDAO.getByStudent(studentId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getTotalAttemptsForExam(int examID) {
        return resultDAO.getTotalAttemptsForExam(examID);
    }

    public int getUniqueStudentsForExam(int examID) {
        return resultDAO.getUniqueStudentsForExam(examID);
    }
}
