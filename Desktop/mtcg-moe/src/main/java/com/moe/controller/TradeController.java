package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.persistence.CardRepository;
import com.moe.model.Card;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.moe.model.Trade;
import com.moe.persistence.TradeRepository;
import com.moe.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TradeController extends HttpServlet {

    TradeRepository tradeRepository;
    CardRepository cardRepository;
    Gson gson;
    Pattern pattern;

    public TradeController() {
        gson = new Gson();
        pattern = Pattern.compile("/trades/(\\d+)/?");
        this.tradeRepository = TradeRepository.getInstance();
        this.cardRepository = CardRepository.getInstance();
    }

    @Override
    public HttpResponse handleIndex(HttpRequest request) {
        // Only authorized users can view their trades
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        List<Trade> trades = tradeRepository.getTrades();

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(gson.toJson(trades))
                .build();
    }

    public HttpResponse handlePost(HttpRequest request) {
        // Only authorized users can create trades
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();

        JsonObject jsonObject = JsonParser.parseString(request.getBody()).getAsJsonObject();
        if (jsonObject.has("cardA")) {
            int id = jsonObject.get("cardA").getAsInt();
            List<Card> userCards = cardRepository.getCardsForUser(user);
            List<Card> filteredCards = userCards.stream().filter(card -> card.getId() == id).collect(Collectors.toList());
            if (filteredCards.size() > 0) {
                Trade trade = (Trade) tradeRepository.addTrade(filteredCards.get(0));

                if (trade != null) {
                    return HttpResponse.builder()
                            .headers(new HashMap<>() {{
                                put("Content-Type", "application/json");
                            }})
                            .statusCode(201)
                            .body(gson.toJson(trade))
                            .build();
                }
            }
        }

        return HttpResponse.badRequest();
    }

    public HttpResponse handlePostOffer(HttpRequest request) {
        // Only authorized users can offer on trades
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();

        Matcher m = pattern.matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));
            Trade trade = tradeRepository.getTrade(id);

            if (trade != null) {

                JsonObject jsonObject = JsonParser.parseString(request.getBody()).getAsJsonObject();

                if (jsonObject.has("cardB") || jsonObject.has("coins")) {

                    int cardId = jsonObject.has("cardB") ? jsonObject.get("cardB").getAsInt() : 0;
                    int coins = jsonObject.has("coins") ? jsonObject.get("coins").getAsInt() : 0;

                    List<Card> userCards = cardRepository.getCardsForUser(user);
                    List<Card> filteredCards = userCards.stream().filter(card -> card.getId() == cardId).collect(Collectors.toList());

                    if (filteredCards.size() > 0) {
                        trade = tradeRepository.addOffer(trade, filteredCards.get(0), coins);

                        if (trade != null) {
                            return HttpResponse.builder()
                                    .headers(new HashMap<>() {{
                                        put("Content-Type", "application/json");
                                    }})
                                    .body(gson.toJson(trade))
                                    .build();
                        }
                    }
                }
            }
        }

        return HttpResponse.badRequest();
    }

    public HttpResponse handlePostAccept(HttpRequest request) {
        // Only authorized users can accept trades
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();

        Matcher m = Pattern.compile("/trades/(\\d+)/accept/?").matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));
            Trade trade = tradeRepository.getTrade(id);

            if (trade != null && trade.getCardA() != null && (trade.getCardB() != null || trade.getCoins() > 0)) {
                List<Card> userCards = cardRepository.getCardsForUser(user);

                Trade finalTrade = trade;
                List<Card> filteredCards = userCards.stream().filter(card -> card.getId() == finalTrade.getCardA().getId()).collect(Collectors.toList());

                if (filteredCards.size() > 0) {
                    trade = tradeRepository.acceptTrade(trade);

                    if (trade != null) {
                        return HttpResponse.builder()
                                .headers(new HashMap<>() {{
                                    put("Content-Type", "application/json");
                                }})
                                .body(gson.toJson(trade))
                                .build();
                    }
                }
            }
        }

        return HttpResponse.badRequest();
    }

    @Override
    public HttpResponse handleDelete(HttpRequest request) {
        // Only authorized users can delete trades
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();

        Matcher m = pattern.matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));

            Trade trade = tradeRepository.getTrade(id);

            if (trade != null) {
                List<Card> userCards = cardRepository.getCardsForUser(user);
                List<Card> filteredCards = userCards.stream().filter(card -> card.getId() == trade.getCardA().getId()).collect(Collectors.toList());
                if (filteredCards.size() > 0) {
                    if (tradeRepository.deleteTrade(trade.getId())) {
                        return HttpResponse.ok();
                    }
                }
            }
        }

        return HttpResponse.badRequest();
    }
}
