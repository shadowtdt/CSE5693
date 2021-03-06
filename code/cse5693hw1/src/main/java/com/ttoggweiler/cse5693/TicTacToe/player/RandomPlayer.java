package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.MoveManager;
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
    private Random rand;

    public RandomPlayer()
    {
        this("Rand");
    }

    public RandomPlayer(String name)
    {
        this(name,null);
    }

    public RandomPlayer(Long randomSeed)
    {
        this("Rand",randomSeed);
    }

    public RandomPlayer(String name, Long randomSeed)
    {
        setName(name);
        setRandomSeed(randomSeed);
    }

    public void setRandomSeed(Long randomSeed)
    {
        if(randomSeed!=null)this.rand= new Random(randomSeed);
        else this.rand = new Random();
    }

    @Override
    public Move getNextMove(UUID gameId)
    {
        return randomMove(this, getGame(gameId).getBoardManager());
    }

    @Override
    public void gameStarted(TicTacToeGame game)
    {

    }

    @Override
    public void gameEnded(UUID gameId, boolean winner)
    {

    }

    public Move randomMove(BasePlayer player, BoardManager board)
    {
        IntStream ints = this.rand.ints(0, board.size());


        PrimitiveIterator.OfInt intItr = ints.iterator();
        while (true) {
            int x = intItr.next();
            int y = intItr.next();
            if(!board.isOccupied(x, y))
                return new Move(player.getId(), x, y);

        }

    }
}
