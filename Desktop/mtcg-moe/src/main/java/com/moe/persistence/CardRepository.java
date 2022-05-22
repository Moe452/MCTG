package com.moe.persistence;

import com.moe.http.service.DatabaseService;
import com.moe.model.Card;
import com.moe.model.Package;
import com.moe.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {
    private static CardRepository instance;

    private CardRepository() {

    }

    public static CardRepository getInstance() {
        if (CardRepository.instance == null) {
            CardRepository.instance = new CardRepository();
        }
        return CardRepository.instance;
    }

    public Card getCard(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, name, damage, card_type, element_type, is_locked FROM cards WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Card card = Card.fromPrimitives(
                        rs.getInt(1), // id
                        rs.getString(2), // name
                        rs.getFloat(3), // damage
                        rs.getString(4), // card_type
                        rs.getString(5), // element_type
                        rs.getBoolean(6)); // is_locked

                rs.close();
                ps.close();
                conn.close();

                return card;
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Card> getCards() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("SELECT id, name, damage, card_type, element_type, is_locked FROM cards;");

            List<Card> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(Card.fromPrimitives(
                        rs.getInt(1), // id
                        rs.getString(2), // name
                        rs.getFloat(3), // damage
                        rs.getString(4), // card_type
                        rs.getString(5), // element_type
                        rs.getBoolean(6))); // is_locked
            }

            rs.close();
            sm.close();
            conn.close();

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Card> getCardsForUser(User user) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, name, damage, card_type, element_type, is_locked FROM cards WHERE user_id = ?;");
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();

            List<Card> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(Card.fromPrimitives(
                        rs.getInt(1), // id
                        rs.getString(2), // name
                        rs.getFloat(3), // damage
                        rs.getString(4), // card_type
                        rs.getString(5), // element_type
                        rs.getBoolean(6))); // is_locked
            }

            rs.close();
            ps.close();
            conn.close();

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Card> getCardsForPackage(Package cardPackage) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, name, damage, card_type, element_type, is_locked FROM cards WHERE package_id = ?;");
            ps.setInt(1, cardPackage.getId());
            ResultSet rs = ps.executeQuery();

            List<Card> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(Card.fromPrimitives(
                        rs.getInt(1), // id
                        rs.getString(2), // name
                        rs.getFloat(3), // damage
                        rs.getString(4), // card_type
                        rs.getString(5), // element_type
                        rs.getBoolean(6))); // is_locked
            }

            rs.close();
            ps.close();
            conn.close();

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Card addCard(Card card) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO cards(name, damage, element_type, card_type, package_id, user_id) VALUES(?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, card.getName());
            ps.setFloat(2, card.getDamage());
            ps.setString(3, card.getElementType().toString());
            ps.setString(4, card.getCardType().toString());
            ps.setNull(5, java.sql.Types.NULL);
            ps.setNull(6, java.sql.Types.NULL);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return this.getCard(generatedKeys.getInt(1));
                }
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteCard(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM cards WHERE id = ?;");
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

    public Card addCardToPackage(Card card, Package cardPackage) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE cards SET package_id = ? WHERE id = ?;");
            ps.setInt(1, cardPackage.getId());
            ps.setInt(2, card.getId());

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }
            return this.getCard(card.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Card addCardToUser(Card card, User user) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE cards SET package_id = NULL, user_id = ? WHERE id = ?;");
            ps.setInt(1, user.getId());
            ps.setInt(2, card.getId());

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }
            return this.getCard(card.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Card lockCard(Card card, boolean isLocked) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE cards SET is_locked = ? WHERE id = ?;");
            ps.setBoolean(1, isLocked);
            ps.setInt(2, card.getId());

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }
            return this.getCard(card.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
