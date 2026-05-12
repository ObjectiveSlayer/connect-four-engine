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

    private static int getDepth(Scanner scanner) {
        System.out.println("\n  \u001B[35m> Select Engine Depth (1-15)\u001B[0m");
        System.out.println("  \u001B[90mBitboard logic scales well. 10-12 is hard, 14+ is slow.\u001B[0m");
        System.out.print("  Depth: ");
        try {
            return Math.max(1, Math.min(Integer.parseInt(scanner.nextLine().trim()), 42));
        } catch (Exception e) {
            return 10;
        }
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
            try { choice = Integer.parseInt(input.trim()); } catch (Exception e) { continue; }
            if (choice == 1) playGame(scanner, engine);
            else if (choice == 2) analyzePosition(scanner, engine);
            else if (choice == 3) {
                clearScreen();
                System.out.println("\n  \u001B[35m> Booting internal server...\u001B[0m");
                new Server().start();
                break;
            } else if (choice == 4) System.exit(0);
        }
    }

    private static void playGame(Scanner scanner, Engine engine) {
        clearScreen();
        int depth = getDepth(scanner);
        Bitboard board = new Bitboard();
        int lastAiMove = -1;
        while (true) {
            clearScreen();
            if (lastAiMove != -1) System.out.println("\n  \u001B[33m> Engine dropped in column " + (lastAiMove + 1) + "\u001B[0m");
            System.out.println("  \u001B[1m> Your Turn (Red)\u001B[0m");
            board.printBoard();
            System.out.print("  Drop (1-7): ");
            String input = scanner.nextLine();
            int col = -1;
            try { col = Integer.parseInt(input.trim()) - 1; } catch (Exception e) { continue; }
            if (!board.getLegalMoves().contains(col)) continue;
            board.makeMove(col, 0);
            if (board.checkWin(0)) { clearScreen(); System.out.println("\n  \u001B[31m> HUMAN DEFEATS ENGINE\u001B[0m"); board.printBoard(); pause(scanner); break; }
            if (board.getLegalMoves().isEmpty()) { clearScreen(); System.out.println("\n  \u001B[90m> DRAW\u001B[0m"); board.printBoard(); pause(scanner); break; }
            clearScreen();
            System.out.println("\n  \u001B[33m> Engine is computing... (Depth " + depth + ")\u001B[0m");
            board.printBoard();
            lastAiMove = engine.getBestMove(board, depth);
            board.makeMove(lastAiMove, 1);
            if (board.checkWin(1)) { clearScreen(); System.out.println("\n  \u001B[33m> ENGINE DEFEATS HUMAN\u001B[0m"); board.printBoard(); pause(scanner); break; }
        }
    }

    private static void analyzePosition(Scanner scanner, Engine engine) {
        clearScreen();
        int depth = getDepth(scanner);
        Bitboard board = new Bitboard();
        clearScreen();
        System.out.println("\n  \u001B[35m> Analyze Snapshot\u001B[0m\n");
        System.out.print("  String: ");
        String snapshot = scanner.nextLine();
        try {
            board.loadSnapshot(snapshot);
            clearScreen();
            System.out.println("\n  \u001B[35m> Board Loaded\u001B[0m");
            board.printBoard();
            long start = System.currentTimeMillis();
            int move = engine.getBestMove(board, depth);
            double time = (System.currentTimeMillis() - start) / 1000.0;
            System.out.println("  \u001B[32mOptimal Drop: Column " + (move + 1) + "\u001B[0m");
            System.out.println("  \u001B[90mCalculated in " + time + "s\u001B[0m");
        } catch (Exception e) { System.out.println("\n  \u001B[31mError: " + e.getMessage() + "\u001B[0m"); }
        pause(scanner);
    }
}