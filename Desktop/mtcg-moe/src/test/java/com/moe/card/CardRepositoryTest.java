package com.moe.card;

import com.moe.http.service.DatabaseService;
import com.moe.model.Card;
import com.moe.model.enums.CardType;
import com.moe.model.enums.ElementType;
import com.moe.persistence.CardRepository;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardRepositoryTest {

    static CardRepository cardRepository;

    @BeforeAll
    static void beforeAll() {
        cardRepository = CardRepository.getInstance();
    }

    @BeforeEach
    void beforeEach() {
        // Delete card with id <0 before every test case
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            sm.executeUpdate("DELETE FROM cards WHERE id < 0;");
            sm.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Get a monster card with the element type water")
    void testGetCard__waterMonster() {
        try {
            // arrange
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            sm.executeUpdate("INSERT INTO cards(id, name, damage, element_type, card_type) VALUES(-1, 'WaterCard', 5, 'WATER', 'MONSTER')");

            // act
            Card card = cardRepository.getCard(-1);

            // cleanup
            sm.executeUpdate("DELETE FROM cards WHERE id = -1;");
            sm.close();
            conn.close();

            // cleanup
            assertEquals("WaterCard", card.getName());
            assertEquals(5.0, card.getDamage());
            Assertions.assertEquals(ElementType.WATER, card.getElementType());
            Assertions.assertEquals(CardType.MONSTER, card.getCardType());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Get a spell card with the element type normal")
    void testGetCard__normalSpell() {
        try {
            // arrange
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            sm.executeUpdate("INSERT INTO cards(id, name, damage, element_type, card_type) VALUES(-1, 'NormalCard', 20, 'NORMAL', 'SPELL')");

            // act
            Card card = (Card) cardRepository.getCard(-1);

            // cleanup
            sm.executeUpdate("DELETE FROM cards WHERE id = -1;");
            sm.close();
            conn.close();

            // cleanup
            assertEquals("NormalCard", card.getName());
            assertEquals(20.0, card.getDamage());
            Assertions.assertEquals(ElementType.NORMAL, card.getElementType());
            Assertions.assertEquals(CardType.SPELL, card.getCardType());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Get all cards")
    void testGetCards() {
        try {
            // arrange
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            sm.executeUpdate("INSERT INTO cards(id, name, damage, element_type, card_type) VALUES(-1, 'WaterCard', 5, 'WATER', 'MONSTER')");
            sm.executeUpdate("INSERT INTO cards(id, name, damage, element_type, card_type) VALUES(-2, 'NormalCard', 20, 'NORMAL', 'SPELL')");

            // act
            List<Card> cards = cardRepository.getCards();

            // cleanup
            sm.executeUpdate("DELETE FROM users WHERE id < 0");
            sm.close();
            conn.close();

            // assert
            assertNotNull(cards);
            assertEquals(2, cards.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Add a card")
    void testAddCard() {
        try {
            // arrange
            Card normalCard = Card.builder()
                    .name("NormalCard")
                    .damage((float) 25.5)
                    .elementType(ElementType.NORMAL)
                    .cardType(CardType.MONSTER)
                    .build();

            // act
            Card card = cardRepository.addCard(normalCard);

            // assert
            assertNotNull(card);
            assertNotNull(card.getName());

            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, name, damage, element_type, card_type FROM cards WHERE id = ?;");
            ps.setInt(1, card.getId());
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals("NormalCard", rs.getString(2));
            assertEquals(25.5, rs.getFloat(3));
            assertEquals(ElementType.NORMAL, ElementType.valueOf(rs.getString(4)));
            assertEquals(CardType.MONSTER, CardType.valueOf(rs.getString(5)));

            // cleanup
            Statement sm = conn.createStatement();
            sm.executeUpdate("DELETE FROM cards WHERE id = " + card.getId() + ";");
            ps.close();
            rs.close();
            sm.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Delete a card")
    void testDeleteCard() {
        try {
            // arrange
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            sm.executeUpdate("INSERT INTO cards(id, name, damage, element_type, card_type) VALUES(-1, 'WaterCard', 5.0, 'WATER', 'MONSTER');");

            // act
            boolean result = cardRepository.deleteCard(-1);
            ResultSet rs = sm.executeQuery("SELECT id FROM cards WHERE id = -1;");

            // assert
            assertTrue(result);
            assertFalse(rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
