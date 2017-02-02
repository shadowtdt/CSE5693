package com.ttoggweiler.cse5693.TicTacToe.board;

import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;

import java.util.ArrayList;
import java.util.Arrays;
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
    private Float estimatedValue;
    private Float trainingValue;

    private int[] move; // the coordinates of the move
    private UUID[][] board; // board after move was made

    private int gameMoveIndex = -1; // the order of the move for a given game

    public Move(int row, int col)
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

    public Float getEstimatedValue()
    {
        return estimatedValue;
    }

    public void setEstimatedValue(Float estimatedValue)
    {
        this.estimatedValue = estimatedValue;
    }

    public Float getTrainingValue()
    {
        return trainingValue;
    }

    public void setTrainingValue(Float trainingValue)
    {
        this.trainingValue = trainingValue;
    }

    public String toString()
    {
        return move[0]+":"+move[1];
    }

    public static Move parse(String moveString)
    {
        String[] coordinates = moveString.split(":");
        if (coordinates.length != 2)
            throw new IllegalArgumentException("Incorrect number of coordinates found in: " + moveString);
        int rCoord = Integer.parseInt(coordinates[0].trim());
        int cCoord = Integer.parseInt(coordinates[1].trim());
        return new Move(rCoord,cCoord);
    }

    public static void main(String[] args)
    {
        String a = "a";
        String b = "b";
        ArrayList<String> list = new ArrayList<>();
        list.add(a);
        list.add(b);


        System.out.println(list.toString());
    }
}
