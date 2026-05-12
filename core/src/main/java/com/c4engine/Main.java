package com.c4engine;

import java.util.Scanner;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Engine engine = new Engine();

        while (true) {
            System.out.println("1. Play vs AI");
            System.out.println("2. Analyze Snapshot");
            System.out.println("3. Launch Local Web Server");
            System.out.println("4. Exit");
            System.out.print("Select mode: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                playGame(scanner, engine);
            } else if (choice == 2) {
                analyzePosition(scanner, engine);
            } else if (choice == 3) {
                System.out.println("Booting internal server...");
                Server webServer = new Server();
                webServer.start();
            } else if (choice == 4) {
                System.out.println("Shutting down...");
                System.exit(0);
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void playGame(Scanner scanner, Engine engine) {
        Board board = new Board();
        System.out.println("\n--- Game Started ---");
        board.printBoard();

        while (true) {
            System.out.print("\nEnter column (1-7): ");
            int col = (scanner.nextInt() - 1);
            if (!board.getLegalMoves().contains(col)) {
                System.out.println("Invalid move!");
                continue;
            }
            board.makeMove(col);
            board.printBoard();
            if (board.checkWin(1)) { System.out.println("HUMAN WINS!"); break; }
            if (board.getLegalMoves().isEmpty()) { System.out.println("DRAW!"); break; }


            System.out.println("\nAI is thinking... (Depth 10)\n");
            int aiMove = engine.getBestMove(board, 10); 
            System.out.println("AI plays column: " + (aiMove + 1));
            board.makeMove(aiMove);
            board.printBoard();
            if (board.checkWin(2)) { System.out.println("AI WINS!"); break; }
        }
    }

    private static void analyzePosition(Scanner scanner, Engine engine) {
        Board board = new Board();
        System.out.print("\nEnter Snapshot String (e.g., 0000000/.../..12210): ");
        String snapshot = scanner.nextLine();

        try {
            board.loadSnapshot(snapshot);
            System.out.println("\nBoard loaded successfully:");
            board.printBoard();
            
            System.out.println("\nEngine is analyzing... (Depth 10)");
            

            long startTime = System.currentTimeMillis();
            int bestMove = engine.getBestMove(board, 10);
            long endTime = System.currentTimeMillis();

            double seconds = (endTime - startTime) / 1000.0;

            System.out.println("---------------------------------");
            System.out.println("Best Move: Column " + (bestMove + 1));
            System.out.println("Time taken: " + seconds + "s");
            System.out.println("---------------------------------");
            
        } catch (Exception e) {
            System.out.println("Error loading snapshot: " + e.getMessage());
        }
    }
}