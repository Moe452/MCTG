package com.moe.card;

import com.moe.model.Card;
import com.moe.model.enums.CardType;
import com.moe.model.enums.ElementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardTest {

    @Mock
    Card cardMock;

    @Test
    @DisplayName("Dragons are immune against Goblins")
    void testWinsAgainst__dragonGoblin() {
        // arrange
        Card dragon = Card.builder().name("Dragon").cardType(CardType.MONSTER).build();
        when(cardMock.getCardType()).thenReturn(CardType.MONSTER);
        when(cardMock.getName()).thenReturn("Goblin");

        // act
        boolean result = dragon.winsAgainst(cardMock);

        // assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Wizards can control Orks so they are not able to damage them")
    void testWinsAgainst__wizardOrk() {
        // arrange
        Card wizard = Card.builder().name("Wizard").cardType(CardType.MONSTER).build();
        when(cardMock.getCardType()).thenReturn(CardType.MONSTER);
        when(cardMock.getName()).thenReturn("Ork");

        // act
        boolean result = wizard.winsAgainst(cardMock);

        // assert
        assertTrue(result);
    }

    @Test
    @DisplayName("The armor of Knights is so heavy that WaterSpells make them drown them instantly")
    void testWinsAgainst__waterSpellKnight() {
        // arrange
        Card waterSpell = Card.builder().elementType(ElementType.WATER).cardType(CardType.SPELL).build();
        when(cardMock.getCardType()).thenReturn(CardType.MONSTER);
        when(cardMock.getName()).thenReturn("Knight");

        // act
        boolean result = waterSpell.winsAgainst(cardMock);

        // assert
        assertTrue(result);
    }

    @Test
    @DisplayName("The Kraken is immune against spells")
    void testWinsAgainst__krakenSpell() {
        // arrange
        Card kraken = Card.builder().name("Kraken").cardType(CardType.MONSTER).build();
        when(cardMock.getCardType()).thenReturn(CardType.SPELL);

        // act
        boolean result = kraken.winsAgainst(cardMock);

        // assert
        assertTrue(result);
    }

    @Test
    @DisplayName("The FireElves know Dragons since they were little and can evade their attacks")
    void testWinsAgainst__fireElveDragon() {
        // arrange
        Card fireElve = Card.builder().name("FireElve").cardType(CardType.MONSTER).build();
        when(cardMock.getCardType()).thenReturn(CardType.MONSTER);
        when(cardMock.getName()).thenReturn("Dragon");

        // act
        boolean result = fireElve.winsAgainst(cardMock);

        // assert
        assertTrue(result);
    }
}
