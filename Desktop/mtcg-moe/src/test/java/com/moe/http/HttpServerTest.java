package com.moe.http;

import com.moe.http.service.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpServerTest {
    HttpServer httpServer1;
    HttpServer httpServer2;

    @BeforeEach
    void beforeEach() {
        httpServer1 = new HttpServer(0); // Any free port
        httpServer2 = new HttpServer(0); // Any free port
    }

    @Test
    @DisplayName("The RestService class should implement the Runnable interface.")
    void testRestService__runnable() {
        Thread t1 = new Thread(httpServer1);
        Thread t2 = new Thread(httpServer2);

        assertNull(httpServer1.getListener());
        assertNull(httpServer2.getListener());

        t1.start();
        t2.start();

        t1.interrupt();
        t2.interrupt();
    }
}
