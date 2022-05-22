package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.moe.model.User;
import com.moe.persistence.UserRepository;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserController extends HttpServlet {

    UserRepository userRepository;
    Gson g;
    Pattern p;

    public UserController() {
        g = new Gson();
        p = Pattern.compile("/users/([a-zA-Z]+)/?");
        this.userRepository = UserRepository.getInstance();
    }

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        if (request.getAuthUser() == null) {
            return HttpResponse.unauthorized();
        }

        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            String username = m.group(1);

            User user = userRepository.getUserByUsernameWithoutSensibleData(username);
            if (user == null) {
                return HttpResponse.notFound();
            }

            if (request.getAuthUser().getId() != user.getId()) {
                return HttpResponse.forbidden();
            }
            // Hide password and token in REST response.
            user = user.toBuilder().password(null).token(null).build();

            if (user != null) {
                return HttpResponse.builder()
                        .headers(new HashMap<>() {{
                            put("Content-Type", "application/json");
                        }})
                        .body(g.toJson(user))
                        .build();
            }
        }
        return HttpResponse.notFound();
    }

    @Override
    public HttpResponse handlePut(HttpRequest request) {
        if (request.getAuthUser() == null) {
            return HttpResponse.unauthorized();
        }

        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            String username = m.group(1);

            User user = userRepository.getUserByUsername(username);
            if (user == null) {
                return HttpResponse.notFound();
            }

            if (request.getAuthUser().getId() != user.getId()) {
                return HttpResponse.forbidden();
            }

            User newUser = g.fromJson(request.getBody(), User.class);
            newUser = newUser.toBuilder()
                    .coins(user.getCoins())
                    .username(user.getUsername())
                    .password(Hashing.sha256().hashString(newUser.getPassword(), StandardCharsets.UTF_8).toString())
                    .build();

            user = userRepository.updateUser(user.getId(), newUser);
            if (user != null) {
                user = userRepository.getUserWithoutSensibleData(user.getId());
                return HttpResponse.builder()
                        .headers(new HashMap<>() {{
                            put("Content-Type", "application/json");
                        }})
                        .body(g.toJson(user))
                        .build();
            }
        }
        return HttpResponse.internalServerError();
    }

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        User user = g.fromJson(request.getBody(), User.class);

        if (user != null && user.getUsername() != null && user.getPassword() != null) {
            user.setCoins(20);
            //Generate a pseudo random token
            //noinspection UnstableApiUsage
            user = userRepository.addUser(
                    user.toBuilder()
                            .token(user.getUsername() + "-" + "mtcgToken")
                            .password(Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString())
                            .build());

            if (user != null) {
                user = userRepository.getUserWithoutSensibleData(user.getId());
                return HttpResponse.builder()
                        .statusCode(201)
                        .reasonPhrase("Created")
                        .headers(new HashMap<>() {{
                            put("Content-Type", "application/json");
                        }})
                        .body(g.toJson(user))
                        .build();
            }
        }
        return HttpResponse.internalServerError();
    }

    public HttpResponse handleDelete(HttpRequest request) {
        if (request.getAuthUser() == null) {
            return HttpResponse.unauthorized();
        }

        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            String username = m.group(1);

            User user = userRepository.getUserByUsername(username);
            if (user == null) {
                return HttpResponse.notFound();
            }

            if (request.getAuthUser().getId() != user.getId()) {
                return HttpResponse.forbidden();
            }
            boolean result = userRepository.deleteUser(user.getId());
            if (result) {
                return HttpResponse.ok();
            }
            return HttpResponse.internalServerError();
        }
        return HttpResponse.notFound();
    }

    public HttpResponse handleLogin(HttpRequest request) {
        Properties data = g.fromJson(request.getBody(), Properties.class);
        String username = data.getProperty("username");
        String password = data.getProperty("password");
        if (username != null && password != null) {
            User user = userRepository.getUserByUsername(username);
            if (user.authorize(password)) {
                return HttpResponse.builder()
                        .statusCode(200)
                        .reasonPhrase("OK")
                        .headers(new HashMap<>() {{
                            put("Content-Type", "application/json");
                        }})
                        .body(g.toJson(user.getToken()))
                        .build();
            }
        }
        return HttpResponse.unauthorized();
    }
}
