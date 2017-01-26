package com.ttoggweiler.cse5693.TicTacToe.board;

import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Board for tick tack toe
 * Supports any board size
 * Tracks move history of players
 * Many helper methods for getting board state information
 */
public class Board
{
    // Example board for reference
    // 0,0 | 0,1 | 0,2
    // ---------------
    // 1,0 | 1,1 | 1,2
    // ---------------
    // 2,0 | 2,1 | 2,2

    public enum BoardState
    {
        EMPTY, // No spaces are occupied
        FULL, // No free spaces
        INTERMEDIATE, // Some spaces available
    }

    private UUID gameID; // ID of the game this board belongs to
    private BasePlayer[][] board;
    private List<Move> moveHistory = new ArrayList<>();

    public Board(UUID gameID, int size)
    {
        board = createBoard(size);
        this.gameID = gameID;
    }

    public UUID getGameID()
    {
        return gameID;
    }

    /**
     * The size of the board
     * All boards are squares, so the height and length will always be the same
     * @return board dimension
     */
    public int size()
    {
        return board.length;
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
     * Gets the current state of the board, {@link Board.BoardState}
     * @return the current state of the board
     */
    public BoardState getState()
    {
        if (moveHistory.size() == 0)
            return BoardState.EMPTY;
        else if (moveHistory.size() == (board.length * board.length)) // switch must be constant
            return BoardState.FULL;
        else
            return BoardState.INTERMEDIATE;
    }

    /**
     * Returns a clone of the current board
     * @return Player[][] array of the current board state
     */
    public BasePlayer[][] getCurrentBoard()
    {
        return board.clone();
    }

    /**
     * Determines of the space at the coordinates is currently occupied
     * @param coordinates row and column of space to check
     * @return true is the space is occupied, false otherwise
     */
    public boolean isOccupied(int... coordinates)
    {
        return findPlayer(coordinates).isPresent();
    }

    /**
     * Runs the same check as {@link Board#validateMove(Move)} but catches the exceptions
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
     * Places player on board
     * Validation will check player, coordinates and throw runtime exceptions should the move not be valid
     * The game index, moveTime and accepted fields will be set on the move
     * @param move Move to make
     */
    public void makeMove(Move move)
    {
        validateMove(move);
        board[move.getMove()[0]][move.getMove()[1]] = move.getPlayer();
        move.setAccepted(true);
        move.setMoveTime(System.currentTimeMillis());
        move.setGameMoveIndex(moveHistory.size());
        moveHistory.add(move);
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
    public Optional<Move> findLastMoveForPlayer(BasePlayer player)
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
    public Optional<List<Move>> findMovesForPlayer(BasePlayer player)
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
     * Finds the player currently occupying the coordinates, empty optional otherwise
     * @param coordinates row and column where to look for player
     * @return if occupied, player occupant, empty optional otherwise
     */
    public Optional<BasePlayer> findPlayer(int... coordinates)
    {
        if (coordinates.length != 2)
            throw new IllegalArgumentException("Invalid number of dimensions for coordinates " + Arrays.toString(coordinates));
        isInBounds(coordinates[0], coordinates[1]);
        if (moveHistory.isEmpty()) return Optional.empty();
        return moveHistory.stream()
                .filter(move -> Arrays.equals(move.getMove(), coordinates))
                .findAny()
                .map(Move::getPlayer);
    }

    /**
     * Determines if the line, defined by the x0,y0, x1,y1 pairs are occupied by the same player
     * @param r0 Starting row
     * @param c0 Starting column
     * @param r1 Ending row
     * @param c1 Ending column
     * @return MoveIndex sorted list of moves that make up a matching sequence, empty optional otherwise
     *
     */
    public Optional<List<Move>> findMatchingSequence(int r0, int c0, int r1, int c1)
    {
        //Check if coordinates are in the bounds of the board
        isInBounds(r0, c0);
        isInBounds(r1, c1);

        int rowDelta = Math.abs(r0 - r1);
        int colDelta = Math.abs(c0 - c1);
        boolean zeroDeltaExists = (rowDelta == 0 || colDelta == 0);
        // if both row and column have deltas, than the slope must be 1 for a straight line
        if (!zeroDeltaExists && ((rowDelta + 0.0f) / colDelta) != 1.0)
            throw new IllegalArgumentException("Sequence is not a valid line");

        List<Move> moveSequence = new ArrayList<>();
        BasePlayer playerToMatch = null;
        int r = r0;
        int c = c0;
        //because slope must be one, the deltas wth be the same, rowDelta vs colDelta
        float sequenceLength = (zeroDeltaExists) ? (rowDelta + colDelta) : rowDelta;
        for (int i = 0; i <= sequenceLength; i++) {
            Optional<Move> oMove = findMoveForCoordinates(r, c);
            if(rowDelta != 0)r = (r0 < r1) ? r + 1 : r - 1;// Move the index based on the slope
            if(colDelta != 0)c = (c0 < c1) ? c + 1 : c - 1;

            if (!oMove.isPresent())
                return Optional.empty(); // Quick fail if space is not occupied
            else if (playerToMatch == null)
                playerToMatch = oMove.get().getPlayer(); // init player to match sequence
            else if (!BasePlayer.areMatching(playerToMatch, oMove.get().getPlayer()))
                return Optional.empty();// Quick fail on miss matched players
            else moveSequence.add(oMove.get()); // occupied && matching, add to sequence
        }
        moveSequence.sort(Comparator.comparingInt((Move::getGameMoveIndex))); // sort based on the move index
        return Optional.of(moveSequence);
    }

    public String getPrettyBoardString()
    {
        BasePlayer x = findMoveForIndex(0)
                .map(Move :: getPlayer).orElse(null);

        String boardStr = "\n";
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                boardStr += (j > 0) ? "| " : "";
                if (findPlayer(i, j).isPresent())
                    boardStr += (findPlayer(i, j).get().equals(x)) ? "X " : "O ";
                else boardStr += "  ";
            }
            if (i != size() - 1) {
                boardStr += "\n---";
                for(int k = 1; k < board.length; k++)boardStr +="----";
            }
            boardStr += "\n";
        }
        return boardStr;
    }

