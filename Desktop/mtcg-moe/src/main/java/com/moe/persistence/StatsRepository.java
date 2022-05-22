package com.moe.persistence;

import com.moe.http.service.DatabaseService;
import com.moe.model.Stats;
import com.moe.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;

public class StatsRepository {
    private static StatsRepository instance;
    private final UserRepository userRepository;

    private StatsRepository() {
        userRepository = UserRepository.getInstance();
    }

    public static StatsRepository getInstance() {
        if (StatsRepository.instance == null) {
            StatsRepository.instance = new StatsRepository();
        }
        return StatsRepository.instance;
    }

    public Stats getStatsForUser(User user) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT total_battles, won_battles, lost_battles, elo FROM users WHERE id = ?;");
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Stats stats = Stats.builder()
                        .totalBattles(rs.getInt(1))
                        .wonBattles(rs.getInt(2))
                        .lostBattles(rs.getInt(3))
                        .elo(rs.getInt(4))
                        .build();

                rs.close();
                ps.close();
                conn.close();

                return stats;
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Stats addStatForUser(User user, int stat) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps;

            if (stat > 0) {
                // Win
                ps = conn.prepareStatement("UPDATE users SET won_battles = won_battles+1, total_battles = total_battles+1 WHERE id = ?;");
            } else if (stat < 0) {
                // Loss
                ps = conn.prepareStatement("UPDATE users SET lost_battles = lost_battles+1, total_battles = total_battles+1 WHERE id = ?;");
            } else {
                // Tie
                ps = conn.prepareStatement("UPDATE users SET total_battles = total_battles+1 WHERE id = ?;");
            }

            ps.setInt(1, user.getId());

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows > 0) {
                return this.getStatsForUser(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateEloForPlayers(User playerA, User playerB, double pA, double pB) {
        int eloA = this.getStatsForUser(playerA).getElo();
        int eloB = this.getStatsForUser(playerB).getElo();

        double eA = 1 / (1 + Math.pow(10, (eloB - eloA) / 400.0));
        double eB = 1 - eA;

        int rEloA = (int) Math.round(eloA + 10 * (pA - eA));
        int rEloB = (int) Math.round(eloB + 10 * (pB - eB));

        try {
            Connection conn = DatabaseService.getInstance().getConnection();

            PreparedStatement ps = conn.prepareStatement("UPDATE users SET elo = ? WHERE id = ?;");
            ps.setInt(1, rEloA);
            ps.setInt(2, playerA.getId());
            if (ps.executeUpdate() <= 0) {
                return false;
            }

            ps = conn.prepareStatement("UPDATE users SET elo = ? WHERE id = ?;");
            ps.setInt(1, rEloB);
            ps.setInt(2, playerB.getId());
            if (ps.executeUpdate() <= 0) {
                return false;
            }

            ps.close();
            conn.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public JsonArray getScoreboard() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("SELECT username, elo, total_battles, won_battles, lost_battles FROM users ORDER BY elo DESC;");

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                JsonObject innerJsonObject = new JsonObject();
                innerJsonObject.addProperty("username", rs.getString(1));
                innerJsonObject.addProperty("elo", rs.getInt(2));
                innerJsonObject.addProperty("total_battles", rs.getInt(3));
                innerJsonObject.addProperty("won_battles", rs.getInt(4));
                innerJsonObject.addProperty("lost_battles", rs.getInt(5));

                jsonArray.add(innerJsonObject);
            }

            rs.close();
            sm.close();
            conn.close();

            return jsonArray;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
