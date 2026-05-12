package com.c4engine;

import java.util.Scanner;

public class Main {
    
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {}
    }

    private static void pause(Scanner scanner) {
        System.out.print("\n  \u001B[90mPress any key to return...\u001B[0m");
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "pause > nul").inheritIO().start().waitFor();
            } else {
                scanner.nextLine(); 
            }
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Engine engine = new Engine();

        while (true) {
            clearScreen();
            System.out.println("\n  \u001B[1m\u001B[35mCONNECT FOUR ENGINE\u001B[0m\n");
            
            System.out.println("  1. Play vs Engine");
            System.out.println("  2. Analyze Snapshot");
            System.out.println("  3. Launch Local Web Server");
            System.out.println("  4. Exit\n");
            System.out.print("  Select mode: ");
            
            String input = scanner.nextLine();
            int choice = -1;
            try {
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                continue; 
            }

            if (choice == 1) {
                playGame(scanner, engine);
            } else if (choice == 2) {
                analyzePosition(scanner, engine);
            } else if (choice == 3) {
                clearScreen();
                System.out.println("\n  \u001B[35m> Booting internal server on port 8080...\u001B[0m");
                Server webServer = new Server();
                webServer.start();
                break; 
            } else if (choice == 4) {
                clearScreen();
                System.exit(0);
            }
        }
        scanner.close();
    }

    private static void playGame(Scanner scanner, Engine engine) {
        Board board = new Board();
        int lastAiMove = -1; 
        
        while (true) {
            clearScreen();
            System.out.println();
            
            if (lastAiMove != -1) {
                System.out.println("  \u001B[33m> Engine dropped in column " + (lastAiMove + 1) + "\u001B[0m");
            }
            System.out.println("  \u001B[1m> Your Turn (Red)\u001B[0m");
            
            board.printBoard();
            System.out.print("\n  Drop in column (1-7): ");
            
            String input = scanner.nextLine();
            int col = -1;
            try {
                col = Integer.parseInt(input.trim()) - 1;
            } catch (NumberFormatException e) {
                continue; 
            }

            if (!board.getLegalMoves().contains(col)) {
                continue; 
            }
            
            board.makeMove(col);
            
            if (board.checkWin(1)) { 
                clearScreen();
                System.out.println("\n  \u001B[31m> HUMAN DEFEATS ENGINE\u001B[0m");
                board.printBoard();
                pause(scanner);
                break; 
            }
            if (board.getLegalMoves().isEmpty()) { 
                clearScreen();
                System.out.println("\n  \u001B[90m> DRAW\u001B[0m");
                board.printBoard();
                pause(scanner);
                break; 
            }

            clearScreen();
            System.out.println("\n  \u001B[33m> Engine is computing... (Depth 10)\u001B[0m");
            board.printBoard();
            
            lastAiMove = engine.getBestMove(board, 10); 
            board.makeMove(lastAiMove);
            
            if (board.checkWin(2)) { 
                clearScreen();
                System.out.println("\n  \u001B[33m> ENGINE DEFEATS HUMAN\u001B[0m");
                board.printBoard();
                pause(scanner);
                break; 
            }
        }
    }

    private static void analyzePosition(Scanner scanner, Engine engine) {
        Board board = new Board();
        clearScreen();
        System.out.println("\n  \u001B[35m> Analyze Snapshot\u001B[0m\n");
        System.out.print("  Enter string: ");
        String snapshot = scanner.nextLine();

        try {
            board.loadSnapshot(snapshot);
            clearScreen();
            System.out.println("\n  \u001B[35m> Board Loaded\u001B[0m");
            board.printBoard();
            
            System.out.println("\n  \u001B[90mEngine computing futures...\u001B[0m");
            long startTime = System.currentTimeMillis();
            int bestMove = engine.getBestMove(board, 10);
            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;

            System.out.println("  \u001B[32mOptimal Drop: Column " + (bestMove + 1) + "\u001B[0m");
            System.out.println("  \u001B[90mCalculated in " + seconds + "s\u001B[0m");
            
        } catch (Exception e) {
            System.out.println("\n  \u001B[31mError: " + e.getMessage() + "\u001B[0m");
        }
        pause(scanner);
    }
}