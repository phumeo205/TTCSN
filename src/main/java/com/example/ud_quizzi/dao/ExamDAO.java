package com.example.ud_quizzi.dao;

import com.example.ud_quizzi.model.Exam;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    private final Connection conn;

    public ExamDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(Exam e) throws SQLException {
        String sql = "INSERT INTO Exams(exam_name, created_by, created_date, test_time) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getExamName());
            ps.setString(2, e.getCreatedBy());
            ps.setTimestamp(3, new Timestamp(e.getCreatedDate().getTime()));
            ps.setInt(4, e.getTestTime());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Exam> getAll() throws SQLException {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT * FROM Exams";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Exam getById(int id) throws SQLException {
        String sql = "SELECT * FROM Exams WHERE exam_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM Exams WHERE exam_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Exam map(ResultSet rs) throws SQLException {
        return new Exam(
                rs.getInt("exam_id"),
                rs.getString("exam_name"),
                rs.getString("created_by"),
                rs.getTimestamp("created_date"),
                rs.getInt("test_time")
        );
    }

    public Connection getConnection() {
        return this.conn;
    }
}
