package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.MoveManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;

import java.util.UUID;

/**
 * Created by ttoggweiler on 1/21/17.
 */
public class SequentialPlayer extends BasePlayer
{
    public SequentialPlayer()
    {
        this.setName("Seq");
    }

    public SequentialPlayer(String name)
    {
        setName(name);
    }

    @Override
    public Move getNextMove(UUID gameId)
    {
        return nextSequentialMove(this, getGame(gameId).getBoardManager());
    }

    @Override
    public void gameStarted(TicTacToeGame game)
    {

    }

    @Override
    public void gameEnded(UUID gameId, boolean winner)
    {

    }

    public static Move nextSequentialMove(BasePlayer player, BoardManager board)
    {
        for (int i = 0; i <= board.size() - 1; i++)
            for (int j = 0; j <= board.size() - 1; j++)
                if (!board.isOccupied(i, j))
                    return new Move(player.getId(), i, j);
        assert false;
        return null;
    }
}
