package com.ttoggweiler.cse5693.appraiser.board;

import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;

import java.util.UUID;

/**
 * For each corner of the board that is owned, +1
 */
public class BoardCornerApr extends BaseAppraiser<Move>
{

    public static final int OCCUPIED_CORNER_VALUE = 5;

    @Override
    public float appraise(Move move)
    {
        float value = 0f;
        UUID myPlayer = move.getPlayer();
        UUID[][] board = move.getBoard();
        int size = board.length - 1;

        if(board[0][0] != null && board[0][0].equals(myPlayer))
            value += OCCUPIED_CORNER_VALUE;
        if(board[size][0] != null && board[size][0].equals(myPlayer))
            value += OCCUPIED_CORNER_VALUE;
        if(board[0][size] != null && board[0][size].equals(myPlayer))
            value += OCCUPIED_CORNER_VALUE;
        if(board[size][size] != null && board[size][size].equals(myPlayer))
            value += OCCUPIED_CORNER_VALUE;

        return getWeight() * value;
    }
}