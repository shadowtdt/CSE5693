package com.ttoggweiler.cse5693.TicTacToe.board;

import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MoveManager for tick tack toe
 * Supports any board size
 * Tracks move history of players
 * Many helper methods for getting board state information
 */
public class MoveManager
{
    private UUID gameID; // ID of the game this board belongs to
    //private BasePlayer[][] board;
    private BoardManager boardManager;
    private List<Move> moveHistory = new ArrayList<>();

    public MoveManager(UUID gameID,BoardManager boardManager)
    {
        if(gameID == null || boardManager == null)
            throw new NullPointerException("GameId or Board manager provided to move manager is null");
        this.boardManager = boardManager;
        this.gameID = gameID;
    }

    public UUID getGameID()
    {
        return gameID;
    }

    /**
     * Gets the current move number of the board
     * @return the current moveIndex
     */
    public int getCurrentMoveIndex()
    {
        return moveHistory.size();
    }

    /**
     * Runs the same check as {@link MoveManager#validateMove(Move)} but catches the exceptions
     * @param move the move to validate its player, coordinates
     * @return true if the move is valid, false otherwise
     */
    public boolean isMoveValid(Move move)
    {
        try {
            validateMove(move);
        } catch (Exception e) {
            move.setAccepted(false);
            move.setRejectionCause(e);
            return false;
        }
        return true;
    }

    /**
     * Returns clone of move history
     * @return game index sorted list of moves
     */
    public List<Move> getMoves()
    {
        return new ArrayList<>(moveHistory);
    }

    /**
     * Get the nth move made on the board
     * @param moveIndex the ordinal of the move to get
     * @return nth Move, else empty optional
     */
    public Optional<Move> findMoveForIndex(int moveIndex)
    {
        if (moveHistory.size() > moveIndex) return Optional.of(moveHistory.get(moveIndex));
        else return Optional.empty();
    }

    /**
     * Gets the most recent move on the board
     * @return most recent move, empty optional otherwise
     */
    public Optional<Move> findLastMove()
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        else return Optional.of(moveHistory.get(moveHistory.size() - 1));
    }

    /**
     * Gets the most recent move for the given player
     * @param player the player of the move
     * @return the most recent move, empty optional otherwirse
     */
    public Optional<Move> findLastMoveForPlayer(UUID player)
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        List<Move> playerMoves = moveHistory.stream()
                .filter(move -> move.getPlayer().equals(player))
                .collect(Collectors.toList());

        if (playerMoves.isEmpty()) return Optional.empty();
        else return Optional.of(playerMoves.get(playerMoves.size() - 1));
    }

    public Optional<Move> findMoveForCoordinates(int... coordinates)
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        return moveHistory.stream()
                .filter(move -> Arrays.equals(move.getMove(), coordinates))
                .findAny();
    }

    /**
     * Gets all the moves for the given player
     * @param player the player of the moves
     * @return MoveIndex sorted list of moves for the given player
     */
    public Optional<List<Move>> findMovesForPlayer(UUID player)
    {
        if (moveHistory.isEmpty()) return Optional.empty();
        List<Move> playerMoves = moveHistory.stream()
                .filter(move -> move.getPlayer().equals(player))
                .collect(Collectors.toList());

        if (playerMoves.isEmpty()) return Optional.empty();
        else {
            return Optional.of(playerMoves);
        }
    }

    /**
     * Places player on board
     * Validation will check player, coordinates and throw runtime exceptions should the move not be valid
     * The game index, moveTime and accepted fields will be set on the move
     * @param move Move to make
     */
    public void makeMove(Move move)
    {
        validateMove(move);
        boardManager.occupySpace(move.getPlayer(),move.getMove()[0],move.getMove()[1]);
        move.setAccepted(true);
        move.setMoveTime(System.currentTimeMillis());
        move.setGameMoveIndex(moveHistory.size());
        move.setBoard(boardManager.getCurrentBoard());
        moveHistory.add(move);
    }

    /**
     * Precondition check before attempting a move
     * checks player, coordinats, space conflicts
     * @param move
     */
    private void validateMove(Move move)
    {
        if (boardManager.getState().equals(BoardManager.BoardState.FULL))
            throw new IllegalMoveException("MoveManager is in a FULL state, no more moves may be played");
        if (boardManager.isOccupied(move.getMove()))
            throw new IllegalMoveException(Arrays.toString(move.getMove()) + " space is already occupied");
        if (move.getPlayer() == null) throw new NullPointerException("A player value must set when making a move");
        if (move.getMove().length > 2)
            throw new IllegalArgumentException("A moves coordinates must only contain a row and column when making a move");
        boardManager.isInBounds(move.getMove()[0], move.getMove()[1]);
    }

}
