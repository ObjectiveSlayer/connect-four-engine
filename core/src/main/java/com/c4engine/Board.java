package com.c4engine;

import java.util.ArrayList;
import java.util.List;

public class Board  {
    private int[][] grid;
    private int currentPlayer;

    private final int ROWS = 6;
    private final int COLS = 7;

    public Board() {
        grid = new int[ROWS][COLS];
        currentPlayer = 1;
    }

    public void printBoard() {
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";
        String YELLOW = "\u001B[33m";
        String GRAY = "\u001B[90m";

        System.out.println();
        for (int r = 0; r < 6; r++) {
            System.out.print("  ");
            for (int c = 0; c < 7; c++) {
                if (grid[r][c] == 1) {
                    System.out.print(RED + "O " + RESET);
                } else if (grid[r][c] == 2) {
                    System.out.print(YELLOW + "O " + RESET);
                } else {
                    System.out.print(GRAY + "· " + RESET);
                }
            }
            System.out.println();
        }
        System.out.println(GRAY + "  1 2 3 4 5 6 7\n" + RESET);
    }

    public void loadSnapshot(String gridString) {
        gridString = gridString.trim();
        String[] textRows = gridString.split("/");

        if(textRows.length != ROWS)
            throw new IllegalArgumentException("Invalid snapshot: Must have exactly 6 rows.");

        int p1Count = 0;
        int p2Count = 0;

        for(int r = 0; r < ROWS; r++) {
            if(textRows[r].length() != COLS)
                throw new IllegalArgumentException("Illegal snapshot: Row " + (r + 1) + " does not have 7 columns.");

            for(int c = 0; c < COLS; c++) {
                char pieceChar = textRows[r].charAt(c);

                int piece = Character.getNumericValue(pieceChar);

                grid[r][c] = piece;
                if(piece == 1)  p1Count++;
                if(piece == 2)  p2Count++;
            }
        }

        if(p1Count == p2Count)          currentPlayer = 1;
        else if(p1Count == p2Count + 1) currentPlayer = 2;
        else    throw new IllegalArgumentException("Illegal board state! Player 1 has " + p1Count + " pieces and Player 2 has " + p2Count + " pieces.");
    }

    public List<Integer> getLegalMoves() {
        List<Integer> legalMoves = new ArrayList<>();

        for(int c = 0; c < COLS; c++)
            if(grid[0][c] == 0)
                legalMoves.add(c);

        return legalMoves;
    }

    public void makeMove(int col) {
        for(int r = ROWS - 1; r >= 0; r--) {
            if(grid[r][col] == 0) {
                grid[r][col] = currentPlayer;

                currentPlayer = (currentPlayer == 1) ? 2 : 1;

                return;
            }
        }
    }

    public void undoMove(int col) {
        for (int r = 0; r < ROWS; r++) {
            if (grid[r][col] != 0) {
                grid[r][col] = 0;
                
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                
                return;
            }
        }
    }

    public boolean checkWin(int player) {
        // x-win
        for (int c = 0; c < COLS - 3; c++) {
            for (int r = 0; r < ROWS; r++) {
                if (grid[r][c] == player && grid[r][c+1] == player && 
                    grid[r][c+2] == player && grid[r][c+3] == player)
                    return true;
            }
        }
        // y-win
        for (int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS - 3; r++) {
                if (grid[r][c] == player && grid[r+1][c] == player && 
                    grid[r+2][c] == player && grid[r+3][c] == player)
                    return true;
            }
        }
        // /-win
        for (int c = 0; c < COLS - 3; c++) {
            for (int r = 3; r < ROWS; r++) {
                if (grid[r][c] == player && grid[r-1][c+1] == player && 
                    grid[r-2][c+2] == player && grid[r-3][c+3] == player)
                    return true;
            }
        }
        // \-win
        for (int c = 0; c < COLS - 3; c++) {
            for (int r = 0; r < ROWS - 3; r++) {
                if (grid[r][c] == player && grid[r+1][c+1] == player && 
                    grid[r+2][c+2] == player && grid[r+3][c+3] == player)
                    return true;
            }
        }
        return false;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public int getPiece(int r, int c) {
        return grid[r][c];
    }
}
