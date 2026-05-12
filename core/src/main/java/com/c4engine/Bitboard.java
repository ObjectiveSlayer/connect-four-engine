package com.c4engine;

import java.util.ArrayList;
import java.util.List;

public class Bitboard {
    public long[] bitboards = new long[2];
    public int[] heights = {0, 7, 14, 21, 28, 35, 42};

    private static final int[] POSITIONAL_WEIGHTS = {
        3, 4, 5, 5, 4, 3, 0,        // Col 0
        4, 6, 8, 8, 6, 4, 0,        // Col 1
        5, 8, 11, 11, 8, 5, 0,      // Col 2
        7, 10, 13, 13, 10, 7, 0,    // Col 3
        5, 8, 11, 11, 8, 5, 0,      // Col 4
        4, 6, 8, 8, 6, 4, 0,        // Col 5
        3, 4, 5, 5, 4, 3, 0         // Col 6
    };

    public void makeMove(int col, int playerIndex) {
        long move = 1L << heights[col]++;
        bitboards[playerIndex] ^= move;
    }

    public void undoMove(int col, int playerIndex) {
        heights[col]--;
        long move = 1L << heights[col];
        bitboards[playerIndex] ^= move;
    }

    public boolean checkWin(int playerIndex) {
        long m = bitboards[playerIndex];
        long[] directions = {1, 7, 6, 8};
        for (long dir : directions) {
            long x = m & (m >> dir);
            if ((x & (x >> (2 * dir))) != 0) return true;
        }
        return false;
    }

    public List<Integer> getLegalMoves() {
        List<Integer> legal = new ArrayList<>();
        int[] moveOrder = {3, 4, 2, 5, 1, 6, 0};
        long topRowMask = 0b1000000_1000000_1000000_1000000_1000000_1000000_1000000L;
        for (int c : moveOrder) {
            if ((topRowMask & (1L << heights[c])) == 0) legal.add(c);
        }
        return legal;
    }

    public int evaluatePosition() {
        int score = 0;
        long p1 = bitboards[0], p2 = bitboards[1];
        while (p1 != 0) {
            int idx = Long.numberOfTrailingZeros(p1);
            score += POSITIONAL_WEIGHTS[idx];
            p1 &= (p1 - 1);
        }
        while (p2 != 0) {
            int idx = Long.numberOfTrailingZeros(p2);
            score -= POSITIONAL_WEIGHTS[idx];
            p2 &= (p2 - 1);
        }
        return score;
    }

    public void loadSnapshot(String snapshot) {
        bitboards[0] = 0L; bitboards[1] = 0L;
        heights = new int[]{0, 7, 14, 21, 28, 35, 42};
        String[] rows = snapshot.trim().split("/");
        for (int r = 5; r >= 0; r--) {
            for (int c = 0; c < 7; c++) {
                char val = rows[r].charAt(c);
                if (val == '1') makeMove(c, 0);
                else if (val == '2') makeMove(c, 1);
            }
        }
    }

    public void printBoard() {
        String GRAY = "\u001B[90m", RED = "\u001B[31m", YELLOW = "\u001B[33m", RESET = "\u001B[0m";
        for (int r = 5; r >= 0; r--) {
            System.out.print("  ");
            for (int c = 0; c < 7; c++) {
                long mask = 1L << (c * 7 + r);
                if ((bitboards[0] & mask) != 0) System.out.print(RED + "O " + RESET);
                else if ((bitboards[1] & mask) != 0) System.out.print(YELLOW + "O " + RESET);
                else System.out.print(GRAY + "· " + RESET);
            }
            System.out.println();
        }
        System.out.println(GRAY + "  1 2 3 4 5 6 7\n" + RESET);
    }
}