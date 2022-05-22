package com.moe.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public class Package {
    @Getter
    int id;

    @Getter
    transient List<Card> cards;

    @Getter
    @Builder.Default
    int price = 5;

    @Getter
    String name;
}
