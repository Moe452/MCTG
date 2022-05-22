package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.model.Battle;
import com.moe.persistence.BattleRepository;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.moe.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.util.HashMap;

public class BattleController extends HttpServlet {

    BattleRepository battleRepository;
    Gson gson;

    public BattleController() {
        gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        this.battleRepository = BattleRepository.getInstance();
    }

    @SneakyThrows
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        // Only authorized users can battle
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();

        Battle battle = battleRepository.createOrAddUserToBattle(user);
        Battle battleResult = battleRepository.waitForBattleToFinish(battle);

        if (battleResult != null) {
            return HttpResponse.builder()
                    .headers(new HashMap<>() {{
                        put("Content-Type", "application/json");
                    }})
                    .body(gson.toJson(battleResult))
                    .build();
        }

        return HttpResponse.internalServerError();
    }
}
