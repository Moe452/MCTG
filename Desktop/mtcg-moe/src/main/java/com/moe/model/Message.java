package com.moe.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class Message {
    @Getter
    @Setter
    int id;

    @Getter
    String message;
}
