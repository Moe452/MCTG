package com.moe.persistence;

import com.moe.http.service.DatabaseService;
import com.moe.model.Card;
import com.moe.model.Trade;
import com.moe.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TradeRepository {
    private static TradeRepository instance;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    private TradeRepository() {
        cardRepository = CardRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    public static TradeRepository getInstance() {
        if (TradeRepository.instance == null) {
            TradeRepository.instance = new TradeRepository();
        }
        return TradeRepository.instance;
    }

    public Trade getTrade(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, card_a, card_b, coins, accepted FROM trades WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Card cardA = cardRepository.getCard(rs.getInt(2));
                Card cardB = cardRepository.getCard(rs.getInt(3));

                Trade trade = Trade.builder()
                        .id((rs.getInt(1)))
                        .cardA(cardA)
                        .cardB(cardB)
                        .coins(rs.getInt(4))
                        .accepted(rs.getBoolean(5))
                        .build();

                rs.close();
                ps.close();
                conn.close();

                return trade;
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Trade> getTrades() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM trades;");
            ResultSet rs = ps.executeQuery();

            List<Trade> trades = new ArrayList<>();
            while (rs.next()) {
                trades.add(this.getTrade(rs.getInt(1)));
            }

            rs.close();
            ps.close();
            conn.close();

            return trades;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Trade addTrade(Card card) {
        if (!card.isLocked()) {
            try {
                Connection conn = DatabaseService.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO trades(card_a) VALUES(?);", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, card.getId());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    return null;
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        ps.close();
                        conn.close();

                        cardRepository.lockCard(card, true);

                        return this.getTrade(id);
                    }
                }
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean deleteTrade(int id) {
        // Unlock cards
        Trade trade = (Trade) this.getTrade(id);
        if (trade != null) {
            Card cardA = trade.getCardA();
            if (cardA != null) {
                cardRepository.lockCard(cardA, false);
            }
            Card cardB = trade.getCardB();
            if (cardB != null) {
                cardRepository.lockCard(cardB, false);
            }
        }

        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM trades WHERE id = ?;");
            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Trade addOffer(Trade trade, Card card, int coins) {
        if (!card.isLocked()) {
            try {
                Connection conn = DatabaseService.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement("UPDATE trades SET card_b = ?, coins = ? WHERE id = ?;");
                ps.setInt(1, card.getId());
                ps.setInt(2, coins);
                ps.setInt(3, trade.getId());

                int affectedRows = ps.executeUpdate();

                ps.close();
                conn.close();

                if (affectedRows > 0) {
                    return this.getTrade(trade.getId());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Trade acceptTrade(Trade trade) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT a.user_id, b.user_id FROM trades JOIN cards a on trades.card_a = a.id JOIN cards b ON trades.card_b = b.id WHERE trades.id=?;");
            ps.setInt(1, trade.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User userA = (User) userRepository.getUser(rs.getInt(1));
                User userB = (User) userRepository.getUser(rs.getInt(2));

                cardRepository.addCardToUser(trade.getCardB(), userA);
                cardRepository.addCardToUser(trade.getCardA(), userB);
                cardRepository.lockCard(trade.getCardB(), false);
                cardRepository.lockCard(trade.getCardA(), false);

                ps = conn.prepareStatement("UPDATE trades SET accepted = TRUE WHERE id=?;");
                ps.setInt(1, trade.getId());
                int affectedRows = ps.executeUpdate();
                if (affectedRows != 0) {
                    return this.getTrade(trade.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
