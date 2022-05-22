package com.moe.http.base;

import com.moe.controller.*;
import lombok.Getter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestContext {
    @Getter
    SocketWrapper socket;

    @Getter
    HttpRequest request;

    @Getter
    HttpResponse response;

    @Getter
    Map<String, Method> routes;

    public RequestContext(SocketWrapper socket) {
        this.socket = socket;
        request = new HttpRequest();
        routes = new HashMap<>() {{
            try {
                put("^GET /messages/\\d+/?$", MessageController.class.getDeclaredMethod("handleGet", HttpRequest.class));
                put("^GET /messages/?$", MessageController.class.getDeclaredMethod("handleIndex", HttpRequest.class));
                put("^POST /messages/?$", MessageController.class.getDeclaredMethod("handlePost", HttpRequest.class));
                put("^PUT /messages/\\d+/?$", MessageController.class.getDeclaredMethod("handlePut", HttpRequest.class));
                put("^DELETE /messages/\\d+/?$", MessageController.class.getDeclaredMethod("handleDelete", HttpRequest.class));

                put("^POST /users/?$", UserController.class.getDeclaredMethod("handlePost", HttpRequest.class));
                put("^GET /users/[a-zA-Z]+/?$", UserController.class.getDeclaredMethod("handleGet", HttpRequest.class));
                put("^PUT /users/[a-zA-Z]+/?$", UserController.class.getDeclaredMethod("handlePut", HttpRequest.class));
                put("^DELETE /users/\\d+/?$", UserController.class.getDeclaredMethod("handleDelete", HttpRequest.class));
                put("^POST /sessions/?$", UserController.class.getDeclaredMethod("handleLogin", HttpRequest.class));

                put("^POST /packages/?$", PackageController.class.getDeclaredMethod("handlePost", HttpRequest.class));

                put("^POST /transactions/packages/?$", TransactionController.class.getDeclaredMethod("handleAcquirePackage", HttpRequest.class));

                put("^GET /cards/?$", CardController.class.getDeclaredMethod("handleIndex", HttpRequest.class));

                put("^GET /deck/?$", DeckController.class.getDeclaredMethod("handleIndex", HttpRequest.class));
                put("^PUT /deck/?$", DeckController.class.getDeclaredMethod("handlePut", HttpRequest.class));

                put("^GET /stats/?$", StatsController.class.getDeclaredMethod("handleIndex", HttpRequest.class));

                put("^GET /score/?$", ScoreboardController.class.getDeclaredMethod("handleIndex", HttpRequest.class));

                put("^POST /battles/?$", BattleController.class.getDeclaredMethod("handlePost", HttpRequest.class));

                put("^GET /trades/?$", TradeController.class.getDeclaredMethod("handleIndex", HttpRequest.class));
                put("^POST /trades/\\d+/accept/?$", TradeController.class.getDeclaredMethod("handlePostAccept", HttpRequest.class));
                put("^POST /trades/\\d+/?$", TradeController.class.getDeclaredMethod("handlePostOffer", HttpRequest.class));
                put("^POST /trades/?$", TradeController.class.getDeclaredMethod("handlePost", HttpRequest.class));
                put("^DELETE /trades/\\d+/?$", TradeController.class.getDeclaredMethod("handleDelete", HttpRequest.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }};

        handleSocket();
    }
    
    public void handleSocket() {
        try {
            // Read the InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            request.read(reader);
            request.authorizeRequest();

            // Resolve the method for the route
            Method method = resolveRoute(request);
            if (method != null) {
                // Try to invoke the resolved method
                try {
                    response = (HttpResponse) method.invoke(method.getDeclaringClass().getConstructor().newInstance(), request);
                } catch (InstantiationException | NoSuchMethodException e) {
                    // Error 500 - Internal Server Error
                    response = HttpResponse.internalServerError();
                    e.printStackTrace();
                }
            } else {
                // Error 404 - Not Found
                response = HttpResponse.notFound();
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            response.write(writer);
        } catch (IOException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    public Method resolveRoute(HttpRequest request) {
        if (request.getMethod() == null || request.getPath() == null) {
            return null;
        }
        String requestRoute = request.getMethod().toUpperCase() + " " + request.getPath();
        for (Map.Entry<String, Method> entry : this.routes.entrySet()) {
            if (Pattern.matches(entry.getKey(), requestRoute)) {
                return entry.getValue();
            }
        }

        return null;
    }
}
