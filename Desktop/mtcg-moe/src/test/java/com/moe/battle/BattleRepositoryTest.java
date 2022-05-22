package com.moe.battle;

import com.moe.http.service.DatabaseService;
import com.moe.model.Battle;
import com.moe.model.User;
import com.moe.persistence.BattleRepository;
import com.moe.persistence.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class BattleRepositoryTest {

    static BattleRepository battleRepository;
    static UserRepository userRepository;

    @BeforeAll
    static void beforeAll() {
        battleRepository = BattleRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    @BeforeEach
    void beforeEach() {
        //
    }

    @Test
    @DisplayName("Get a single empty battle")
    void testGetBattle__empty() {
        try {
            // arrange
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            sm.executeUpdate("INSERT INTO battles(id) VALUES(-1)");

            // act
            Battle battle = battleRepository.getBattle(-1);

            // cleanup
            sm.executeUpdate("DELETE FROM battles WHERE id = -1");
            sm.close();
            conn.close();

            // assert
            assertNotNull(battle);
            assertEquals(-1, battle.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Get a single battle with players")
    void testGetBattle__withPlayers() {
        try {
            // arrange
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();

            String usernameA = "4f757206c8489b4e605f3a89bdaeabd120b2e30b";
            String usernameB = "7a0d9df39c58ef2439901c07cb496b3f1f030c30";

            User playerA = userRepository.addUser(User.builder().username(usernameA).password("password").build());
            User playerB = userRepository.addUser(User.builder().username(usernameB).password("password").build());

            PreparedStatement ps = conn.prepareStatement("INSERT INTO battles(id, player_a, player_b) VALUES(-1, ?, ?);");
            ps.setInt(1, playerA.getId());
            ps.setInt(2, playerB.getId());
            ps.executeUpdate();

            // act
            Battle battle = battleRepository.getBattle(-1);

            // cleanup
            sm.executeUpdate("DELETE FROM battles WHERE id = -1;");
            userRepository.deleteUser(playerA.getId());
            userRepository.deleteUser(playerB.getId());
            sm.close();
            ps.close();
            conn.close();

            // assert
            assertNotNull(battle);
            assertNotNull(battle.getPlayerA());
            assertNotNull(battle.getPlayerB());
            assertEquals(-1, battle.getId());
            assertEquals(usernameA, battle.getPlayerA().getUsername());
            assertEquals(usernameB, battle.getPlayerB().getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Add a new empty battle")
    void testAddBattle() {
        try {
            // arrange
            Battle newBattle = Battle.builder().build();

            // act
            Battle battle = battleRepository.addBattle(newBattle);

            // assert
            assertNotNull(battle);

            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM battles WHERE id = ?;");
            ps.setInt(1, battle.getId());
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals(battle.getId(), rs.getInt(1));

            // cleanup
            Statement sm = conn.createStatement();
            sm.executeUpdate("DELETE FROM battles WHERE id = " + battle.getId() + ";");
            sm.close();
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Add a user to a battle as player A")
    void testAddUserToBattle__playerA() {
        try {
            // arrange
            Connection conn = DatabaseService.getInstance().getConnection();
            String usernameA = "4f757206c8489b4e605f3a89bdaeabd120b2e30b";
            User player = (User) userRepository.addUser(User.builder().username(usernameA).password("password").build());
            Battle battle = (Battle) battleRepository.addBattle(Battle.builder().build());

            // act
            boolean result = battleRepository.addUserToBattle(player, battle);

            // assert
            PreparedStatement ps = conn.prepareStatement("SELECT id, player_a, player_b, winner FROM battles WHERE id=?;");
            ps.setInt(1, battle.getId());
            ResultSet rs = ps.executeQuery();

            assertTrue(result);
            assertTrue(rs.next());
            assertEquals(player.getId(), rs.getInt(2));
            assertEquals(0, rs.getInt(3));
            assertEquals(0, rs.getInt(4));

            // cleanup
            Statement sm = conn.createStatement();
            sm.executeUpdate("DELETE FROM battles WHERE id = " + battle.getId() + ";");
            userRepository.deleteUser(player.getId());
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Add two users to a battle as player A and B")
    void testAddUserToBattle__playersAB() {
        try {
            // arrange
            Connection conn = DatabaseService.getInstance().getConnection();
            String usernameA = "ea7b8a96743e64d0610f72ad2a691b8f11b782c8";
            String usernameB = "dce0d43d971341031e46f9775cd247308dc7a4f4";

            User playerA = (User) userRepository.addUser(User.builder().username(usernameA).password("password").build());
            User playerB = (User) userRepository.addUser(User.builder().username(usernameB).password("password").build());
            Battle battle = (Battle) battleRepository.addBattle(Battle.builder().build());

            // act
            boolean resultA = battleRepository.addUserToBattle(playerA, battle);
            boolean resultB = battleRepository.addUserToBattle(playerB, battle);

            // assert
            PreparedStatement ps = conn.prepareStatement("SELECT id, player_a, player_b, winner FROM battles WHERE id=?;");
            ps.setInt(1, battle.getId());
            ResultSet rs = ps.executeQuery();

            assertTrue(resultA);
            assertTrue(resultB);
            assertTrue(rs.next());
            assertEquals(playerA.getId(), rs.getInt(2));
            assertEquals(playerB.getId(), rs.getInt(3));

            // cleanup
            Statement sm = conn.createStatement();
            sm.executeUpdate("DELETE FROM battles WHERE id = " + battle.getId() + ";");
            userRepository.deleteUser(playerA.getId());
            userRepository.deleteUser(playerB.getId());
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Create a new battle or add to existing empty battle")
    void testCreateOrAddUserToBattle() {

    }

}
