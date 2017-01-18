package com.ttoggweiler.cse5693.board;

import com.ttoggweiler.cse5693.player.BasePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        INTERMEDIATE, // Some spaces available
        WINNING // A winning state
    }

    private UUID id = UUID.randomUUID();
    private long creationTime = System.currentTimeMillis();
    private List<Move> moveHistory = new ArrayList<>();

    /* Abstract */
    /**
     * Gets the board state
     * @return the state of board
     */
    public abstract BoardState getState();

    public abstract Optional<BasePlayer> findWinner();

    /* Non-Public abstract*/
    abstract boolean validateMove(Move move);

    abstract void submitMove(Move move);

    /* Base implementations */
    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public long getCreationTime()
    {
        return creationTime;
    }

    public void setCreationTime(long creationTime)
    {
        this.creationTime = creationTime;
    }

    public void makeMove(Move move) throws IllegalMoveException
    {
        if(!validateMove(move))throw new IllegalMoveException();
        else submitMove(move);
        moveHistory.add(move);
    }

    public boolean isOccupied(int ... coordinates)
    {
        return findPlayer(coordinates).isPresent();
    }

    public int getCurrentMoveIndex()
    {
        return moveHistory.size();
    }

    public List<Move> getMoves()
    {
        return moveHistory;
    }

    public Move getMove(int moveIndex)
    {
        if (moveHistory.size() > moveIndex) return moveHistory.get(moveIndex);
        else
            throw new IndexOutOfBoundsException("Could not find move #" + moveIndex + ". Board only has record of " + moveHistory.size() + "moves");
    }

    public Optional<Move> findLastMove()
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        else return Optional.of(moveHistory.get(moveHistory.size() - 1));
    }

    public Optional<Move> findLastMoveForPlayer(BasePlayer player)
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        List<Move> playerMoves = moveHistory.stream()
                .filter(move -> move.getPlayer().getId().compareTo(player.getId()) == 0)
                .collect(Collectors.toList());

        if(playerMoves.isEmpty())return Optional.empty();
        else return Optional.of(playerMoves.get(playerMoves.size() - 1));
    }

    public Optional<List<Move>> findMovesForPlayer(BasePlayer player)
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        List<Move> playerMoves = moveHistory.stream()
                .filter(move -> move.getPlayer().getId().compareTo(player.getId()) == 0)
                .collect(Collectors.toList());

        if(playerMoves.isEmpty())return Optional.empty();
        else return Optional.of(playerMoves);
    }

    public Optional<BasePlayer> findPlayer(int... coordinates)
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        return moveHistory.stream()
                .filter(move -> Arrays.equals(move.getMove(),coordinates))
                .findAny()
                .map(Move::getPlayer);
    }

    public Optional<Move> findMove(int... coordinates)
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        return moveHistory.stream()
                .filter(move -> Arrays.equals(move.getMove(),coordinates))
                .findAny();
    }

}
