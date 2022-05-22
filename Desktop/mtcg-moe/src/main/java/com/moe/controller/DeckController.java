package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.model.Card;
import com.moe.persistence.DeckRepository;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.moe.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class DeckController extends HttpServlet {

    DeckRepository deckRepository;
    Gson gson;

    public DeckController() {
        gson = new Gson();
        deckRepository = DeckRepository.getInstance();
    }

    @Override
    public HttpResponse handleIndex(HttpRequest request) {
        // Only authorized users can view their decks
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();
        List<Card> cards = deckRepository.getDeck(user);

        String returnBody;
        String returnContentType;

        if ("text/plain".equals(request.getHeaders().get("Accept"))) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Deck: ");
            for (Card card : cards) {
                stringBuilder
                        .append(card.getName())
                        .append("(")
                        .append(card.getElementType())
                        .append(", ")
                        .append(card.getCardType())
                        .append(", ")
                        .append(card.getDamage())
                        .append("), ");
            }

            stringBuilder.setLength(stringBuilder.length() - 2);

            returnBody = stringBuilder.toString();
            returnContentType = "text/plain";
        } else {
            returnBody = gson.toJson(cards);
            returnContentType = "application/json";
        }

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", returnContentType);
                }})
                .body(returnBody)
                .build();
    }

    @Override
    public HttpResponse handlePut(HttpRequest request) {
        // Only authorized users can update their decks
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();
        int[] ids = gson.fromJson(request.getBody(), int[].class);

        boolean result = deckRepository.addCardsWithIdsToDeck(ids, user);

        int statusCode = result ? 200 : 400;
        String reasonPhrase = result ? "OK" : "Bad Request";
        List<Card> cards = deckRepository.getDeck(user);

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .statusCode(statusCode)
                .reasonPhrase(reasonPhrase)
                .body(gson.toJson(cards))
                .build();
    }
}
