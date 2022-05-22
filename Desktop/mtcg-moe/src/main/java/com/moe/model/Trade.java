package com.moe.model;

import lombok.Builder;
import lombok.Getter;

@Builder
public class Trade {

    @Getter
    int id;

    @Getter
    Card cardA;

    @Getter
    Card cardB;

    @Getter
    int coins;

    @Getter
    boolean accepted;
}
