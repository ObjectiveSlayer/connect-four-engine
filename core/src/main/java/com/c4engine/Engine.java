package com.c4engine;

import java.util.List;

public class Engine {
    
    public int getBestMove(Board board, int depth) {
        int bestScore = Integer.MIN_VALUE;
        int bestCol = -1;
        
        int alpha = Integer.MIN_VALUE; 
        int beta = Integer.MAX_VALUE;  
        
        int enginePlayer = board.getCurrentPlayer(); 
        
        List<Integer> legalMoves = board.getLegalMoves();

        for (int col : legalMoves) {
            board.makeMove(col);
            int score = minimax(board, depth - 1, alpha, beta, false, enginePlayer); 
            board.undoMove(col);

            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }
            alpha = Math.max(alpha, bestScore);
        }
        return bestCol;
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizing, int enginePlayer) {
        int opponent = (enginePlayer == 1) ? 2 : 1;

        if (board.checkWin(enginePlayer)) return 10000 + depth; 
        if (board.checkWin(opponent)) return -10000 - depth; 
        
        List<Integer> legalMoves = board.getLegalMoves();
        if (legalMoves.isEmpty() || depth == 0) {
            return 0; 
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int col : legalMoves) {
                board.makeMove(col);
                int eval = minimax(board, depth - 1, alpha, beta, false, enginePlayer);
                board.undoMove(col);
                
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
            
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col : legalMoves) {
                board.makeMove(col);
                int eval = minimax(board, depth - 1, alpha, beta, true, enginePlayer);
                board.undoMove(col);
                
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }
}