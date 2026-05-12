package com.c4engine;

public class Engine {
    public int getBestMove(Bitboard board, int depth) {
        int bestScore = Integer.MIN_VALUE, bestMove = -1;
        for (int move : board.getLegalMoves()) {
            board.makeMove(move, 1);
            if (board.checkWin(1)) { board.undoMove(move, 1); return move; }
            int score = minimax(board, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.undoMove(move, 1);
            if (score > bestScore) { bestScore = score; bestMove = move; }
        }
        return (bestMove != -1) ? bestMove : board.getLegalMoves().get(0);
    }

    private int minimax(Bitboard board, int depth, int alpha, int beta, boolean isMax) {
        if (board.checkWin(1)) return 1000000 + depth;
        if (board.checkWin(0)) return -1000000 - depth;
        if (depth == 0 || board.getLegalMoves().isEmpty()) return -board.evaluatePosition();

        if (isMax) {
            int maxEval = Integer.MIN_VALUE;
            for (int m : board.getLegalMoves()) {
                board.makeMove(m, 1);
                int eval = minimax(board, depth - 1, alpha, beta, false);
                board.undoMove(m, 1);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int m : board.getLegalMoves()) {
                board.makeMove(m, 0);
                int eval = minimax(board, depth - 1, alpha, beta, true);
                board.undoMove(m, 0);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }
}