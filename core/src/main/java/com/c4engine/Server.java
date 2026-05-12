package com.c4engine;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {
    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new UIHandler());
            server.createContext("/api/move", new ApiHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("\n  \u001B[32m> Server online at http://localhost:8080\u001B[0m");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class UIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String currentDir = System.getProperty("user.dir");
                java.nio.file.Path filePath = Paths.get(currentDir, "src", "web", "index.html");
                if (!Files.exists(filePath)) {
                    filePath = Paths.get(currentDir, "core", "src", "web", "index.html");
                }
                byte[] response = Files.readAllBytes(filePath);
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            } catch (IOException e) {
                String error = "404 Not Found";
                exchange.sendResponseHeaders(404, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }

    class ApiHandler implements HttpHandler {
        private Engine engine = new Engine();
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String snapshot = query.split("snapshot=")[1].split("&")[0];
            int depth = 10;
            if (query.contains("depth=")) {
                depth = Integer.parseInt(query.split("depth=")[1].split("&")[0]);
            }
            Bitboard board = new Bitboard();
            board.loadSnapshot(snapshot);
            int bestMove = engine.getBestMove(board, depth);
            String response = "{\"move\": " + bestMove + "}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}