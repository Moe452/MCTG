package com.moe.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public class Battle  {
    @Getter
    int id;

    @Getter
    boolean finished;

    @Getter
    User playerA;

    @Getter
    User playerB;

    @Getter
    User winner;

    @Getter
    List<BattleRound> battleRounds;
}
