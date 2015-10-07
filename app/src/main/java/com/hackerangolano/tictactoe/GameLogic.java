package com.hackerangolano.tictactoe;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Asus on 07/09/2015.
 */
public class GameLogic {
    private final int BOARD_SIZE = 9;
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;
    public static final int DIFFICULTY_EXTREME = 3;
    public static final char PLAYER_1 = 'X';
    public static final char PLAYER_2 = 'O';
    public static final char EMPTY_STRING = ' ';

    // Create a character array to store all the position of the board
    private char[] mBoard;

    public GameLogic() {
        // Initialize the mBoard array
        mBoard = new char[BOARD_SIZE];
        clearBoard();
    }

    public void clearBoard() {
        // Cycle through every position of the array and put a space ' '
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = EMPTY_STRING;
        }
    }

    public int getBoardSize() {
        // Return the size of the board
        return BOARD_SIZE;
    }

    public boolean gameEnd() {
        // Check if player 1 win, player 2 win, or if no move is possible
        return win(mBoard, PLAYER_1) || win(mBoard, PLAYER_2) || possibleMoves(mBoard).length == 0;
    }

    public boolean win(char[] board, char turn) {
        // Horizontal Win
        for (int i = 0; i <= 6; i += 3) {
            if (board[i] == turn && board[i + 1] == turn && board[i + 2] == turn)
                return true;
        }

        // Vertical Win
        for (int i = 0; i <= 2; i++) {
            if (board[i] == turn && board[i + 3] == turn && board[i + 6] == turn)
                return true;
        }

        // Diagonal Win
        return (board[0] == turn && board[4] == turn && board[8] == turn
                || board[2] == turn && board[4] == turn && board[6] == turn);
    }

    public Integer[] possibleMoves(char[] board) {
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == EMPTY_STRING) {
                list.add(i);
            }
        }

        Integer[] array = new Integer[list.size()];
        list.toArray(array);
        return array;
    }

    public char[] getBoard() {
        return mBoard;
    }

    public void move(char[] board, char turn, Integer index) {
        board[index] = turn;
    }

    public int getComputerMove(int difficulty) {
        int move = -1, alpha = -100, beta = 100;

        // if it is a winning move
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != PLAYER_1 && mBoard[i] != PLAYER_2) {
                move(mBoard, PLAYER_2, i);
                if (win(mBoard, PLAYER_2))
                    return i;
                else
                    mBoard[i] = EMPTY_STRING;
            }
        }

        // if it is a blocking move
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != PLAYER_1 && mBoard[i] != PLAYER_2) {
                move(mBoard, PLAYER_1, i);
                if (win(mBoard, PLAYER_1))
                    return i;
                else
                    mBoard[i] = EMPTY_STRING;
            }
        }

        // Calculate the best beta value
        for (Integer index : possibleMoves(mBoard)) {
            char[] tempBoard = mBoard.clone();
            move(tempBoard, PLAYER_2, index);
            int vAlphaBeta = alphaBeta(tempBoard, difficulty * 2, alpha, beta, PLAYER_1);
            if (vAlphaBeta < beta) {
                beta = vAlphaBeta;
                move = index;
            }
        }

        // Create a list of possible moves and pick a ramdom
        LinkedList<Integer> list = new LinkedList<>();
        for (Integer index : possibleMoves(mBoard))
        {
            char[] tempBoard = mBoard.clone();
            move(tempBoard, PLAYER_2, index);
            int vAlphaBeta = alphaBeta(tempBoard, difficulty * 2, alpha, 100, PLAYER_1);
            if (vAlphaBeta == beta)
                list.add(index);
        }
        Random mRand = new Random();
        move = list.get(mRand.nextInt(list.size()));

        return move;
    }

    public int alphaBeta(char[] board, int depth, int alpha, int beta, char turn) {
        // Base case to stop the recursive call
        // If we are at the furthest level of detail or we don't have any moves left
        if (depth == 0 || possibleMoves(board).length == 0) {
            // If X win
            if (win(board, PLAYER_1)) {
                return 100;
            }
            // If O win
            else if (win(board, PLAYER_2)) {
                return -100;
            }
            // It's a tie
            else {
                return 0;
            }
        }

        // Check if O wins with this move
        if (win(board, PLAYER_2)) {
            return -100;
        }
        // Check if X wins with this move
        if (win(board, PLAYER_1)) {
            return 100;
        }

        // If it's X's turn
        if (turn == PLAYER_1) {
            for (Integer index : possibleMoves(board)) {
                int value;
                char[] tempBoard = board.clone();
                move(tempBoard, turn, index);
                value = alphaBeta(tempBoard, depth - 1, alpha, beta, PLAYER_2);
                if (value > alpha)
                    alpha = value;
                if (alpha > beta)
                    break;
            }
            return alpha - 1;
        }
        // If it's O's turn
        if (turn == PLAYER_2) {
            for (Integer index : possibleMoves(board)) {
                int value;
                char[] tempBoard = board.clone();
                move(tempBoard, turn, index);
                value = alphaBeta(tempBoard, depth - 1, alpha, beta, PLAYER_1);
                if (beta > value)
                    beta = value;
                if (alpha > beta)
                    break;
            }
            return beta + 1;
        }

        return -1;
    }
}
