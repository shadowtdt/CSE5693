package com.ttoggweiler.cse5693.board;

import com.ttoggweiler.cse5693.player.BasePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Traditional Tick Tack Toe board, 3x3 box
 */
public class ThreeByThreeBoard extends BaseBoard
{
    private BasePlayer[][] board = {
            {null,null,null},// 0,0 | 0,1 | 0,2
                             // ---------------
            {null,null,null},// 1,0 | 1,1 | 1,2
                             // ---------------
            {null,null,null} // 2,0 | 2,1 | 2,2
    };
    private boolean isMinMoveThresholdMet = false;
    private BasePlayer[] winningMoves = new BasePlayer[board.length];



    @Override
    public BoardState getState()
    {
        return null;
    }

    @Override
    public Optional<BasePlayer> findWinner()
    {
        //set state
        return null;
    }

    @Override
    boolean validateMove(Move move)
    {
        if(move)
        return false;
    }

    @Override
    void submitMove(Move move)
    {

        findWinner();
    }

    /* victory checking */

    /**
     * Reduce winning search space by first checking if there have been enough moves for a winner
     * @return
     */
    private boolean checkMinMoveThreshold()
    {
        if (!isMinMoveThresholdMet) {
            isMinMoveThresholdMet = ((board.length * 2) - 1) >= getCurrentMoveIndex();
        }
        return isMinMoveThresholdMet;
    }

    /**
     * Checks the horizontal of the last move played for a winning player
     * @return Optional winning player if one is found
     */
    private boolean checkLastMoveHorizontal()
    {
        Move lastMove = findLastMove().orElseThrow(() ->
                new IllegalStateException("Horizontal check should always be preceded by the Min-Move-Threshold check."));
        BasePlayer[] horizontalValues = new BasePlayer[board.length];

        int row = lastMove.getMove()[0];

        isSequenceMatching(row,0, row,board.length);
        for(int i = 0; i <= board.length; i++){
            horizontalValues[i] = board[row][i];
            if( horizontalValues[i] == null || !horizontalValues[i].getId().equals(lastMove.getId()))
                return Optional.empty(); //Quick fail if any value is null or the player does not match the previous move
        }
        return Optional.of(horizontalValues[0]);
    }

    /**
     * Checks the vertical of the last move played for a winning player
     * @return Optional winning player if one is found
     */
    private Optional<BasePlayer> checkLastMoveVertical()
    {
        Move lastMove = findLastMove().orElseThrow(() ->
                new IllegalStateException("Vertical check should always be preceded by the Min-Move-Threshold check."));
        BasePlayer[] verticalValues = new BasePlayer[board.length];

        int col = lastMove.getMove()[1];

        for(int i = 0; i <= board.length; i++){
            verticalValues[i] = board[i][col];
            if( verticalValues[i] == null || !verticalValues[i].getId().equals(lastMove.getId()))
                return Optional.empty(); //Quick fail if any value is null or the player does not match the previous move
        }
        return Optional.of(verticalValues[0]:
    }

    /**
     * Checks the diagonal of the last move played for a winning player
     * @return Optional winning player if one is found
     */
    private Optional<BasePlayer> checkLastMoveDiagonal()
    {
        Move lastMove = findLastMove().orElseThrow(() ->
                new IllegalStateException("Diagonal check should always be preceded by the Min-Move-Threshold check."));
        BasePlayer[] diagonalValues = new BasePlayer[board.length];

        int col = lastMove.getMove()[1];
        int row = lastMove.getMove()[0];

        // Quick fail if the last move was not on a diagonal
        if(col-row == 0) // 0,0 -> N,N diagonal
        {
            for(int i = 0; i <= board.length; i++){
                diagonalValues[i] = board[i][i];
                if( diagonalValues[i] == null || !diagonalValues[i].getId().equals(lastMove.getId()))
                    return Optional.empty(); //Quick fail if any value is null or the player does not match the previous move
            }
        }
        else if(row+col == board.length) // 0,N -> N,0 diagonal
        {
            for(int i = 0; i <= board.length; i++){
                diagonalValues[i] = board[i][board.length-i];
                if( diagonalValues[i] == null || !diagonalValues[i].getId().equals(lastMove.getId()))
                    return Optional.empty(); //Quick fail if any value is null or the player does not match the previous move
            }
        }
        else
            return Optional.empty();

        for(int i = 0; i <= board.length; i++){
            diagonalValues[i] = board[i][col];
            if( diagonalValues[i] == null) return Optional.empty(); //Quick fail if any value is null
        }
        return Optional.of(diagonalValues[0]);
    }

    private Optional<List<Move>> isSequenceMatching(int r0, int c0, int r1, int c1)
    {
        if(r0 < 0 || r0 > board.length-1 || c0 < 0 || c0 > board.length-1 ||
                r1 < 0 || r1 > board.length-1 || c1 < 0 || c1 > board.length-1)
            throw new IndexOutOfBoundsException("Sequence is not in the boards bounds");

        int rowDelta = Math.abs(r0 - r1);
        int colDelta = Math.abs(c0 - c1);
        boolean zeroDeltaExists = (rowDelta == 0 || colDelta == 0);
        // if both row and column have deltas, than the slope must be 1 for a straight line
        if(!zeroDeltaExists && (rowDelta + 0.0f / colDelta) != 1.0)
            throw new IllegalArgumentException("Sequence is not a valid line");

        List<Move> moveSequence = new ArrayList<>();
        BasePlayer playerToMatch = null;
        int r = r0;
        int c = c0;
        float sequenceLength = (zeroDeltaExists)? (rowDelta+colDelta) : rowDelta;//because slope must be one, the deltas wth be the same
        for(int i = 0; i < sequenceLength; i++){
            Optional<Move> oMove = findMove(r,c);
            r = (r0 < r1)? r+1 : r-1;// Move the index based on the slope
            c = (c0 < c1)? c+1 : c-1;

            if(!oMove.isPresent())
                return Optional.empty(); // Quick fail if space is not occupied
            else if (playerToMatch == null)
                playerToMatch = oMove.get().getPlayer(); // init player to match sequence
            else if (! BasePlayer.areMatching(playerToMatch,oMove.get().getPlayer()))
                return Optional.empty();// Quick fail on miss matched players
            else moveSequence.add(oMove.get()); // occupied && matching, add to sequence
        }
        moveSequence.sort(Comparator.comparingInt((Move :: getGameMoveIndex))); // sort based on the move index
        return Optional.of(moveSequence);
    }

    private boolean validPartialWinningMove(Move lastMove, Move partial)
    {
        return (partial != null && lastMove.getPlayer().getId().equals(partial.getPlayer().getId()));
    }
}
