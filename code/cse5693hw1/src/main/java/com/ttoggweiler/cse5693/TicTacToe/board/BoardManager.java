package com.ttoggweiler.cse5693.TicTacToe.board;

import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Helper methods for working with N size boards for ticTacToe
 *
 */
public class BoardManager
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

    private UUID[][] board;
    private BoardState state;

    public BoardManager(UUID[][] board)
    {
        if(board == null) throw new NullPointerException("Null board provided ");
        else this.board = board.clone();

        for(int i = 0; i < size(); i++) {
            if (board[i].length != size())
                throw new IllegalArgumentException("Provided board is not a square, row and columns but be of equal length");
            for (int j = 0; j < size(); j++)
                if(board[i][j] != null) state = BoardState.INTERMEDIATE;
        }
        getState();
    }

    public BoardManager(int size)
    {
        this.board = createBoard(size);
        state = BoardState.EMPTY;
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
     * Gets the current state of the board, {@link BoardManager.BoardState}
     * @return the current state of the board
     */
    public BoardState getState()
    {
        if(!state.equals(BoardState.INTERMEDIATE))return state; // Constructors will always init, safe to assume

        for(int i = 0; i < size(); i++)
            for(int j = 0; j < size(); j++)
                if(board[i][j] == null)
                    return state;
        state = BoardState.FULL;
        return state;
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
     * Returns a clone of the current board
     * @return Player[][] array of the current board state
     */
    public UUID[][] getCurrentBoard()
    {
        UUID[][] copy = new UUID[size()][size()];
        for(int i = 0; i < size(); i++)
            for(int j = 0; j < size(); j++)
                copy[i][j] = board[i][j];

        return copy;
    }

    /**
     * Finds the player currently occupying the coordinates, empty optional otherwise
     * @param coordinates row and column where to look for player
     * @return if occupied, player occupant, empty optional otherwise
     */
    public Optional<UUID> findPlayer(int... coordinates)
    {
        if (coordinates.length != 2)
            throw new IllegalArgumentException("Invalid number of dimensions for coordinates " + Arrays.toString(coordinates));
        isInBounds(coordinates[0], coordinates[1]);
        return Optional.ofNullable(board[coordinates[0]][coordinates[1]]);
    }

    /**
     * Determines if the line, defined by the x0,y0, x1,y1 pairs are occupied by the same player
     * @param r0 Starting row
     * @param c0 Starting column
     * @param r1 Ending row
     * @param c1 Ending column
     * @param allowEmpty allow empty spaces in sequences
     * @return MoveIndex sorted list of moves that make up a matching sequence, empty optional otherwise
     *
     */
    // TODO: ttoggweiler 1/26/17 update
    public Optional<UUID> findMatchingInSequence(int r0, int c0, int r1, int c1, boolean allowEmpty)
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

        UUID playerToMatch = null;
        int r = r0;
        int c = c0;
        //because slope must be one, the deltas wth be the same, rowDelta vs colDelta
        float sequenceLength = (zeroDeltaExists) ? (rowDelta + colDelta) : rowDelta;
        for (int i = 0; i <= sequenceLength; i++) {
            Optional<UUID> oPlayer = findPlayer(r, c);
            if(rowDelta != 0)r = (r0 < r1) ? r + 1 : r - 1;// Move the index based on the slope
            if(colDelta != 0)c = (c0 < c1) ? c + 1 : c - 1;

            if (!oPlayer.isPresent())
                if(allowEmpty)break; // allow unoccupied spaces
                else return Optional.empty(); // Quick fail if space is not occupied
            else if (playerToMatch == null)
                playerToMatch = oPlayer.get(); // init player to match sequence
            else if (!playerToMatch.equals(oPlayer.get()))
                return Optional.empty();// Quick fail on miss matched players
            // occupant matches
        }
        return Optional.ofNullable(playerToMatch);
    }

    public Optional<UUID> findWinner()
    {
        int length = size() - 1;
        for(int i = 0; i <= length; i++){
            // Check horizontals
            if(findMatchingInSequence(i,0,i,length ,false).isPresent())
                return Optional.of(board[i][0]);
            // Check verticals
            if(findMatchingInSequence(0,i,length,i,false).isPresent())
                return Optional.of(board[0][i]);
        }

        // Check 0,0 -> N,N
        if(findMatchingInSequence(0,0,length,length ,false).isPresent())
            return Optional.of(board[0][0]);
        // Check 0,N -> N,0
        if(findMatchingInSequence(0,length,length,0,false).isPresent())
            return Optional.of(board[0][length]);

        return Optional.empty();
    }

    public String getPrettyBoardString(UUID firstPlayer)
    {
        String boardStr = "\n";
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                boardStr += (j > 0) ? "| " : "";
                if (findPlayer(i, j).isPresent()) {
                    if(firstPlayer == null)firstPlayer = findPlayer(i, j).get();
                        boardStr += (findPlayer(i, j).get().equals(firstPlayer)) ? "X " : "O ";
                }
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
     * Checks if the provided row and column are in the bounds of the board
     * @param row vertical position
     * @param col horizontal position
     * @throws IndexOutOfBoundsException when the provided coordinates are not inside the board
     */
    public void isInBounds(int row, int col) throws IndexOutOfBoundsException
    {
        if (row < 0 || row > board.length - 1
                || col < 0 || col > board.length - 1)
            throw new IndexOutOfBoundsException("(" + row + "," + col + ") is not in the boards bounds");
    }

    /**
     * Creates a Player[][] null board
     * @param size the dimensions of the board, minimum value of 3 is required
     * @return Null Player[][] matrix
     */
    public static UUID[][] createBoard(int size)
    {
        if (size < 3) throw new IllegalArgumentException("MoveManager size has a minimum value of 3");
        UUID[][] newBoard = new UUID[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                newBoard[i][j] = null;
        return newBoard;
    }

    /**
     * // TODO: ttoggweiler 1/26/17
     * @param player
     * @param row
     * @param col
     * @return
     */
    boolean occupySpace(UUID player, int row, int col)
    {
        if(player == null) throw new NullPointerException("Null player cannot occupy a space");
        if(board[row][col] != null)return false;
        else board[row][col] = player;
        if(state.equals(BoardState.EMPTY))state=BoardState.INTERMEDIATE;
        return true;
    }

}
