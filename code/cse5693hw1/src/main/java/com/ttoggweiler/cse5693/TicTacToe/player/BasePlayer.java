package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.Board;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * Object that represents a player, human or otherwise
 */
public abstract class BasePlayer
{
    private UUID id = UUID.randomUUID();
    private String name;

    public abstract Move getNextMove(Board board);
    public abstract void gameStart(TicTacToeGame game);
    public abstract void gameEnd(UUID gameId, boolean winner);

    public UUID getId()
    {
        return id;
    }

    public String getName()
    {
        if(name == null || name.isEmpty())return id.toString();
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static boolean areMatching(BasePlayer ... players)
    {
        if(players.length < 2) throw new IllegalArgumentException("Impossible to determine if players match with less than 2 players!");
        if(Arrays.stream(players).anyMatch(Objects :: isNull))throw new NullPointerException("One or more players to match are null!");
        return Arrays.stream(players).allMatch(p -> players[0].getId().equals(p.getId()));
    }
}
