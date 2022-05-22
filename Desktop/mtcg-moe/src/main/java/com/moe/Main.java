package com.moe;

import com.moe.http.service.HttpServer;

public class Main {

    public static void main(String[] args) {
        Thread restServiceT = new Thread(new HttpServer(10001));
        restServiceT.start();
    }

}
