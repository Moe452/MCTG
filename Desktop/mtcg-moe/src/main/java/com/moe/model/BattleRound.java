package com.moe.model;

import lombok.Builder;
import lombok.Getter;

@Builder
public class BattleRound {
    @Getter
    int id;

    @Getter
    Card cardA;

    @Getter
    Card cardB;

    @Getter
    Card winnerCard;
}
