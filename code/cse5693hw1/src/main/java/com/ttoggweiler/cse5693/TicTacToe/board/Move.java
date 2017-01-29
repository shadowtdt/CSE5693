package com.ttoggweiler.cse5693.TicTacToe.board;

import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;

import java.util.UUID;

/**
 * This class represents the data structure that will be passed into games/boards
 * that represent a players move for the Tick tack toe game
 */
public class Move
{
    private UUID id = UUID.randomUUID();
    private UUID gameId;
    private Long creationTime = System.currentTimeMillis(); // object creation
    private Long moveTime; // when the move was made in the game
    private boolean accepted = false; // if the move was accepted
    private Throwable rejectionCause;
    private UUID player; // the player making the move

    private int[] move; // the coordinates of the move
    private UUID[][] board; // board after move was made

    private int gameMoveIndex = -1; // the order of the move for a given game

    Move(int row, int col)
    {
        move = new int[]{row,col};
    }

    public Move(UUID player, int row, int col)
    {
        if(player == null) throw new NullPointerException("Player of move cannot be mull");
        this.player = player;
        move = new int[]{row,col};
    }
    public UUID getId()
    {
        return id;
    }

    public UUID getGameId()
    {
        return gameId;
    }

    void setGameId(UUID gameId)
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

    void setMoveTime(long moveTime)
    {
        this.moveTime = moveTime;
    }

    public boolean wasAccepted()
    {
        return accepted;
    }

    void setAccepted(boolean accepted)
    {
        this.accepted = accepted;
    }

    public UUID getPlayer()
    {
        return player;
    }

    public void setPlayer(UUID player)
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

    void setGameMoveIndex(int gameMoveIndex)
    {
        this.gameMoveIndex = gameMoveIndex;
    }

    public UUID[][] getBoard()
    {
        return board;
    }

    public void setBoard(UUID[][] board)
    {
        this.board = board;
    }

    void setRejectionCause(Throwable t)
    {
        rejectionCause = t;
    }

    public Throwable getRejectionCause()
    {
        return rejectionCause;
    }

    public String toString()
    {
        return "("+move[0]+","+move[1]+")";
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
