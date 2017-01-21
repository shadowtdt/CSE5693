package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.Board;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;

import java.util.UUID;

/**
 * Created by ttoggweiler on 1/21/17.
 */
public class SequentialPlayer extends BasePlayer
{
    @Override
    public Move getNextMove(Board board)
    {
        return nextSequentialMove(this, board);
    }

    @Override
    public void gameStart(TicTacToeGame game)
    {

    }

    @Override
    public void gameEnd(UUID gameId, boolean winner)
    {

    }

    public static Move nextSequentialMove(BasePlayer player, Board board)
    {
        for (int i = 0; i <= board.size() - 1; i++)
            for (int j = 0; j <= board.size() - 1; j++)
                if (!board.isOccupied(i, j))
                    return new Move(player, i, j);
        assert false;
        return null;
    }
}
