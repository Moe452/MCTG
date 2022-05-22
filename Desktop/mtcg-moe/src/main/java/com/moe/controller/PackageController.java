package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.model.Package;
import com.moe.model.Card;
import com.moe.persistence.CardRepository;
import com.moe.persistence.PackageRepository;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class PackageController extends HttpServlet {

    PackageRepository packageRepository;
    CardRepository cardRepository;
    Gson gson;

    public PackageController() {
        gson = new Gson();
        this.packageRepository = PackageRepository.getInstance();
        this.cardRepository = CardRepository.getInstance();
    }

    public HttpResponse handlePost(HttpRequest request) {

        // Only admins can create card packages.
        if (request.getAuthUser() == null || !"admin".equalsIgnoreCase(request.getAuthUser().getUsername())) {
            return HttpResponse.unauthorized();
        }

        Package cardPackage = packageRepository.addPackage(Package.builder().name("pack").price(5).build());

        if (cardPackage != null) {

            Type cardsType = new TypeToken<ArrayList<Card>>(){}.getType();

            ArrayList<Card> cardArrayList = gson.fromJson(request.getBody(), cardsType);

            for (Card card : cardArrayList) {
                cardRepository.addCardToPackage(card, cardPackage);
            }

            JsonObject returnJsonObject = (JsonObject) gson.toJsonTree(cardPackage);
            returnJsonObject.add("cards", gson.toJsonTree(cardArrayList));

            return HttpResponse.builder()
                    .headers(new HashMap<>() {{
                        put("Content-Type", "application/json");
                    }})
                    .statusCode(201)
                    .body(gson.toJson(returnJsonObject))
                    .build();
        }
        return HttpResponse.internalServerError();
    }
}
