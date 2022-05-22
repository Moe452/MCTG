package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.moe.model.Stats;
import com.moe.persistence.StatsRepository;
import com.moe.model.User;
import com.google.gson.Gson;

import java.util.HashMap;

public class StatsController extends HttpServlet {

    StatsRepository statsRepository;
    Gson gson;

    public StatsController() {
        gson = new Gson();
        this.statsRepository = StatsRepository.getInstance();
    }

    @Override
    public HttpResponse handleIndex(HttpRequest request) {
        // Only authorized users can view their stats
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();
        Stats stats = statsRepository.getStatsForUser(user);

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(gson.toJson(stats))
                .build();
    }
}
