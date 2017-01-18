package com.ttoggweiler.cse5693.player;

import com.ttoggweiler.cse5693.board.Move;

import java.util.UUID;

/**
 * Object that represents a player, human or otherwise
 */
public abstract class BasePlayer
{
    private UUID id = UUID.randomUUID();
    private String name;

    public abstract Move getNextMove();

    public UUID getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
