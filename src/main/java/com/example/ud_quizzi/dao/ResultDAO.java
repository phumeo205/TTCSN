package com.example.ud_quizzi.dao;

import com.example.ud_quizzi.model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    private final Connection conn;

    public ResultDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(Result r) throws SQLException {
        String sql = "INSERT INTO Results(exam_id, student_id, score, submitted_date) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getExamID());
            ps.setInt(2, r.getStudentID());
            ps.setDouble(3, r.getScore());
            ps.setDate(4, new java.sql.Date(r.getSubmittedDate().getTime()));
            return ps.executeUpdate() > 0;
        }
    }

    // Thêm vào ResultDAO.java
    public List<Result> getAll() throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT * FROM Results";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Result> getByStudent(int studentId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT * FROM Results WHERE student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Result> getByExam(int examId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT * FROM Results WHERE exam_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    private Result map(ResultSet rs) throws SQLException {
        return new Result(
                rs.getInt("result_id"),
                rs.getInt("exam_id"),
                rs.getInt("student_id"),
                rs.getDouble("score"),
                rs.getDate("submitted_date")
        );
    }

    /**
     * Lấy tất cả kết quả của học sinh kèm tên đề thi
     */
    public List<Result> getResultsWithExamNameByStudent(int studentId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = """
                SELECT r.result_id, r.exam_id, r.student_id, r.score, r.submitted_date,
                       e.exam_name
                FROM Results r
                JOIN Exams e ON r.exam_id = e.exam_id
                WHERE r.student_id = ?
                ORDER BY r.submitted_date DESC
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Result r = new Result(
                        rs.getInt("result_id"),
                        rs.getInt("exam_id"),
                        rs.getInt("student_id"),
                        rs.getDouble("score"),
                        rs.getDate("submitted_date")
                );
                r.setDescription(rs.getString("exam_name")); // lưu tên đề thi
                list.add(r);
            }
        }
        return list;
    }

    // Tính tổng số lượt tham gia của học sinh
    public int getTotalAttemptsForExam(int examID) {
        int total = 0;
        try {
            String sql = "SELECT COUNT(*) FROM Results WHERE exam_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // Tính số lượt tham gia của học sinh không tính lượt làm lại
    public int getUniqueStudentsForExam(int examID) {
        int total = 0;
        try {
            String sql = "SELECT COUNT(DISTINCT student_id) FROM Results WHERE exam_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public List<Result> getAllResults() {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT * FROM Results ORDER BY submitted_date DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Result r = new Result(
                        rs.getInt("result_id"),
                        rs.getInt("exam_id"),
                        rs.getInt("student_id"),
                        rs.getDouble("score"),
                        rs.getDate("submitted_date")
                );
                list.add(r);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Result> getResultsByExamId(int examId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT * FROM results WHERE exam_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Result r = new Result(
                        rs.getInt("exam_id"),
                        rs.getInt("student_id"),
                        rs.getDouble("score"),
                        rs.getTimestamp("submitted_date")
                );
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
