package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.model.Card;
import com.moe.persistence.CardRepository;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.moe.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class CardController extends HttpServlet {

    CardRepository cardRepository;
    Gson g;

    public CardController() {
        g = new Gson();
        this.cardRepository = CardRepository.getInstance();
    }

    @Override
    public HttpResponse handleIndex(HttpRequest request) {
        // Only authorized users can view their cards
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();
        List<Card> cards = cardRepository.getCardsForUser(user);

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(g.toJson(cards))
                .build();
    }
}
