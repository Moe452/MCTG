package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.model.Message;
import com.moe.persistence.MessageRepository;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageController extends HttpServlet {

    MessageRepository messageRepository;
    Gson g;
    Pattern p;

    public MessageController() {
        g = new Gson();
        p = Pattern.compile("/messages/(\\d+)/?");
        this.messageRepository = MessageRepository.getInstance();
    }

    @Override
    public HttpResponse handleIndex(HttpRequest request) {
        List<Message> messages = messageRepository.getMessages();

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(g.toJson(messages))
                .build();
    }

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));
            Message message = messageRepository.getMessage(id);

            if (message != null) {
                return HttpResponse.builder()
                        .headers(new HashMap<>() {{
                            put("Content-Type", "application/json");
                        }})
                        .body(g.toJson(message))
                        .build();
            }
        }
        return HttpResponse.notFound();
    }

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        Message message = messageRepository.addMessage(g.fromJson(request.getBody(), Message.class));

        if (message != null) {
            return HttpResponse.builder()
                    .headers(new HashMap<>() {{
                        put("Content-Type", "application/json");
                    }})
                    .body(g.toJson(message))
                    .build();
        }
        return HttpResponse.internalServerError();
    }

    @Override
    public HttpResponse handlePut(HttpRequest request) {
        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));

            Message message = messageRepository.updateMessage(id, g.fromJson(request.getBody(), Message.class));
            if (message != null) {
                return HttpResponse.builder()
                        .headers(new HashMap<>() {{
                            put("Content-Type", "application/json");
                        }})
                        .body(g.toJson(message))
                        .build();
            }
        }
        return HttpResponse.notFound();
    }

    @Override
    public HttpResponse handleDelete(HttpRequest request) {
        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));

            if (messageRepository.deleteMessage(id)) {
                return HttpResponse.ok();
            }
        }
        return HttpResponse.notFound();
    }
}
