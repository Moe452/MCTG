package com.moe.persistence;

import com.moe.http.service.DatabaseService;
import com.moe.model.Card;
import com.moe.model.Package;
import com.moe.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageRepository {
    private static PackageRepository instance;

    private UserRepository userRepository;
    private CardRepository cardRepository;

    private PackageRepository() {
        userRepository = UserRepository.getInstance();
        cardRepository = CardRepository.getInstance();
    }

    public static PackageRepository getInstance() {
        if (PackageRepository.instance == null) {
            PackageRepository.instance = new PackageRepository();
        }
        return PackageRepository.instance;
    }

    public Package getPackage(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, name, price FROM packages WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Package cardPackage = Package.builder()
                        .id(rs.getInt(1))
                        .name(rs.getString(2))
                        .price(rs.getInt(3))
                        .build();
                rs.close();
                ps.close();
                conn.close();

                return cardPackage;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Package getRandomPackage() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("SELECT id, name, price FROM packages ORDER BY RANDOM() LIMIT 1;");

            if (rs.next()) {
                Package cardPackage = Package.builder()
                        .id(rs.getInt(1))
                        .name(rs.getString(2))
                        .price(rs.getInt(3))
                        .build();
                rs.close();
                sm.close();
                conn.close();

                return cardPackage;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Package> getPackages() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("SELECT id, name, price FROM packages;");

            List<Package> packages = new ArrayList<>();
            while (rs.next()) {
                packages.add(Package.builder()
                        .id(rs.getInt(1))
                        .name(rs.getString(2))
                        .price(rs.getInt(3))
                        .build());
            }

            rs.close();
            sm.close();
            conn.close();

            return packages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Package addPackage(Package cardPackage) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO packages(name, price) VALUES(?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cardPackage.getName());
            ps.setInt(2, cardPackage.getPrice());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return this.getPackage(generatedKeys.getInt(1));
                }
            }
            ps.close();
            conn.close();
        } catch (SQLException ignored) {

        }
        return null;
    }

    public boolean deletePackage(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM packages WHERE id = ?;");
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

    public boolean addPackageToUser(Package cardPackage, User user) {
        // Not enough coins
        if (user.getCoins() < cardPackage.getPrice()) return false;

        // Update coin balance
        user.setCoins(user.getCoins() - cardPackage.getPrice());

        // Save user
        userRepository.updateUser(user.getId(), user);

        for (Card card : cardRepository.getCardsForPackage(cardPackage)) {
            cardRepository.addCardToUser(card, user);
        }

        this.deletePackage(cardPackage.getId());

        return true;
    }
}
