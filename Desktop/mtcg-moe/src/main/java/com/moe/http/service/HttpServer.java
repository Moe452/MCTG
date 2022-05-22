package com.moe.http.service;

import com.moe.http.base.RequestContext;
import com.moe.http.base.SocketWrapper;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;

public class HttpServer implements Runnable {

    @Getter
    ServerSocket listener;

    private int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void listen() {
        try {
            listener = new ServerSocket(port, 5);
            System.out.println("Listening on port " + listener.getLocalPort() + "...");
            // noinspection InfiniteLoopStatement
            while (true) {
                SocketWrapper socket = new SocketWrapper(listener.accept());
                System.out.println("New connection on port " + listener.getLocalPort() + "...");
                Thread thread = new Thread(() -> new RequestContext(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        System.out.println("Closing on port " + listener.getLocalPort() + "...");
        try {
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        listen();
    }
}
