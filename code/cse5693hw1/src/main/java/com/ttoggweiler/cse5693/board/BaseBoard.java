package com.ttoggweiler.cse5693.board;

import com.ttoggweiler.cse5693.player.BasePlayer;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Abstract class that all tick-tack-toe boards will implement
 * This will allow for boards of any size or even possibility 3d boards
 */
public abstract class BaseBoard
{
    public enum BoardState
    {
        EMPTY, // No spaces are occupied
        FULL, // No free spaces
        INTERMEDIATE // Some spaces available
    }

    private ArrayList<int[]> moveHistory = new ArrayList<>();

    /**
     * Gets the board state
     * @return the state of board
     */
    public abstract BoardState getState();

    public abstract Optional<BasePlayer> findOccupant(int... coordinates);

    public abstract Optional<BasePlayer> findWinner();

    /* Non-Public */
    abstract boolean validateMove(BasePlayer player, int... coordinates);

    abstract void occupy(BasePlayer player, int... coordinates);


    public void makeMove(BasePlayer player, int... coordinates) throws IllegalMoveException
    {
        if(!validateMove(player,coordinates))throw new IllegalMoveException();
        else occupy(player,coordinates);
    }

    public ArrayList<int[]> getMoves()
    {
        return (ArrayList<int[]>) moveHistory.clone();
    }

    public int[] getMove(int moveIndex)
    {
        if (moveHistory.size() > moveIndex) return moveHistory.get(moveIndex);
        else
            throw new IndexOutOfBoundsException("Could not find move #" + moveIndex + ". Board only has record of " + moveHistory.size() + "moves");
    }

    public Optional<int[]> findLastMove()
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        else return Optional.of(moveHistory.get(moveHistory.size() - 1));
    }

    public int getCurrentMoveIndex()
    {
        return moveHistory.size();
    }
}
