package com.moe.persistence;

import com.moe.http.service.DatabaseService;
import com.moe.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static UserRepository instance;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (UserRepository.instance == null) {
            UserRepository.instance = new UserRepository();
        }
        return UserRepository.instance;
    }

    public User getUser(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, username, password, token, coins, status FROM users WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = User.builder()
                        .id(rs.getInt(1))
                        .username(rs.getString(2))
                        .password(rs.getString(3))
                        .token(rs.getString(4))
                        .coins(rs.getInt(5))
                        .status(rs.getString(6))
                        .build();

                rs.close();
                ps.close();
                conn.close();

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserWithoutSensibleData(int id) {
        if (id == 0) {
            return null;
        }
        return this.getUser(id).toBuilder().password(null).token(null).build();
    }

    public User getUserByUsername(String username) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, username, password, token, coins, status FROM users WHERE username=?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = User.builder()
                        .id(rs.getInt(1))
                        .username(rs.getString(2))
                        .password(rs.getString(3))
                        .token(rs.getString(4))
                        .coins(rs.getInt(5))
                        .status(rs.getString(6))
                        .build();

                rs.close();
                ps.close();
                conn.close();

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByUsernameWithoutSensibleData(String username) {
        if (username == null) {
            return null;
        }
        return this.getUserByUsername(username).toBuilder().password(null).token(null).build();
    }

    public List<User> getUsers() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("SELECT id, username, password, token, coins, status FROM users;");

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(User.builder()
                        .id(rs.getInt(1))
                        .username(rs.getString(2))
                        .password(rs.getString(3))
                        .token(rs.getString(4))
                        .coins(rs.getInt(5))
                        .status(rs.getString(6))
                        .build());
            }

            rs.close();
            sm.close();
            conn.close();

            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User addUser(User user) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username, password, token, coins, status) VALUES(?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getToken());
            ps.setInt(4, user.getCoins());
            ps.setString(5, user.getStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return this.getUser(generatedKeys.getInt(1));
                }
            }
            ps.close();
            conn.close();
        } catch (SQLException ignored) {

        }
        return null;
    }

    public User updateUser(int id, User user) {
        User oldUser = this.getUser(id);
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET username = ?, password = ?, token = ?, coins = ?, status = ? WHERE id = ?;");

            ps.setString(1, user.getUsername() != null ? user.getUsername() : oldUser.getUsername());
            ps.setString(2, user.getPassword() != null ? user.getPassword() : oldUser.getPassword());
            ps.setString(3, user.getToken() != null ? user.getToken() : oldUser.getToken());
            ps.setInt(4, user.getCoins());
            ps.setString(5, user.getStatus() != null ? user.getStatus() : oldUser.getStatus());
            ps.setInt(6, id);

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }

            return this.getUser(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?;");
            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return false;
            }

            ps.close();
            conn.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
