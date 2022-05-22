package com.moe.model;

import com.moe.model.enums.CardType;
import com.moe.model.enums.ElementType;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Getter
    int id;

    @Getter
    String name;

    @Getter
    float damage;

    @Getter
    ElementType elementType;

    @Getter
    CardType cardType;

    @Getter
    boolean locked;

    public boolean winsAgainst(Card card) {

        // wrap Card vs Card
        if (CardType.MONSTER.equals(this.getCardType()) && CardType.MONSTER.equals(card.getCardType())) {

            // Dragons defeat Goblins
            if ("Dragon".equalsIgnoreCase(this.getName()) && "Goblin".equalsIgnoreCase(card.getName())) {
                return true;
            }

            // Wizards defeat Orks
            if ("Wizard".equalsIgnoreCase(this.getName()) && "Ork".equalsIgnoreCase(card.getName())) {
                return true;
            }

            // FireElves defeat Dragons
            if ("FireElve".equalsIgnoreCase(this.getName()) && "Dragon".equalsIgnoreCase(card.getName())) {
                return true;
            }
        }

        // wrap Card vs Card
        if (CardType.SPELL.equals(this.getCardType()) && CardType.MONSTER.equals(card.getCardType())) {

            // WaterSpells defeat Knight
            if (ElementType.WATER.equals(this.getElementType()) && "Knight".equalsIgnoreCase(card.getName())) {
                return true;
            }
        }

        // wrap Card vs Card
        if (CardType.MONSTER.equals(this.getCardType()) && CardType.SPELL.equals(card.getCardType())) {

            // Kraken defeat all Spells
            return "Kraken".equalsIgnoreCase(this.getName());

        }

        return false;
    }

    public float calculateDamage(Card card) {
        // Effectiveness only relevant for spell cards
        if (CardType.SPELL.equals(this.getCardType())) {
            // Effective (double damage)
            if ((ElementType.WATER.equals(this.getElementType()) && ElementType.FIRE.equals(card.getElementType())) ||
                    (ElementType.FIRE.equals(this.getElementType()) && ElementType.NORMAL.equals(card.getElementType())) ||
                    (ElementType.NORMAL.equals(this.getElementType()) && ElementType.WATER.equals(card.getElementType()))) {
                System.out.println(2 * this.getDamage());
                return 2 * this.getDamage();
            }

            // Not Effective
            if ((ElementType.FIRE.equals(this.getElementType()) && ElementType.WATER.equals(card.getElementType())) ||
                    (ElementType.NORMAL.equals(this.getElementType()) && ElementType.FIRE.equals(card.getElementType())) ||
                    (ElementType.WATER.equals(this.getElementType()) && ElementType.NORMAL.equals(card.getElementType()))) {
                System.out.println(this.getDamage() / 2);
                return this.getDamage() / 2;
            }
        }

        // No Effect
        System.out.println(this.getDamage());
        return this.getDamage();
    }

    public static Card fromPrimitives(int id, String name, float damage, String cardTypeString, String elementTypeString, boolean locked) {
        CardType cardType;
        ElementType elementType;
        Card card;

        try {
            cardType = CardType.valueOf(cardTypeString);
        } catch (IllegalArgumentException e) {
            cardType = CardType.MONSTER;
        }

        try {
            elementType = ElementType.valueOf(elementTypeString);
        } catch (IllegalArgumentException e) {
            elementType = ElementType.NORMAL;
        }


        card = Card.builder()
                .id(id)
                .name(name)
                .damage(damage)
                .cardType(cardType)
                .elementType(elementType)
                .build();


        return card;
    }
}
