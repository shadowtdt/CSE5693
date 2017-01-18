package com.ttoggweiler.cse5693.board;

import com.ttoggweiler.cse5693.player.BasePlayer;

import java.util.Comparator;
import java.util.UUID;

/**
 * This class represents the data structure that will be passed into games/boards
 * that represent a players move for the Tick tack toe game
 */
public class Move implements Comparator<Move>
{
    private UUID id = UUID.randomUUID();
    private UUID gameId;
    private Long creationTime = System.currentTimeMillis(); // object createion
    private Long moveTime; // when the move was made in the game
    private Long accepted; // if the move was accepted
    private BasePlayer player; // the player making the move
    private int[] move; // the coordinates of the move
    private int gameMoveIndex = -1; // the order of the move for a given game
    private int playerMoveIndex; // the order of the move for the given player

    public UUID getId()
    {
        return id;
    }

    public UUID getGameId()
    {
        return gameId;
    }

    public void setGameId(UUID gameId)
    {
        this.gameId = gameId;
    }

    public Long getCreationTime()
    {
        return creationTime;
    }

    public Long getMoveTime()
    {
        return moveTime;
    }

    public void setMoveTime(long moveTime)
    {
        this.moveTime = moveTime;
    }

    public Long wasAccepted()
    {
        return accepted;
    }

    public void setAccepted(long accepted)
    {
        this.accepted = accepted;
    }

    public BasePlayer getPlayer()
    {
        return player;
    }

    public void setPlayer(BasePlayer player)
    {
        this.player = player;
    }

    public int[] getMove()
    {
        return move;
    }

    public void setMove(int[] move)
    {
        this.move = move;
    }

    public int getGameMoveIndex()
    {
        return gameMoveIndex;
    }

    public void setGameMoveIndex(int gameMoveIndex)
    {
        this.gameMoveIndex = gameMoveIndex;
    }

    public int getPlayerMoveIndex()
    {
        return playerMoveIndex;
    }

    public void setPlayerMoveIndex(int playerMoveIndex)
    {
        this.playerMoveIndex = playerMoveIndex;
    }

//    @Override
//    public int compare(Move o1, Move o2)
//    {
//        if(o1.getGameMoveIndex() != -1 || o2.getGameMoveIndex() != -1)
//            return o1.getGameMoveIndex() - o2.getGameMoveIndex();
//        else if(o1.getPlayerMoveIndex() != -1 || o2.getPlayerMoveIndex() != -1)
//            return o1.getPlayerMoveIndex() - o2.getPlayerMoveIndex();
//        else if(o1.getMoveTime() != -1 || o2.getMoveTime() != -1)
//            return thenComparingLong(Move::getMoveTime);
//
//    }
}
