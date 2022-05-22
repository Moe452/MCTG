package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.moe.persistence.StatsRepository;
import com.google.gson.Gson;

import java.util.HashMap;

public class ScoreboardController extends HttpServlet {

    StatsRepository statsRepository;
    Gson gson;

    public ScoreboardController() {
        gson = new Gson();
        this.statsRepository = StatsRepository.getInstance();
    }

    @Override
    public HttpResponse handleIndex(HttpRequest request) {
        // Only authorized users can view the scoreboard
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(gson.toJson(statsRepository.getScoreboard()))
                .build();
    }
}
