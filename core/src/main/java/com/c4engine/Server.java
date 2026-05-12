package com.c4engine;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.InetSocketAddress;

public class Server {
    private Engine engine;

    public Server() {
        this.engine = new Engine();
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/", new UIHandler());

            server.createContext("/api/move", new ApiHandler());

            server.setExecutor(null);
            server.start();
            
            System.out.println("\n[+] Server successfully started!");
            System.out.println("[+] Open your browser and go to: http://localhost:8080\n");

        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }

    class UIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                byte[] response = Files.readAllBytes(Paths.get("core/src/web/index.html"));
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            } catch (IOException e) {
                String error = "Could not find web/index.html. Make sure the folder is created!";
                exchange.sendResponseHeaders(404, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }

    class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            
            if (query != null && query.startsWith("snapshot=")) {
                String snapshot = query.split("=")[1];
                
                try {
                    Board board = new Board();
                    board.loadSnapshot(snapshot);
                    
                    int bestMove = engine.getBestMove(board, 10);
                    
                    String jsonResponse = "{\"move\": " + bestMove + "}";
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, jsonResponse.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonResponse.getBytes());
                    os.close();
                    
                } catch (Exception e) {
                    String error = "{\"error\": \"Engine failed to analyze snapshot.\"}";
                    exchange.sendResponseHeaders(500, error.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(error.getBytes());
                    os.close();
                }
            }
        }
    }
}