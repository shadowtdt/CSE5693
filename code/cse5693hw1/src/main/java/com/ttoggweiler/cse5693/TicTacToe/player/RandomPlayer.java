package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.Board;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;

import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Created by ttoggweiler on 1/21/17.
 */
public class RandomPlayer extends BasePlayer
{
    @Override
    public Move getNextMove(Board board)
    {
        return randomMove(this, board);
    }

    @Override
    public void gameStart(TicTacToeGame game)
    {

    }

    @Override
    public void gameEnd(UUID gameId, boolean winner)
    {

    }
    public static Move randomMove(BasePlayer player, Board board)
    {
        IntStream ints = new Random().ints(0, board.size());


        PrimitiveIterator.OfInt intItr = ints.iterator();
        while (true) {
            int x = intItr.next();
            int y = intItr.next();
            if(!board.isOccupied(x, y))
                return new Move(player, x, y);

        }

    }
}