package com.moe.model;

import lombok.Builder;
import lombok.Getter;

@Builder
public class Stats {
    @Getter
    int totalBattles;

    @Getter
    int wonBattles;

    @Getter
    int lostBattles;

    @Getter
    int elo;
}
