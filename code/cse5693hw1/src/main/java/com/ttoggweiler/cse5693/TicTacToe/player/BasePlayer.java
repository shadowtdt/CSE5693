package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Object that represents a player, human or otherwise
 */
public abstract class BasePlayer
{
    private UUID id = UUID.randomUUID();
    private String name = id.toString();
    HashMap<UUID, TicTacToeGame> games = new HashMap<>();

    public abstract Move getNextMove(UUID gameId);
    public abstract void gameStarted(TicTacToeGame game);
    public abstract void gameEnded(UUID gameId, boolean winner);


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
        if(name != null && !name.trim().isEmpty())
            this.name = name;
    }

    public void gameStart(TicTacToeGame game)
    {
        games.put(game.getId(),game);
        gameStarted(game);
    }

    public TicTacToeGame getGame(UUID gameID)
    {
        if(gameID == null)throw new NullPointerException("Unable to get game with a UUID of null");
        return games.get(gameID);
    }
}
