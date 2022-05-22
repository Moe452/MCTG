package com.moe.persistence;

import com.moe.http.service.DatabaseService;
import com.moe.model.Battle;
import com.moe.model.BattleRound;
import com.moe.model.Card;
import com.moe.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleRepository {
    private static BattleRepository instance;
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final StatsRepository statsRepository;

    private BattleRepository() {
        userRepository = UserRepository.getInstance();
        deckRepository = DeckRepository.getInstance();
        cardRepository = CardRepository.getInstance();
        statsRepository = StatsRepository.getInstance();
    }

    public static BattleRepository getInstance() {
        if (BattleRepository.instance == null) {
            BattleRepository.instance = new BattleRepository();
        }
        return BattleRepository.instance;
    }

    public Battle createOrAddUserToBattle(User user) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("SELECT id FROM battles WHERE player_a IS NULL OR player_b IS NULL LIMIT 1;");

            Battle battle;
            if (rs.next()) {
                // Get existing battle
                battle = this.getBattle(rs.getInt(1));
            } else {
                // Create new battle
                battle = this.addBattle(Battle.builder().build());
            }

            // Now add user to battle
            if (this.addUserToBattle(user, battle)) {
                return this.getBattle(battle.getId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Battle getBattle(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, player_a, player_b, winner, finished FROM battles WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            List<BattleRound> battleRounds = new ArrayList<>();

            if (rs.next()) {

                int battleId = rs.getInt(1);

                User playerA = userRepository.getUserWithoutSensibleData(rs.getInt(2));
                User playerB = userRepository.getUserWithoutSensibleData(rs.getInt(3));
                User winner = userRepository.getUserWithoutSensibleData(rs.getInt(4));

                PreparedStatement ps2 = conn.prepareStatement("SELECT id, card_a, card_b, winner_card FROM battle_rounds WHERE battle_id=?;");
                ps2.setInt(1, battleId);
                ResultSet rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    Card cardA = cardRepository.getCard(rs2.getInt(2));
                    Card cardB = cardRepository.getCard(rs2.getInt(3));
                    Card winnerCard = cardRepository.getCard(rs2.getInt(4));
                    battleRounds.add(BattleRound.builder()
                            .id(rs2.getInt(1))
                            .cardA(cardA)
                            .cardB(cardB)
                            .winnerCard(winnerCard)
                            .build());
                }

                Battle battle = Battle.builder()
                        .id(battleId)
                        .playerA(playerA)
                        .playerB(playerB)
                        .winner(winner)
                        .battleRounds(battleRounds)
                        .finished(rs.getBoolean(5))
                        .build();

                rs2.close();
                ps2.close();
                rs.close();
                ps.close();
                conn.close();

                return battle;
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Battle addBattle(Battle battle) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO battles VALUES(DEFAULT);", Statement.RETURN_GENERATED_KEYS);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return this.getBattle(generatedKeys.getInt(1));
                }
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUserToBattle(User user, Battle battle_) {

        Battle battle = (Battle) this.getBattle(battle_.getId());

        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            int affectedRows;

            if (battle.getPlayerA() == null) {
                // Set user as playerA
                PreparedStatement ps = conn.prepareStatement("UPDATE battles SET player_a = ? WHERE id = ?;");
                ps.setInt(1, user.getId());
                ps.setInt(2, battle.getId());
                affectedRows = ps.executeUpdate();
                ps.close();
            } else if (battle.getPlayerB() == null) {
                // Set user as playerB
                PreparedStatement ps = conn.prepareStatement("UPDATE battles SET player_b = ? WHERE id = ?;");
                ps.setInt(1, user.getId());
                ps.setInt(2, battle.getId());
                affectedRows = ps.executeUpdate();
                ps.close();
            } else {
                conn.close();
                return false;
            }

            conn.close();

            if (affectedRows > 0) {
                battle = this.getBattle(battle_.getId());
                // Check if the battle is full now
                if (battle.getPlayerA() != null && battle.getPlayerB() != null) {
                    // Now start the battle
                    this.battle(battle);
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean battle(Battle battle) {
        User playerA = battle.getPlayerA();
        User playerB = battle.getPlayerB();
        User winner = null;

        ArrayList<Card> deckA = (ArrayList<Card>) deckRepository.getDeck(playerA);
        ArrayList<Card> deckB = (ArrayList<Card>) deckRepository.getDeck(playerB);

        // Check if decks are complete
        if (deckA.size() != 4 || deckB.size() != 4) {
            return false;
        }

        Card cardA;
        Card cardB;
        Card winnerCard;

        System.out.println(playerA.getUsername() + " vs. " + playerB.getUsername());

        for (int i = 0; i < 100; i++) {
            // Check for winners
            if (deckA.size() == 0) {
                // Deck A is empty, therefore player B won.
                winner = playerB;
                // Update stats
                statsRepository.addStatForUser(playerB, 1);
                statsRepository.addStatForUser(playerA, -1);
                // Update elo
                statsRepository.updateEloForPlayers(playerA, playerB, 0.0, 1.0);
                System.out.println("Player A won.");
                break;
            } else if (deckB.size() == 0) {
                // Deck B is empty, therefore player A won.
                winner = playerA;
                // Update stats
                statsRepository.addStatForUser(playerA, 1);
                statsRepository.addStatForUser(playerB, -1);
                // Update elo
                statsRepository.updateEloForPlayers(playerA, playerB, 1.0, 0.0);
                System.out.println("Player B won.");
                break;
            }

            cardA = deckA.get(new Random().nextInt(deckA.size()));
            cardB = deckB.get(new Random().nextInt(deckB.size()));
            winnerCard = null;

            if (cardA.winsAgainst(cardB) || cardA.calculateDamage(cardB) > cardB.calculateDamage(cardA)) {
                // Player A wins this round, and gets cardB
                winnerCard = cardA;
                deckB.remove(cardB);
                deckA.add(cardB);
            } else if (cardB.getDamage() > cardA.getDamage()) {
                // Player B wins this round, and gets cardA
                winnerCard = cardB;
                deckA.remove(cardA);
                deckB.add(cardA);
            }

            if (winnerCard != null) {
                System.out.println("Winner: " + winnerCard.getName() + "(" + winnerCard.getDamage() + ")");
            }

            this.addBattleRound(battle, cardA, cardB, winnerCard);
        }

        // Transfer cards from current decks to users
        for (Card card : deckA) {
            cardRepository.addCardToUser(card, playerA);
        }
        for (Card card : deckB) {
            cardRepository.addCardToUser(card, playerB);
        }

        // Clear deck
        deckRepository.clearDeck(playerA);
        deckRepository.clearDeck(playerB);

        // Update stats, tie
        if (winner == null) {
            statsRepository.addStatForUser(playerA, 0);
            statsRepository.addStatForUser(playerB, 0);
            statsRepository.updateEloForPlayers(playerA, playerB, 0.5, 0.5);
        }

        return this.setWinnerForBattle(winner, battle);
    }

    public boolean addBattleRound(Battle battle, Card cardA, Card cardB, Card winnerCard) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO battle_rounds(battle_id, card_a, card_b, winner_card) VALUES(?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, battle.getId());
            ps.setInt(2, cardA.getId());
            ps.setInt(3, cardB.getId());

            if (winnerCard != null) {
                ps.setInt(4, winnerCard.getId());
            } else {
                ps.setNull(4, java.sql.Types.NULL);
            }

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setWinnerForBattle(User winner, Battle battle) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE battles SET winner = ?, finished = TRUE WHERE id = ?;");
            ps.setInt(2, battle.getId());

            if (winner != null) {
                // Update winner
                ps.setInt(1, winner.getId());
            } else {
                // It's a draw.
                ps.setNull(1, java.sql.Types.NULL);
            }

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Battle waitForBattleToFinish(Battle battle) {
        Battle currentBattle;
        for (int i = 0; i < 60; i++) {
            currentBattle = (Battle) this.getBattle(battle.getId());
            if (currentBattle.isFinished()) {
                return currentBattle;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
