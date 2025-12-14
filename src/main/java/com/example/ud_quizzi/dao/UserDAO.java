package com.example.ud_quizzi.dao;

import com.example.ud_quizzi.model.User;
import org.mindrot.jbcrypt.BCrypt; // Import thư viện mã hóa

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Khai báo biến kết nối (quan trọng để sửa lỗi 'conn')
    private final Connection conn;

    // Constructor nhận kết nối
    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    // 1. Phương thức insert (Đăng ký) - Đã sửa để mã hóa mật khẩu
    public boolean insert(User u) throws SQLException {
        String sql = "INSERT INTO Users(username, password, full_name, email, phone, role) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());

            // --- MÃ HÓA MẬT KHẨU TẠI ĐÂY ---
            // Tạo salt và hash password
            String hashedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());
            ps.setString(2, hashedPassword);

            ps.setString(3, u.getFullName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPhone());
            ps.setString(6, u.getRole());
            return ps.executeUpdate() > 0;
        }
    }

    // 2. Phương thức login (Đăng nhập) - Đã sửa lỗi Invalid salt version
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                boolean isMatch = false;

                // Kiểm tra xem mật khẩu trong DB có phải là Hash BCrypt không (bắt đầu bằng $2)
                if (dbPassword != null && dbPassword.startsWith("$2")) {
                    try {
                        isMatch = BCrypt.checkpw(password, dbPassword);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Lỗi định dạng mật khẩu: " + e.getMessage());
                    }
                } else {
                    // Nếu không phải hash (dữ liệu cũ), so sánh chuỗi thường
                    if (dbPassword != null && dbPassword.equals(password)) {
                        isMatch = true;
                        //Tại đây có thể gọi hàm update lại mật khẩu thành hash để bảo mật
                    }
                }

                if (isMatch) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    // Các phương thức khác giữ nguyên
    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public boolean update(User u) throws SQLException {
        //Nếu cập nhật mật khẩu thì cũng cần mã hóa lại ở đây
        String sql = "UPDATE Users SET password=?, full_name=?, email=?, phone=?, role=? WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getPassword());
            ps.setString(2, u.getFullName());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getRole());
            ps.setInt(6, u.getUserID());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Hàm map này quan trọng để sửa lỗi 'map' báo đỏ
    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("role")
        );
    }
}