    public String toString()
    {
        //// TODO: ttoggweiler 1/24/17 convert board to string value
        throw new NotImplementedException();
    }

    /**
     * Precondition check before attempting a move
     * checks player, coordinats, space conflicts
     * @param move
     */
    private void validateMove(Move move)
    {
        if (getState().equals(BoardState.FULL))
            throw new IllegalMoveException("Board is in a FULL state, no more moves may be played");
        if (isOccupied(move.getMove()))
            throw new IllegalMoveException(Arrays.toString(move.getMove()) + " space is already occupied");
        if (move.getPlayer() == null) throw new NullPointerException("A player value must set when making a move");
        if (move.getMove().length > 2)
            throw new IllegalArgumentException("A moves coordinates must only contain a row and column when making a move");
        isInBounds(move.getMove()[0], move.getMove()[1]);
    }

    /**
     * Checks if the provided row and column are in the bounds of the board
     * @param row vertical position
     * @param col horizontal position
     * @throws IndexOutOfBoundsException when the provided coordinates are not inside the board
     */
    private void isInBounds(int row, int col) throws IndexOutOfBoundsException
    {
        if (row < 0 || row > board.length - 1 || col < 0 || col > board.length - 1)
            throw new IndexOutOfBoundsException("(" + row + "," + col + ") is not in the boards bounds");
    }

    /**
     * Creates a Player[][] null board
     * @param size the dimensions of the board, minimum value of 3 is required
     * @return Null Player[][] matrix
     */
    private static BasePlayer[][] createBoard(int size)
    {
        if (size < 3) throw new IllegalArgumentException("Board size has a minimum value of 3");
        BasePlayer[][] newBoard = new BasePlayer[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                newBoard[i][j] = null;
        return newBoard;
    }
}
