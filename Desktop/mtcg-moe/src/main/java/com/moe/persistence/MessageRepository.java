package com.moe.persistence;

import com.moe.http.service.DatabaseService;
import com.moe.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    private static MessageRepository instance;

    private MessageRepository() {
    }

    public static MessageRepository getInstance() {
        if (MessageRepository.instance == null) {
            MessageRepository.instance = new MessageRepository();
        }
        return MessageRepository.instance;
    }

    public Message getMessage(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, message FROM messages WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();


            if (rs.next()) {
                Message message = Message.builder()
                        .id(rs.getInt(1))
                        .message(rs.getString(2))
                        .build();

                rs.close();
                ps.close();
                conn.close();

                return message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getMessages() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("SELECT id, message FROM messages;");

            List<Message> messages = new ArrayList<>();
            while (rs.next()) {
                messages.add(Message.builder()
                        .id(rs.getInt(1))
                        .message(rs.getString(2))
                        .build());
            }

            rs.close();
            sm.close();
            conn.close();

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message addMessage(Message message) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO messages(message) VALUES(?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, message.getMessage());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return this.getMessage(generatedKeys.getInt(1));
                }
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message updateMessage(int id, Message message) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE messages SET message = ? WHERE id = ?;");

            ps.setString(1, message.getMessage());
            ps.setInt(2, id);

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }

            return this.getMessage(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteMessage(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM messages WHERE id = ?;");
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
