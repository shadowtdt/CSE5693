package com.ttoggweiler.cse5693.appraiser.board;

import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;

import java.util.UUID;

/**
 * For each corner of the board that is owned, +1
 */
public class CornerCtApr extends BaseAppraiser<Move>
{

    public static final int DEFAULT_OWNED_CORNER_VALUE = 1;
    public static final int DEFAULT_OPPONENT_CORNER_VALUE = 0;

    private int ownedCornerValue;
    private int opponentCornerValue;

    public CornerCtApr()
    {
        ownedCornerValue = DEFAULT_OWNED_CORNER_VALUE;
        opponentCornerValue = DEFAULT_OPPONENT_CORNER_VALUE;
    }

    public CornerCtApr(int ownedCoreneValue, int opponentCornerValue)
    {
        this.ownedCornerValue = ownedCoreneValue;
        this.opponentCornerValue = opponentCornerValue;
    }

    @Override
    public Float appraise(Move move)
    {
        float value = 0f;
        UUID myPlayer = move.getPlayer();
        UUID[][] board = move.getBoard();
        int size = board.length - 1;

        if(board[0][0] != null)
            value += (board[0][0].equals(myPlayer)? ownedCornerValue : opponentCornerValue);
        if(board[size][0] != null)
            value += (board[size][0].equals(myPlayer)? ownedCornerValue : opponentCornerValue);
        if(board[0][size] != null)
            value += (board[0][size].equals(myPlayer)? ownedCornerValue : opponentCornerValue);
        if(board[size][size] != null)
            value += (board[size][size].equals(myPlayer)? ownedCornerValue : opponentCornerValue);

        return getWeight() * value;
    }
}
